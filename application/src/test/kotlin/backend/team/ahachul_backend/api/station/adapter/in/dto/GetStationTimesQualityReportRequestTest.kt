package backend.team.ahachul_backend.api.station.adapter.`in`.dto

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class GetStationTimesQualityReportRequestTest {

    @Test
    @DisplayName("samplePerLine은 최소 1로 보정한다.")
    fun normalizeSamplePerLineMin() {
        val request = GetStationTimesDto.QualityReportRequest(
            stationTimeWeekType = StationTimeWeekType.WEEKDAY,
            samplePerLine = 0,
        )

        val command = request.toCommand()

        assertThat(command.samplePerLine).isEqualTo(1)
    }

    @Test
    @DisplayName("samplePerLine은 최대 50으로 보정한다.")
    fun normalizeSamplePerLineMax() {
        val request = GetStationTimesDto.QualityReportRequest(
            stationTimeWeekType = StationTimeWeekType.WEEKDAY,
            samplePerLine = 100,
        )

        val command = request.toCommand()

        assertThat(command.samplePerLine).isEqualTo(50)
    }
}
