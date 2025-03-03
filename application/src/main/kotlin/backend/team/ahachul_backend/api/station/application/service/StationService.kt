package backend.team.ahachul_backend.api.station.application.service

import backend.team.ahachul_backend.api.common.application.port.out.SubwayLineStationReader
import backend.team.ahachul_backend.api.station.adapter.`in`.dto.GetStationTimesDto
import backend.team.ahachul_backend.api.station.application.port.`in`.StationUseCase
import backend.team.ahachul_backend.api.station.application.port.`in`.dto.GetStationTimesCommand
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
        val subwayLineStation = subwayLineStationReader.findBySubwayLineIdAndStationId(command.subwayLineId, command.stationId)
        val stationCode = subwayLineStation.stationCode ?: throw BusinessException(ResponseCode.NOT_EXIST_PUBLIC_STATION_CODE)

        val cacheCommand = command.toCacheCommand(stationCode)
        stationTimesCacheUtils.getStationTimesByCache(cacheCommand)?.let {
            return GetStationTimesDto.Response(it)
        }

        val response = seoulTrainClient.getStationTimesByApi(command.toRequest(stationCode))
        if (response.isFail()) {
            throw BusinessException(ResponseCode.INVALID_STATION_TIMES_API_RESPONSE)
        }

        val stationTimes = response.toStationTimes()
        stationTimesCacheUtils.setStationTimesCache(cacheCommand, stationTimes)

        return GetStationTimesDto.Response(stationTimes)
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
}