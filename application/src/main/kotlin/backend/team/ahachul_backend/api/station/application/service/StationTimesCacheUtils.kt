package backend.team.ahachul_backend.api.station.application.service

import backend.team.ahachul_backend.api.station.adapter.`in`.dto.GetStationTimesDto
import backend.team.ahachul_backend.api.station.application.port.`in`.dto.GetStationTimesCacheCommand
import backend.team.ahachul_backend.common.client.RedisClient
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class StationTimesCacheUtils(
    private val redisClient: RedisClient,
    private val objectMapper: ObjectMapper,
) {

    fun setStationTimesCache(command: GetStationTimesCacheCommand, value: List<GetStationTimesDto.StationTimes>) {
        val key = createKey(command)
        val ttlSeconds = if (value.isEmpty()) {
            EMPTY_STATION_TIMES_REDIS_EXPIRE_SEC
        } else {
            STATION_TIMES_REDIS_EXPIRE_SEC
        }
        redisClient.set(
            key, value,
            ttlSeconds,
            TimeUnit.SECONDS
        )
    }

    fun getStationTimesByCache(command: GetStationTimesCacheCommand): List<GetStationTimesDto.StationTimes>? {
        val key = createKey(command)
        val cachedData = redisClient.get(key)

        return cachedData?.let {
            val typeRef = object : TypeReference<List<GetStationTimesDto.StationTimes>>() {}
            objectMapper.readValue(it, typeRef)
        }
    }

    private fun createKey(command: GetStationTimesCacheCommand): String {
        return STATION_TIMES_REDIS_PREFIX +
            "${command.stationCode}-" +
            "${command.upDownType.publicCode}-" +
            "${command.stationTimeWeekType.publicCode}"
    }

    companion object {
        const val STATION_TIMES_REDIS_PREFIX = "STATION_TIMES:"
        const val STATION_TIMES_REDIS_EXPIRE_SEC = 86400L
        const val EMPTY_STATION_TIMES_REDIS_EXPIRE_SEC = 600L
    }
}
