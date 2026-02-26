package backend.team.ahachul_backend.api.member.application.command

import backend.team.ahachul_backend.api.member.domain.model.MemberStationWalkingSourceType

data class BookmarkStationCommand(
    val stationName: String,
    val label: String?,
    val stationId: Long? = null,
    val locationMeta: BookmarkStationLocationMetaCommand? = null,
)

data class BookmarkStationLocationMetaCommand(
    val locationName: String? = null,
    val roadAddress: String? = null,
    val jibunAddress: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val walkingMinutes: Int? = null,
    val walkingSource: MemberStationWalkingSourceType? = null,
)
