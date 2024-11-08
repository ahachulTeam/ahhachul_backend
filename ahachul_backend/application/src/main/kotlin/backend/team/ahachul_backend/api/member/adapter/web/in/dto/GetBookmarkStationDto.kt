package backend.team.ahachul_backend.api.member.adapter.web.`in`.dto

class GetBookmarkStationDto {

    data class Response(
        val stationInfoList: List<StationInfo>
    )

    data class StationInfo(
        val stationId: Long,
        val stationName: String,
        val subwayLineInfoList: List<SubwayLineInfo>
    )

    data class SubwayLineInfo(
        val subwayLineId: Long,
        val subwayLineName: String
    )
}
