package backend.team.ahachul_backend.api.station.application.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class StationNearbyPlacesRecommendationGeneratorTest {

    @Test
    fun 고정_데이터가_있으면_limit_범위로_반환한다() {
        val result = StationNearbyPlacesRecommendationGenerator.generate(
            stationId = 622,
            subwayLineId = 3,
            exitNo = "3",
            limit = 3,
        )

        assertThat(result).hasSize(3)
        assertThat(result.first().category).isEqualTo("편의점")
        assertThat(result.first().reliabilityScore).isGreaterThan(70)
    }

    @Test
    fun 고정_데이터가_없어도_fallback_데이터를_반환한다() {
        val result = StationNearbyPlacesRecommendationGenerator.generate(
            stationId = 99999,
            subwayLineId = 99,
            exitNo = null,
            limit = 5,
        )

        assertThat(result).isNotEmpty()
        assertThat(result.first().name).contains("출구")
        assertThat(result.map { it.essentialType.name }).contains("CONVENIENCE_STORE", "RESTROOM", "ATM")
    }
}
