package backend.team.ahachul_backend.api.foreigner.application.port.`in`.dto

import java.time.LocalDateTime

data class CreateForeignerStationSocialMeetupCommand(
    val stationId: Long,
    val subwayLineId: Long,
    val title: String,
    val description: String,
    val meetupAt: LocalDateTime,
    val maxParticipants: Int,
    val nationalityCode: String?,
    val sameNationalityOnly: Boolean,
)
