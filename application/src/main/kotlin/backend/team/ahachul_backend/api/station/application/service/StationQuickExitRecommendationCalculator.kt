package backend.team.ahachul_backend.api.station.application.service

import backend.team.ahachul_backend.api.station.adapter.`in`.dto.GetStationTimesDto
import backend.team.ahachul_backend.api.train.domain.model.UpDownType

object StationQuickExitRecommendationCalculator {

    private data class Rule(
        val carNo: String,
        val exitNo: String,
        val directionHint: String,
        val walkingBenefitMinutes: Int,
        val confidenceLevel: GetStationTimesDto.QuickExitConfidenceLevel,
    )

    private val seededRules: Map<Triple<Long, Long, UpDownType>, List<Rule>> = mapOf(
        Triple(622L, 3L, UpDownType.DOWN) to listOf(
            Rule("5-2", "3", "환승 통로 우측", 2, GetStationTimesDto.QuickExitConfidenceLevel.MEDIUM),
            Rule("4-4", "2", "엘리베이터 인접", 1, GetStationTimesDto.QuickExitConfidenceLevel.LOW),
        ),
        Triple(622L, 3L, UpDownType.UP) to listOf(
            Rule("6-1", "1", "계단 진입 즉시 좌측", 3, GetStationTimesDto.QuickExitConfidenceLevel.HIGH),
            Rule("5-3", "2", "환승 통로 직진", 2, GetStationTimesDto.QuickExitConfidenceLevel.MEDIUM),
        ),
    )

    fun recommend(
        stationId: Long,
        subwayLineId: Long,
        upDownType: UpDownType,
    ): List<GetStationTimesDto.QuickExitRecommendation> {
        val rules = seededRules[Triple(stationId, subwayLineId, upDownType)]
            ?: buildFallbackRules(stationId, subwayLineId, upDownType)

        return rules
            .take(2)
            .map { rule ->
                GetStationTimesDto.QuickExitRecommendation(
                    carNo = rule.carNo,
                    exitNo = rule.exitNo,
                    directionHint = rule.directionHint,
                    walkingBenefitMinutes = rule.walkingBenefitMinutes,
                    confidenceLevel = rule.confidenceLevel,
                )
            }
    }

    private fun buildFallbackRules(
        stationId: Long,
        subwayLineId: Long,
        upDownType: UpDownType,
    ): List<Rule> {
        val base = ((stationId + subwayLineId) % 6 + 3).toInt()
        val primaryCar = if (upDownType == UpDownType.UP) "${base}-1" else "${base}-3"
        val secondaryCar = if (upDownType == UpDownType.UP) "${base + 1}-2" else "${base - 1}-2"

        return listOf(
            Rule(
                carNo = primaryCar,
                exitNo = if (upDownType == UpDownType.UP) "2" else "3",
                directionHint = "표지판 기준 ${if (upDownType == UpDownType.UP) "좌측" else "우측"} 이동",
                walkingBenefitMinutes = 2,
                confidenceLevel = GetStationTimesDto.QuickExitConfidenceLevel.MEDIUM,
            ),
            Rule(
                carNo = secondaryCar,
                exitNo = if (upDownType == UpDownType.UP) "1" else "4",
                directionHint = "계단 인접 동선",
                walkingBenefitMinutes = 1,
                confidenceLevel = GetStationTimesDto.QuickExitConfidenceLevel.LOW,
            ),
        )
    }
}
