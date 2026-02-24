package backend.team.ahachul_backend.api.member.application.command

data class CreateFavoriteRouteCommand(
    val sourceStationId: Long,
    val destinationStationId: Long,
    val title: String?,
)
