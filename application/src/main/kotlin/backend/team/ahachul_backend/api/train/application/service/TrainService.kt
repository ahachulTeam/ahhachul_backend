package backend.team.ahachul_backend.api.train.application.service

import backend.team.ahachul_backend.api.common.application.port.out.StationReader
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
import backend.team.ahachul_backend.common.persistence.SubwayLineReader
import backend.team.ahachul_backend.common.response.ResponseCode
import io.github.resilience4j.circuitbreaker.CallNotPermittedException
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import org.springframework.data.redis.RedisConnectionFailureException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class TrainService(
    private val trainReader: TrainReader,
    private val stationLineReader: StationReader,
    private val subwayLineReader: SubwayLineReader,

    private val seoulTrainClient: SeoulTrainClient,

    private val trainCacheUtils: TrainCacheUtils,
    private val congestionCacheUtils: CongestionCacheUtils,
    private val trainCongestionClient: TrainCongestionClient,
): TrainUseCase {

    private val logger: Logger = Logger(javaClass)

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
     * 외부 열차 조회 API를 호출하는 메서드
     */
    @CircuitBreaker(name = CUSTOM_CIRCUIT_BREAKER, fallbackMethod = "fallbackOnExternalTrainApiGet")
    override fun getTrainRealTimes(stationId: Long, subwayLineId: Long, upDownType: UpDownType?): List<GetTrainRealTimesDto.TrainRealTime> {
        val station = stationLineReader.getById(stationId)
        val subwayLine = subwayLineReader.getById(subwayLineId)
        val subwayLineIdentity = subwayLine.identity

        trainCacheUtils.getCache(subwayLineIdentity, stationId)?.let { return it }

        val trainRealTimeMap = requestTrainRealTimesAndSorting(station.name)
        trainRealTimeMap.forEach {
            trainCacheUtils.setCache(it.key.toLong(), stationId, it.value)
        }

        val trainRealTimes = trainRealTimeMap.getOrElse(subwayLineIdentity.toString()) { emptyList() }

        return upDownType?.let {
                type ->  trainRealTimes.filter { it.upDownType == type }.take(4)
        } ?: trainRealTimes
    }

    private fun requestTrainRealTimesAndSorting(
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

    private fun generateTrainRealTimeByUpDnType(trainRealTime: List<RealtimeArrivalListDTO>?): List<GetTrainRealTimesDto.TrainRealTime> {
        val total = mutableListOf<GetTrainRealTimesDto.TrainRealTime>()
        trainRealTime
            ?.groupBy { it.updnLine }
            ?.entries?.forEach { map ->
                val subIdx = if (map.value.size >= 2) 2 else 1  // 상행, 하행 각각 최대 두개씩 반환

                val lis = map.value.map { dto ->
                        GetTrainRealTimesDto.TrainRealTime.of(dto, extractStationOrder(dto.arvlMsg2))
                    }.sortedWith( compareBy(
                        { it.currentTrainArrivalCode.priority },
                        { it.stationOrder }
                    )).subList(0, subIdx)

                total.addAll(lis)
            }
        return total
    }

    private fun extractStationOrder(destinationMessage: String): Int {
        // 도착 우선순위 추출
        return if (destinationMessage.startsWith("[")) {
            pattern.find(destinationMessage)!!.value.toInt().times(2)
        } else if (destinationMessage.contains("분")) {
            pattern.find(destinationMessage)!!.value.toInt()
        } else {
            Int.MAX_VALUE
        }
    }

    fun fallbackOnExternalTrainApiGet(
        stationId: Long, subwayLineId: Long, upDownType: UpDownType?, e: RedisConnectionFailureException
    ): List<GetTrainRealTimesDto.TrainRealTime> {
        logger.error("circuit breaker opened for redis server")
        throw CommonException(ResponseCode.FAILED_TO_CONNECT_TO_REDIS, e)
    }

    fun fallbackOnExternalTrainApiGet(
        stationId: Long, subwayLineId: Long, upDownType: UpDownType?, e : CallNotPermittedException
    ): List<GetTrainRealTimesDto.TrainRealTime> {
        logger.error("circuit breaker opened for external train api")
        throw CommonException(ResponseCode.FAILED_TO_GET_TRAIN_INFO, e)
    }

    /**
     * 혼잡도 API 호출
     */
    @CircuitBreaker(name = CUSTOM_CIRCUIT_BREAKER, fallbackMethod = "fallbackOnExternalCongestionApiGet")
    override fun getTrainCongestion(command: GetCongestionCommand): GetCongestionDto.Response {
        val subwayLineId = subwayLineReader.getById(command.subwayLineId).id
        val trainNo = command.trainNo

        try {
            congestionCacheUtils.getCache(subwayLineId, trainNo)?.let { return it }
        } catch (e: RedisConnectionFailureException) {
            throw CommonException(ResponseCode.FAILED_TO_CONNECT_TO_REDIS)
        }

        val correctTrainNum = getCorrectTrainNum(subwayLineId, trainNo)
        val response = trainCongestionClient.getCongestions(subwayLineId, correctTrainNum.toInt())
        val trainCongestion = response.data!!

        val congestions = mapCongestionDto(response.success, trainCongestion)
        val congestionDto = GetCongestionDto.Response.from(correctTrainNum, congestions)
        congestionCacheUtils.setCache(subwayLineId, correctTrainNum, congestionDto)
        return congestionDto
    }

    private fun getCorrectTrainNum(subwayLineId: Long, trainNo: String): String {
        // API 자체에서 발생하는 열차 번호 에러 수정
        return when (trainNo[0] != subwayLineId.toString()[0]) {
            false -> "${subwayLineId}${trainNo.substring(1, trainNo.length)}"
            true -> trainNo
        }
    }

    private fun mapCongestionDto(
        success: Boolean, trainCongestion: TrainCongestionDto.Train
    ): List<GetCongestionDto.Section>  {
        if (success) {
            val congestion = trainCongestion.congestionResult.congestionCar
            val congestions = congestion.trim().split(DELIMITER)
            val congestionIntList = congestions.map { it.toInt() }
            return congestionIntList.mapIndexed {
                    idx, it -> GetCongestionDto.Section.from(idx, it)
            }
        }
        return emptyList()
    }

    fun fallbackOnExternalCongestionApiGet(
        command: GetCongestionCommand, e: RedisConnectionFailureException
    ): GetCongestionDto.Response {
        logger.error("circuit breaker opened for redis server")
        throw CommonException(ResponseCode.FAILED_TO_CONNECT_TO_REDIS, e)
    }

    fun fallbackOnExternalCongestionApiGet(
        command: GetCongestionCommand, e : CallNotPermittedException
    ): GetCongestionDto.Response {
        logger.error("circuit breaker opened for external congestion api")
        throw CommonException(ResponseCode.FAILED_TO_GET_CONGESTION_INFO, e)
    }

    companion object {
        const val DELIMITER = "|"
        val pattern = "\\d+".toRegex()
    }
}
