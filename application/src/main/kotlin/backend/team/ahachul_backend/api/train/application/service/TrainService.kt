package backend.team.ahachul_backend.api.train.application.service

import backend.team.ahachul_backend.api.train.adapter.`in`.dto.GetCongestionDto
import backend.team.ahachul_backend.api.train.adapter.`in`.dto.GetTrainDto
import backend.team.ahachul_backend.api.train.adapter.`in`.dto.GetTrainRealTimesDto
import backend.team.ahachul_backend.api.train.application.port.`in`.TrainUseCase
import backend.team.ahachul_backend.api.train.application.port.`in`.command.GetCongestionCommand
import backend.team.ahachul_backend.api.train.application.port.out.TrainReader
import backend.team.ahachul_backend.api.train.domain.entity.TrainEntity
import backend.team.ahachul_backend.api.train.domain.model.UpDownType
import backend.team.ahachul_backend.common.client.SeoulTrainClient
import backend.team.ahachul_backend.common.client.TrainCongestionClient
import backend.team.ahachul_backend.common.client.dto.TrainCongestionDto
import backend.team.ahachul_backend.common.config.CircuitBreakerConfig.Companion.CUSTOM_CIRCUIT_BREAKER
import backend.team.ahachul_backend.common.dto.RealtimeArrivalListDTO
import backend.team.ahachul_backend.common.exception.AdapterException
import backend.team.ahachul_backend.common.exception.BusinessException
import backend.team.ahachul_backend.common.exception.CommonException
import backend.team.ahachul_backend.common.logging.Logger
import backend.team.ahachul_backend.common.response.ResponseCode
import io.github.resilience4j.circuitbreaker.CallNotPermittedException
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import org.redisson.api.RedissonClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.TimeUnit

@Service
@Transactional(readOnly = true)
class TrainService(
    private val trainReader: TrainReader,
    private val seoulTrainClient: SeoulTrainClient,
    private val trainQueryService: TrainQueryService,
    private val trainCacheUtils: TrainCacheUtils,
    private val congestionCacheUtils: CongestionCacheUtils,
    private val trainCongestionClient: TrainCongestionClient,
    private val redissonClient: RedissonClient,
): TrainUseCase {

    @Lazy
    @Autowired
    private lateinit var self: TrainService

    private val logger: Logger = Logger(javaClass)

    /**
     * 특정 열차에 대한 지하철 노선 정보를 조회하는 메서드
     */
    override fun getTrain(trainNo: String): GetTrainDto.Response {
        val (prefixTrainNo, location, organizationTrainNo) = decompositionTrainNo(trainNo)
        val train: TrainEntity
        try {
             train = trainReader.getTrain(prefixTrainNo)
        } catch (e: AdapterException) {
            logger.info("prefixTrainNo is no matching train. prefixTrainNo : {}".format(prefixTrainNo))
            throw BusinessException(ResponseCode.INVALID_PREFIX_TRAIN_NO)
        }

        return GetTrainDto.Response.of(
            train = train,
            location = location,
            organizationTrainNo = organizationTrainNo,
        )
    }

    private fun decompositionTrainNo(trainNo: String): Triple<String, Int, String> {
        return Triple(
            trainNo.dropLast(3),
            trainNo[trainNo.length - 3].digitToInt(),
            trainNo.takeLast(2),
        )
    }

    /**
     * 실시간 열차 도착 정보를 조회하는 메서드
     * - DB 조회는 trainQueryService 에서 트랜잭션 내 처리 후 커넥션 반환
     * - 외부 API 호출은 트랜잭션 없이 수행
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    override fun getTrainRealTimes(stationId: Long, subwayLineId: Long, upDownType: UpDownType?): List<GetTrainRealTimesDto.TrainRealTime> {
        val (stationName, subwayLineIdentity, lockKey) = trainQueryService.getStationAndSubwayLine(stationId, subwayLineId)

        trainCacheUtils.getCache(subwayLineIdentity, stationId)?.let {
            logger.info("[cache hit] 응답 반환: lockKey=$lockKey")
            return it
        }

        val lock = redissonClient.getLock(lockKey)

        try {
            val acquired = lock.tryLock(10, 15, TimeUnit.SECONDS)

            if (!acquired) {
                logger.error("분산 락 획득 실패: lockKey=$lockKey")
                throw BusinessException(ResponseCode.LOCK_ACQUISITION_FAILED)
            }

            trainCacheUtils.getCache(subwayLineIdentity, stationId)?.let {
                logger.info("[cache miss] 이미 캐싱된 데이터로 인해 바로 락 해제 후 종료: lockKey=$lockKey")
                lock.unlock()
                return it
            }

            logger.info("[cache miss] 외부 열차 도착 정보 API 호출 시작 (분산 락 획득): lockKey=$lockKey")
            val result = self.requestTrainRealTimesAndSorting(stationName)
            result.forEach { (key, value) ->
                trainCacheUtils.setCache(key.toLong(), stationId, value)
            }

            val trainRealTimes = result.getOrElse(subwayLineIdentity.toString()) { emptyList() }
            return upDownType?.let { type ->
                trainRealTimes.filter { it.upDownType == type }.take(4)
            } ?: trainRealTimes
        } catch (e: InterruptedException) {
            logger.error("Lock 획득 중 인터럽트 발생", e)
            throw BusinessException(ResponseCode.LOCK_ACQUISITION_FAILED)
        } finally {
            if (lock.isHeldByCurrentThread) {
                lock.unlock()
                logger.info("[cache miss] 외부 열차 도착 정보 API 호출 완료 (분산 락 해제): lockKey=$lockKey")
            }
        }
    }

    @CircuitBreaker(name = CUSTOM_CIRCUIT_BREAKER, fallbackMethod = "fallbackOnRequestTrainRealTimes")
    fun requestTrainRealTimesAndSorting(
        stationName: String
    ): Map<String, List<GetTrainRealTimesDto.TrainRealTime>> {
        var startIndex = 1
        var endIndex = 5
        var totalSize = startIndex
        val totalTrainRealTimes = mutableListOf<RealtimeArrivalListDTO>()

        while (startIndex <= totalSize) {
            val trainRealTimesPublicData = seoulTrainClient.getTrainRealTimes(stationName, startIndex, endIndex)
            totalSize = trainRealTimesPublicData.errorMessage?.total ?: break
            trainRealTimesPublicData.realtimeArrivalList?.let { totalTrainRealTimes.addAll(it) }
            startIndex = endIndex + 1
            endIndex = startIndex + 4
        }

        if (totalTrainRealTimes.isEmpty()) {
            throw BusinessException(ResponseCode.NOT_EXIST_ARRIVAL_TRAIN)
        }

        return totalTrainRealTimes
            .groupBy { it.subwayId }
            .mapValues { generateTrainRealTimeByUpDnType(it.value) }
    }

    fun fallbackOnRequestTrainRealTimes(
        stationName: String, e: Exception
    ): Map<String, List<GetTrainRealTimesDto.TrainRealTime>> {
        logger.error("외부 열차 도착 API fallback: stationName=$stationName, cause=${e::class.simpleName}")
        throw when (e) {
            is CallNotPermittedException -> CommonException(ResponseCode.FAILED_TO_GET_TRAIN_INFO, e)
            else -> CommonException(ResponseCode.INTERNAL_SERVER_ERROR, e)
        }
    }

    private fun generateTrainRealTimeByUpDnType(trainRealTime: List<RealtimeArrivalListDTO>?): List<GetTrainRealTimesDto.TrainRealTime> {
        val total = mutableListOf<GetTrainRealTimesDto.TrainRealTime>()
        trainRealTime
            ?.groupBy { it.updnLine }
            ?.entries?.forEach { map ->
                val subIdx = if (map.value.size >= 2) 2 else 1

                val lis = map.value.map { dto ->
                        GetTrainRealTimesDto.TrainRealTime.of(dto, extractStationOrder(dto.arvlMsg2))
                    }.sortedWith(compareBy(
                        { it.currentTrainArrivalCode.priority },
                        { it.stationOrder }
                    )).subList(0, subIdx)

                total.addAll(lis)
            }
        return total
    }

    private fun extractStationOrder(destinationMessage: String): Int {
        return if (destinationMessage.startsWith("[")) {
            pattern.find(destinationMessage)!!.value.toInt().times(2)
        } else if (destinationMessage.contains("분")) {
            pattern.find(destinationMessage)!!.value.toInt()
        } else {
            Int.MAX_VALUE
        }
    }

    /**
     * 실시간 열차 혼잡도 정보를 조회하는 메서드
     * - DB 조회는 trainQueryService 에서 트랜잭션 내 처리 후 커넥션 반환
     * - 외부 API 호출은 트랜잭션 없이 수행
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    override fun getTrainCongestion(command: GetCongestionCommand): GetCongestionDto.Response {
        val subwayLineId = trainQueryService.getSubwayLineId(command.subwayLineId)
        val trainNo = command.trainNo

        congestionCacheUtils.getCache(subwayLineId, trainNo)?.let {
            logger.info("[cache hit] 혼잡도 응답 반환: subwayLineId=$subwayLineId, trainNo=$trainNo")
            return it
        }

        val lockKey = "congestion:$subwayLineId-$trainNo"
        val lock = redissonClient.getLock(lockKey)

        try {
            val acquired = lock.tryLock(10, 15, TimeUnit.SECONDS)
            if (!acquired) {
                logger.error("분산 락 획득 실패: lockKey=$lockKey")
                throw BusinessException(ResponseCode.LOCK_ACQUISITION_FAILED)
            }

            congestionCacheUtils.getCache(subwayLineId, trainNo)?.let {
                logger.info("[cache miss] 이미 캐싱된 데이터로 인해 바로 락 해제 후 종료: lockKey=$lockKey")
                lock.unlock()
                return it
            }

            logger.info("[cache miss] 외부 열차 혼잡도 API 호출 시작 (분산 락 획득): lockKey=$lockKey")
            return self.fetchCongestion(subwayLineId, trainNo)
        } catch (e: InterruptedException) {
            logger.error("Lock 획득 중 인터럽트 발생", e)
            throw BusinessException(ResponseCode.LOCK_ACQUISITION_FAILED)
        } finally {
            if (lock.isHeldByCurrentThread) {
                lock.unlock()
                logger.info("[cache miss] 외부 열차 혼잡도 API 처리 완료 (분산 락 해제): lockKey=$lockKey")
            }
        }
    }

    @CircuitBreaker(name = CUSTOM_CIRCUIT_BREAKER, fallbackMethod = "fallbackOnFetchCongestion")
    fun fetchCongestion(subwayLineId: Long, trainNo: String): GetCongestionDto.Response {
        val correctTrainNum = getCorrectTrainNum(subwayLineId, trainNo)
        val response = trainCongestionClient.getCongestions(subwayLineId, correctTrainNum.toInt())

        val trainCongestion = response.data!!
        val congestions = mapCongestionDto(response.success, trainCongestion)
        val congestionDto = GetCongestionDto.Response.from(correctTrainNum, congestions)

        congestionCacheUtils.setCache(subwayLineId, correctTrainNum, congestionDto)
        return congestionDto
    }

    fun fallbackOnFetchCongestion(
        subwayLineId: Long, trainNo: String, e: Exception
    ): GetCongestionDto.Response {
        logger.error("외부 혼잡도 API fallback: subwayLineId=$subwayLineId, cause=${e::class.simpleName}")
        throw when (e) {
            is CallNotPermittedException -> CommonException(ResponseCode.FAILED_TO_GET_TRAIN_INFO, e)
            else -> CommonException(ResponseCode.INTERNAL_SERVER_ERROR, e)
        }
    }

    private fun getCorrectTrainNum(subwayLineId: Long, trainNo: String): String {
        return when (trainNo[0] != subwayLineId.toString()[0]) {
            false -> "${subwayLineId}${trainNo.substring(1, trainNo.length)}"
            true -> trainNo
        }
    }

    private fun mapCongestionDto(
        success: Boolean, trainCongestion: TrainCongestionDto.Train
    ): List<GetCongestionDto.Section> {
        if (success) {
            val congestion = trainCongestion.congestionResult.congestionCar
            val congestions = congestion.trim().split(DELIMITER)
            return congestions.map { it.toInt() }.mapIndexed { idx, it ->
                GetCongestionDto.Section.from(idx, it)
            }
        }
        return emptyList()
    }

    companion object {
        const val DELIMITER = "|"
        val pattern = "\\d+".toRegex()
    }
}
