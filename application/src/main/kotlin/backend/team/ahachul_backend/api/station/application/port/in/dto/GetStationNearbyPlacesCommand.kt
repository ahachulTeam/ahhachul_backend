package backend.team.ahachul_backend.api.station.application.port.`in`.dto

class GetStationNearbyPlacesCommand(
    val stationId: Long,
    val subwayLineId: Long,
    val exitNo: String?,
    val limit: Int?,
)
