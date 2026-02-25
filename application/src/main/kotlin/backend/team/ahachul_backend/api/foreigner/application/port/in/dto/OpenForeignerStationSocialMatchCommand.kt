package backend.team.ahachul_backend.api.foreigner.application.port.`in`.dto

data class OpenForeignerStationSocialMatchCommand(
    val meetupId: Long,
    val targetMemberId: Long,
    val openingMessage: String?,
)
