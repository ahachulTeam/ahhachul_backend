package backend.team.ahachul_backend.api.station.adapter.`in`.dto

import backend.team.ahachul_backend.api.train.domain.model.UpDownType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class GetStationTimesDtoRequestTest {

    @Test
    @DisplayName("stationTimeWeekType가 있으면 해당 값을 우선 사용한다.")
    fun preferStationTimeWeekType() {
        val request = GetStationTimesDto.Request(
            stationId = 1,
            subwayLineId = 2,
            upDownType = UpDownType.UP,
            stationTimeWeekType = StationTimeWeekType.HOLIDAY,
            weekTag = 1,
        )

        val command = request.toCommand()

        assertThat(command.stationTimeWeekType).isEqualTo(StationTimeWeekType.HOLIDAY)
    }

    @Test
    @DisplayName("stationTimeWeekType가 없으면 weekTag를 사용한다.")
    fun fallbackToWeekTag() {
        val request = GetStationTimesDto.Request(
            stationId = 1,
            subwayLineId = 2,
            upDownType = UpDownType.UP,
            stationTimeWeekType = null,
            weekTag = 2,
        )

        val command = request.toCommand()

        assertThat(command.stationTimeWeekType).isEqualTo(StationTimeWeekType.SATURDAY)
    }

    @Test
    @DisplayName("stationTimeWeekType와 weekTag가 모두 없으면 WEEKDAY를 기본값으로 사용한다.")
    fun defaultToWeekday() {
        val request = GetStationTimesDto.Request(
            stationId = 1,
            subwayLineId = 2,
            upDownType = UpDownType.UP,
            stationTimeWeekType = null,
            weekTag = null,
        )

        val command = request.toCommand()

        assertThat(command.stationTimeWeekType).isEqualTo(StationTimeWeekType.WEEKDAY)
    }
}
