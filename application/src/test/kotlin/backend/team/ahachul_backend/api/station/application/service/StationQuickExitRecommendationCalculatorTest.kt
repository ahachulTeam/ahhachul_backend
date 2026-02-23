package backend.team.ahachul_backend.api.station.application.service

import backend.team.ahachul_backend.api.station.adapter.`in`.dto.GetStationTimesDto
import backend.team.ahachul_backend.api.train.domain.model.UpDownType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class StationQuickExitRecommendationCalculatorTest {

    @Test
    fun 고정_룰이_있으면_룰기반_추천을_반환한다() {
        val result = StationQuickExitRecommendationCalculator.recommend(
            stationId = 622,
            subwayLineId = 3,
            upDownType = UpDownType.DOWN,
        )

        assertThat(result).hasSize(2)
        assertThat(result.first().carNo).isEqualTo("5-2")
        assertThat(result.first().confidenceLevel).isEqualTo(GetStationTimesDto.QuickExitConfidenceLevel.MEDIUM)
    }

    @Test
    fun 룰이_없어도_fallback_추천을_반환한다() {
        val result = StationQuickExitRecommendationCalculator.recommend(
            stationId = 99999,
            subwayLineId = 88,
            upDownType = UpDownType.UP,
        )

        assertThat(result).isNotEmpty()
        assertThat(result.first().carNo).contains("-")
        assertThat(result.first().walkingBenefitMinutes).isBetween(1, 4)
    }
}
