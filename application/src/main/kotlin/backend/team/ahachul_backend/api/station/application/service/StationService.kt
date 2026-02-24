package backend.team.ahachul_backend.api.station.application.service

import backend.team.ahachul_backend.api.common.application.port.out.SubwayLineStationReader
import backend.team.ahachul_backend.api.station.adapter.`in`.dto.GetStationTimesDto
import backend.team.ahachul_backend.api.station.application.port.`in`.StationUseCase
import backend.team.ahachul_backend.api.station.application.port.`in`.dto.GetStationLastTrainRiskCommand
import backend.team.ahachul_backend.api.station.application.port.`in`.dto.GetStationQuickExitCommand
import backend.team.ahachul_backend.api.station.application.port.`in`.dto.GetStationTimesCommand
import backend.team.ahachul_backend.api.station.application.port.`in`.dto.GetStationTimesSummaryCommand
import backend.team.ahachul_backend.api.train.domain.model.UpDownType
import backend.team.ahachul_backend.common.client.SeoulTrainClient
import backend.team.ahachul_backend.common.config.CircuitBreakerConfig.Companion.CUSTOM_CIRCUIT_BREAKER
import backend.team.ahachul_backend.common.exception.BusinessException
import backend.team.ahachul_backend.common.exception.CommonException
import backend.team.ahachul_backend.common.logging.Logger
import backend.team.ahachul_backend.common.response.ResponseCode
import io.github.resilience4j.circuitbreaker.CallNotPermittedException
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import org.springframework.data.redis.RedisConnectionFailureException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

@Service
@Transactional(readOnly = true)
class StationService(
    private val subwayLineStationReader: SubwayLineStationReader,
    private val stationTimesCacheUtils: StationTimesCacheUtils,
    private val seoulTrainClient: SeoulTrainClient,
): StationUseCase {

    private val logger: Logger = Logger(javaClass)

    @CircuitBreaker(name = CUSTOM_CIRCUIT_BREAKER, fallbackMethod = "fallbackOnExternalStationTimesApiGet")
    override fun getStationTimes(command: GetStationTimesCommand): GetStationTimesDto.Response {
        return GetStationTimesDto.Response(loadStationTimes(command))
    }

    @CircuitBreaker(name = CUSTOM_CIRCUIT_BREAKER, fallbackMethod = "fallbackOnExternalStationTimesSummaryApiGet")
    override fun getStationTimesSummary(command: GetStationTimesSummaryCommand): GetStationTimesDto.SummaryResponse {
        val summaries = UpDownType.values().map { upDownType ->
            val stationTimes = loadStationTimesForSummary(
                GetStationTimesCommand(
                    stationId = command.stationId,
                    subwayLineId = command.subwayLineId,
                    upDownType = upDownType,
                    stationTimeWeekType = command.stationTimeWeekType,
                )
            )
            val sortedByDepartureTime = stationTimes.sortedBy { it.departureTime }
            val firstTrain = sortedByDepartureTime.firstOrNull()
            val lastTrain = sortedByDepartureTime.lastOrNull()

            GetStationTimesDto.UpDownSummary(
                upDownType = upDownType,
                firstDepartureTime = firstTrain?.departureTime,
                lastDepartureTime = lastTrain?.departureTime,
                firstDestinationStationName = firstTrain?.arrivalStationName,
                lastDestinationStationName = lastTrain?.arrivalStationName,
            )
        }

        return GetStationTimesDto.SummaryResponse(
            stationTimeWeekType = command.stationTimeWeekType,
            summaries = summaries,
        )
    }

    private fun loadStationTimesForSummary(command: GetStationTimesCommand): List<GetStationTimesDto.StationTimes> {
        return try {
            loadStationTimes(command)
        } catch (e: BusinessException) {
            if (e.code == ResponseCode.FAILED_STATION_TIMES_API ||
                e.code == ResponseCode.INVALID_STATION_TIMES_API_RESPONSE
            ) {
                logger.error("station times summary fallback to empty list", e)
                emptyList()
            } else {
                throw e
            }
        }
    }

    @CircuitBreaker(name = CUSTOM_CIRCUIT_BREAKER, fallbackMethod = "fallbackOnExternalStationTimesLastTrainRiskApiGet")
    override fun getLastTrainRisk(command: GetStationLastTrainRiskCommand): GetStationTimesDto.LastTrainRiskResponse {
        val stationTimes = loadStationTimesForLastTrainRisk(
            GetStationTimesCommand(
                stationId = command.stationId,
                subwayLineId = command.subwayLineId,
                upDownType = command.upDownType,
                stationTimeWeekType = command.stationTimeWeekType,
            )
        )

        val lastDepartureTime = stationTimes
            .maxByOrNull { it.departureTime }
            ?.departureTime

        val calculated = StationLastTrainRiskCalculator.calculate(
            nowAt = OffsetDateTime.now(),
            lastDepartureTime = lastDepartureTime,
            walkingMinutes = command.walkingMinutes,
        )

        return GetStationTimesDto.LastTrainRiskResponse(
            stationTimeWeekType = command.stationTimeWeekType,
            upDownType = command.upDownType,
            walkingMinutes = command.walkingMinutes,
            nowAt = calculated.nowAt.toString(),
            lastDepartureTime = lastDepartureTime,
            minutesToLastTrain = calculated.minutesToLastTrain,
            isLastTrainRisk = calculated.isLastTrainRisk,
            riskLevel = calculated.riskLevel,
            message = calculated.message,
        )
    }

    private fun loadStationTimesForLastTrainRisk(command: GetStationTimesCommand): List<GetStationTimesDto.StationTimes> {
        return try {
            loadStationTimes(command)
        } catch (e: BusinessException) {
            if (e.code == ResponseCode.FAILED_STATION_TIMES_API ||
                e.code == ResponseCode.INVALID_STATION_TIMES_API_RESPONSE
            ) {
                logger.error("station times last-train-risk fallback to empty list", e)
                emptyList()
            } else {
                throw e
            }
        }
    }

    override fun getQuickExits(command: GetStationQuickExitCommand): GetStationTimesDto.QuickExitResponse {
        val recommendations = StationQuickExitRecommendationCalculator.recommend(
            stationId = command.stationId,
            subwayLineId = command.subwayLineId,
            upDownType = command.upDownType,
        )

        return GetStationTimesDto.QuickExitResponse(
            stationId = command.stationId,
            subwayLineId = command.subwayLineId,
            upDownType = command.upDownType,
            recommendations = recommendations,
        )
    }

    private fun loadStationTimes(command: GetStationTimesCommand): List<GetStationTimesDto.StationTimes> {
        val subwayLineStation = subwayLineStationReader.findBySubwayLineIdAndStationId(command.subwayLineId, command.stationId)
        val stationCode = subwayLineStation.stationCode ?: throw BusinessException(ResponseCode.NOT_EXIST_PUBLIC_STATION_CODE)

        val cacheCommand = command.toCacheCommand(stationCode)
        stationTimesCacheUtils.getStationTimesByCache(cacheCommand)?.let {
            return it
        }

        val response = seoulTrainClient.getStationTimesByApi(command.toRequest(stationCode))
        if (response.isFail()) {
            throw BusinessException(ResponseCode.INVALID_STATION_TIMES_API_RESPONSE)
        }

        val stationTimes = response.toStationTimes()
        stationTimesCacheUtils.setStationTimesCache(cacheCommand, stationTimes)
        return stationTimes
    }

    /**
     * Redis 통신 오류에 대한 FallBack 메서드
     */
    fun fallbackOnExternalStationTimesApiGet(
        command: GetStationTimesCommand, e: RedisConnectionFailureException
    ): GetStationTimesDto.Response {
        logger.error("can't connect to redis server")
        throw CommonException(ResponseCode.FAILED_TO_CONNECT_TO_REDIS, e)
    }

    /**
     * 열차 도착 정보 API 오류에 대한 FallBack 메서드
     */
    fun fallbackOnExternalStationTimesApiGet(
        command: GetStationTimesCommand, e : CallNotPermittedException
    ): GetStationTimesDto.Response {
        logger.error("circuit breaker opened for external station times api")
        throw CommonException(ResponseCode.FAILED_TO_GET_STATION_TIMES, e)
    }

    /**
     * Redis 통신 오류에 대한 첫차/막차 요약 FallBack 메서드
     */
    fun fallbackOnExternalStationTimesSummaryApiGet(
        command: GetStationTimesSummaryCommand, e: RedisConnectionFailureException
    ): GetStationTimesDto.SummaryResponse {
        logger.error("can't connect to redis server")
        throw CommonException(ResponseCode.FAILED_TO_CONNECT_TO_REDIS, e)
    }

    /**
     * 열차 도착 정보 API 오류에 대한 첫차/막차 요약 FallBack 메서드
     */
    fun fallbackOnExternalStationTimesSummaryApiGet(
        command: GetStationTimesSummaryCommand, e : CallNotPermittedException
    ): GetStationTimesDto.SummaryResponse {
        logger.error("circuit breaker opened for external station times summary api")
        throw CommonException(ResponseCode.FAILED_TO_GET_STATION_TIMES, e)
    }

    /**
     * Redis 통신 오류에 대한 막차 리스크 FallBack 메서드
     */
    fun fallbackOnExternalStationTimesLastTrainRiskApiGet(
        command: GetStationLastTrainRiskCommand, e: RedisConnectionFailureException
    ): GetStationTimesDto.LastTrainRiskResponse {
        logger.error("can't connect to redis server")
        throw CommonException(ResponseCode.FAILED_TO_CONNECT_TO_REDIS, e)
    }

    /**
     * 열차 도착 정보 API 오류에 대한 막차 리스크 FallBack 메서드
     */
    fun fallbackOnExternalStationTimesLastTrainRiskApiGet(
        command: GetStationLastTrainRiskCommand, e : CallNotPermittedException
    ): GetStationTimesDto.LastTrainRiskResponse {
        logger.error("circuit breaker opened for external station times last-train-risk api")
        throw CommonException(ResponseCode.FAILED_TO_GET_STATION_TIMES, e)
    }
}
