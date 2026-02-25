package backend.team.ahachul_backend.api.member.adapter.web.`in`.dto

class CommuteCoachDto {

    enum class RiskLevel {
        LOW,
        MEDIUM,
        HIGH,
    }

    data class Response(
        val generatedAt: String,
        val targetArrivalAt: String,
        val safeDepartureAt: String?,
        val departureInMinutes: Int?,
        val riskLevel: RiskLevel,
        val riskReasons: List<String>,
        val primaryRoute: FavoriteRouteDto.Route?,
        val alternativeRoutes: List<FavoriteRouteDto.Route>,
        val guidanceMessage: String,
    )
}
