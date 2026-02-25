package backend.team.ahachul_backend.api.foreigner.application.port.`in`.dto

data class JoinForeignerStationSocialMeetupCommand(
    val meetupId: Long,
    val introductionMessage: String?,
    val nationalityCode: String?,
)
