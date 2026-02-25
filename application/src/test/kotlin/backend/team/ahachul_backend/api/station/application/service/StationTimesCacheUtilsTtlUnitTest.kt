package backend.team.ahachul_backend.api.station.application.service

import backend.team.ahachul_backend.api.station.adapter.`in`.dto.GetStationTimesDto
import backend.team.ahachul_backend.api.station.adapter.`in`.dto.StationTimeWeekType
import backend.team.ahachul_backend.api.station.application.port.`in`.dto.GetStationTimesCacheCommand
import backend.team.ahachul_backend.api.train.domain.model.TrainType
import backend.team.ahachul_backend.api.train.domain.model.UpDownType
import backend.team.ahachul_backend.common.client.RedisClient
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.util.concurrent.TimeUnit

class StationTimesCacheUtilsTtlUnitTest {

    private val redisClient: RedisClient = mock(RedisClient::class.java)
    private val stationTimesCacheUtils = StationTimesCacheUtils(redisClient, ObjectMapper())

    @Test
    @DisplayName("역 시간표가 비어있지 않으면 기본 TTL(24h)로 캐시한다.")
    fun setStationTimesCacheWithNormalTtl() {
        val command = GetStationTimesCacheCommand(
            stationCode = "001",
            upDownType = UpDownType.UP,
            stationTimeWeekType = StationTimeWeekType.WEEKDAY,
        )
        val value = listOf(
            GetStationTimesDto.StationTimes(
                arrivalTime = "06:30:00",
                departureTime = "06:30:30",
                arrivalStationName = "대화",
                departureStationName = "수서",
                trainType = TrainType.GENERAL,
            )
        )

        stationTimesCacheUtils.setStationTimesCache(command, value)

        verify(redisClient).set(
            "STATION_TIMES:001-1-1",
            value,
            StationTimesCacheUtils.STATION_TIMES_REDIS_EXPIRE_SEC,
            TimeUnit.SECONDS,
        )
    }

    @Test
    @DisplayName("역 시간표가 비어있으면 짧은 TTL(10m)로 캐시한다.")
    fun setStationTimesCacheWithShortTtlOnEmpty() {
        val command = GetStationTimesCacheCommand(
            stationCode = "001",
            upDownType = UpDownType.UP,
            stationTimeWeekType = StationTimeWeekType.WEEKDAY,
        )
        val value = emptyList<GetStationTimesDto.StationTimes>()

        stationTimesCacheUtils.setStationTimesCache(command, value)

        verify(redisClient).set(
            "STATION_TIMES:001-1-1",
            value,
            StationTimesCacheUtils.EMPTY_STATION_TIMES_REDIS_EXPIRE_SEC,
            TimeUnit.SECONDS,
        )
    }
}
