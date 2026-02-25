package backend.team.ahachul_backend.api.station.application.port.`in`.dto

data class GetStationTimesFullCommand(
    val stationId: Long,
    val subwayLineId: Long,
)
