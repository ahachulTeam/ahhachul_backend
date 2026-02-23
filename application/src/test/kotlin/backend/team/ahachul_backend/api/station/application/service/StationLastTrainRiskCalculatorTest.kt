package backend.team.ahachul_backend.api.station.application.service

import backend.team.ahachul_backend.api.station.adapter.`in`.dto.GetStationTimesDto
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime

class StationLastTrainRiskCalculatorTest {

    @Test
    fun 여유시간이_충분하면_SAFE를_반환한다() {
        val nowAt = OffsetDateTime.parse("2026-02-23T22:00:00+09:00")
        val result = StationLastTrainRiskCalculator.calculate(nowAt, "23:50:00", 15)

        assertThat(result.riskLevel).isEqualTo(GetStationTimesDto.LastTrainRiskLevel.SAFE)
        assertThat(result.isLastTrainRisk).isFalse()
        assertThat(result.minutesToLastTrain).isEqualTo(110)
    }

    @Test
    fun 여유시간이_부족하면_WARN을_반환한다() {
        val nowAt = OffsetDateTime.parse("2026-02-23T23:45:00+09:00")
        val result = StationLastTrainRiskCalculator.calculate(nowAt, "23:58:00", 15)

        assertThat(result.riskLevel).isEqualTo(GetStationTimesDto.LastTrainRiskLevel.WARN)
        assertThat(result.isLastTrainRisk).isTrue()
        assertThat(result.minutesToLastTrain).isEqualTo(13)
    }

    @Test
    fun 막차가_이미_지나면_RISK를_반환한다() {
        val nowAt = OffsetDateTime.parse("2026-02-23T02:00:00+09:00")
        val result = StationLastTrainRiskCalculator.calculate(nowAt, "00:30:00", 10)

        assertThat(result.riskLevel).isEqualTo(GetStationTimesDto.LastTrainRiskLevel.RISK)
        assertThat(result.isLastTrainRisk).isTrue()
        assertThat(result.minutesToLastTrain).isLessThanOrEqualTo(0)
    }
}
