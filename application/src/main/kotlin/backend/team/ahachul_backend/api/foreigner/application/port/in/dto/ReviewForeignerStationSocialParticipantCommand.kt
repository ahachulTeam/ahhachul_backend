package backend.team.ahachul_backend.api.foreigner.application.port.`in`.dto

data class ReviewForeignerStationSocialParticipantCommand(
    val meetupId: Long,
    val participantId: Long,
    val approve: Boolean,
)
