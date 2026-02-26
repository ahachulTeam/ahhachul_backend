package backend.team.ahachul_backend.api.member.adapter.web.`in`.dto

import backend.team.ahachul_backend.api.member.domain.model.MemberStationWalkingSourceType

class GetBookmarkStationDto {

    data class Response(
        val stationInfoList: List<StationInfo>
    )

    data class StationInfo(
        val stationId: Long,
        val stationName: String,
        val label: String?,
        val locationMeta: LocationMeta?,
        val subwayLineInfoList: List<SubwayLineInfo>
    )

    data class LocationMeta(
        val locationName: String?,
        val roadAddress: String?,
        val jibunAddress: String?,
        val latitude: Double?,
        val longitude: Double?,
        val walkingMinutes: Int?,
        val walkingSource: MemberStationWalkingSourceType?,
        val walkingUpdatedAt: String?,
    )

    data class SubwayLineInfo(
        val subwayLineId: Long,
        val subwayLineName: String
    )
}
