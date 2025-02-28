package backend.team.ahachul_backend.api.station.application.service

import backend.team.ahachul_backend.api.station.adapter.`in`.dto.GetStationTimesDto
import backend.team.ahachul_backend.api.station.adapter.`in`.dto.StationTimeWeekType
import backend.team.ahachul_backend.api.station.application.port.`in`.dto.GetStationTimesCacheCommand
import backend.team.ahachul_backend.api.train.domain.model.TrainType
import backend.team.ahachul_backend.api.train.domain.model.UpDownType
import backend.team.ahachul_backend.config.controller.CommonServiceTestConfig
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class StationCacheUtilsTest(
    @Autowired val stationTimesCacheUtils: StationTimesCacheUtils,
) : CommonServiceTestConfig() {

    @Test
    @DisplayName("캐시에 역 시간표 API 응답을 가공하여 저장한다.")
    fun saveApiResponseToCache() {
        // given
        val command = GetStationTimesCacheCommand(
            stationCode = "001",
            upDownType = UpDownType.UP,
            stationTimeWeekType = StationTimeWeekType.WEEKDAY
        )

        val value = listOf(
            GetStationTimesDto.StationTimes(
                arrivalTime = "06:30:00",
                departureTime = "00:00:00",
                arrivalStationName = "대화",
                departureStationName = "수서",
                trainType = TrainType.GENERAL
            ),
            GetStationTimesDto.StationTimes(
                arrivalTime = "06:35:00",
                departureTime = "06:35:30",
                arrivalStationName = "구파발",
                departureStationName = "오금",
                trainType = TrainType.GENERAL
            )
        )

        // when
        stationTimesCacheUtils.setStationTimesCache(command, value)

        // then
        val result = stationTimesCacheUtils.getStationTimesByCache(command)
        assertThat(result?.size).isEqualTo(2)
        assertThat(result)
            .extracting("arrivalTime", "departureTime", "arrivalStationName", "departureStationName", "trainType")
            .containsExactly(
                tuple("06:30:00", "00:00:00", "대화", "수서", TrainType.GENERAL),
                tuple("06:35:00", "06:35:30", "구파발", "오금", TrainType.GENERAL),
            )
    }

    @Test
    @DisplayName("캐시를 가져올 때 상하행값이 달라서는 안된다.")
    fun getCacheUpAndDownTypeMustBeEqual() {
        // given
        val command = GetStationTimesCacheCommand(
            stationCode = "001",
            upDownType = UpDownType.UP,
            stationTimeWeekType = StationTimeWeekType.WEEKDAY
        )

        val value = listOf(
            GetStationTimesDto.StationTimes(
                arrivalTime = "06:30:00",
                departureTime = "00:00:00",
                arrivalStationName = "대화",
                departureStationName = "수서",
                trainType = TrainType.GENERAL
            ),
            GetStationTimesDto.StationTimes(
                arrivalTime = "06:35:00",
                departureTime = "06:35:30",
                arrivalStationName = "구파발",
                departureStationName = "오금",
                trainType = TrainType.GENERAL
            )
        )

        stationTimesCacheUtils.setStationTimesCache(command, value)

        // when
        val result = stationTimesCacheUtils.getStationTimesByCache(GetStationTimesCacheCommand(
            stationCode = "001",
            upDownType = UpDownType.DOWN,
            stationTimeWeekType = StationTimeWeekType.WEEKDAY
        ))

        // then
        assertThat(result).isNull()
    }

    @Test
    @DisplayName("캐시를 가져올 때 역 시간 요일값이 달라서는 안된다.")
    fun getCacheStationTimeWeekTypeMustBeEqual() {
        // given
        val command = GetStationTimesCacheCommand(
            stationCode = "001",
            upDownType = UpDownType.UP,
            stationTimeWeekType = StationTimeWeekType.WEEKDAY
        )

        val value = listOf(
            GetStationTimesDto.StationTimes(
                arrivalTime = "06:30:00",
                departureTime = "00:00:00",
                arrivalStationName = "대화",
                departureStationName = "수서",
                trainType = TrainType.GENERAL
            ),
            GetStationTimesDto.StationTimes(
                arrivalTime = "06:35:00",
                departureTime = "06:35:30",
                arrivalStationName = "구파발",
                departureStationName = "오금",
                trainType = TrainType.GENERAL
            )
        )

        stationTimesCacheUtils.setStationTimesCache(command, value)

        // when
        val result = stationTimesCacheUtils.getStationTimesByCache(GetStationTimesCacheCommand(
            stationCode = "001",
            upDownType = UpDownType.UP,
            stationTimeWeekType = StationTimeWeekType.HOLIDAY
        ))

        // then
        assertThat(result).isNull()
    }

}