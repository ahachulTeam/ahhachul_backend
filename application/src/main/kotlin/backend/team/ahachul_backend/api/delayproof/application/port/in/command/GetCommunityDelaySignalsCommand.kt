package backend.team.ahachul_backend.api.delayproof.application.port.`in`.command

class GetCommunityDelaySignalsCommand(
    val subwayLineId: Long,
    val stationId: Long?,
    val windowMinutes: Int?,
    val limit: Int?,
) {
}
