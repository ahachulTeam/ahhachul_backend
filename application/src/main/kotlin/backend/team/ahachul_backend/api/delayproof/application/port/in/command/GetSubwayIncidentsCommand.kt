package backend.team.ahachul_backend.api.delayproof.application.port.`in`.command

class GetSubwayIncidentsCommand(
    val subwayLineId: Long,
    val stationId: Long?,
    val limit: Int?,
) {
}
