package backend.team.ahachul_backend.api.member.adapter.web.`in`.dto

class CommuteCoachDto {

    enum class RiskLevel {
        LOW,
        MEDIUM,
        HIGH,
    }

    enum class WalkingMinutesSource {
        USER_PROFILE,
        DEFAULT,
    }

    data class WalkingLeg(
        val stationId: Long,
        val stationName: String,
        val walkingMinutes: Int,
        val walkingMinutesSource: WalkingMinutesSource,
        val walkingMinutesUpdatedAt: String?,
    )

    data class WalkingMeta(
        val totalWalkingMinutes: Int,
        val source: WalkingLeg,
        val destination: WalkingLeg,
    )

    data class Response(
        val generatedAt: String,
        val targetArrivalAt: String,
        val safeDepartureAt: String?,
        val departureInMinutes: Int?,
        val riskLevel: RiskLevel,
        val riskReasons: List<String>,
        val walkingMeta: WalkingMeta?,
        val primaryRoute: FavoriteRouteDto.Route?,
        val alternativeRoutes: List<FavoriteRouteDto.Route>,
        val guidanceMessage: String,
    )
}
