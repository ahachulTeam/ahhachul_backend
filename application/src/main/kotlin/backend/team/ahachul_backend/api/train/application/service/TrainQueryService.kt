package backend.team.ahachul_backend.api.train.application.service

import backend.team.ahachul_backend.api.common.application.port.out.StationReader
import backend.team.ahachul_backend.common.config.CircuitBreakerConfig.Companion.CUSTOM_CIRCUIT_BREAKER
import backend.team.ahachul_backend.common.exception.BusinessException
import backend.team.ahachul_backend.common.logging.Logger
import backend.team.ahachul_backend.common.persistence.SubwayLineReader
import backend.team.ahachul_backend.common.response.ResponseCode
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class TrainQueryService(
    private val stationLineReader: StationReader,
    private val subwayLineReader: SubwayLineReader,
) {

    private val logger = Logger(javaClass)

    data class StationSubwayInfo(
        val stationName: String,
        val subwayLineIdentity: Long,
        val lockKey: String,
    )

    @CircuitBreaker(name = CUSTOM_CIRCUIT_BREAKER, fallbackMethod = "fallbackOnGetStationAndSubwayLine")
    fun getStationAndSubwayLine(stationId: Long, subwayLineId: Long): StationSubwayInfo {
        val station = stationLineReader.getById(stationId)
        val subwayLine = subwayLineReader.getById(subwayLineId)
        return StationSubwayInfo(
            stationName = station.name,
            subwayLineIdentity = subwayLine.identity,
            lockKey = "${subwayLine.identity}-${stationId}",
        )
    }

    private fun fallbackOnGetStationAndSubwayLine(stationId: Long, subwayLineId: Long, e: Exception): StationSubwayInfo {
        logger.error("DB 조회 실패: stationId=$stationId, subwayLineId=$subwayLineId, cause=${e::class.simpleName}")
        throw BusinessException(ResponseCode.FAILED_TO_GET_TRAIN_INFO)
    }

    @CircuitBreaker(name = CUSTOM_CIRCUIT_BREAKER, fallbackMethod = "fallbackOnGetSubwayLineId")
    fun getSubwayLineId(subwayLineId: Long): Long {
        return subwayLineReader.getById(subwayLineId).id
    }

    private fun fallbackOnGetSubwayLineId(subwayLineId: Long, e: Exception): Long {
        logger.error("DB 조회 실패: subwayLineId=$subwayLineId, cause=${e::class.simpleName}")
        throw BusinessException(ResponseCode.FAILED_TO_GET_TRAIN_INFO)
    }
}
