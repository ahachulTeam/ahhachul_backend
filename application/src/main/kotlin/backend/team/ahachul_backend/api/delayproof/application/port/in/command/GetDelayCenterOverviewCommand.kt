package backend.team.ahachul_backend.api.delayproof.application.port.`in`.command

import backend.team.ahachul_backend.api.train.domain.model.UpDownType

class GetDelayCenterOverviewCommand(
    val stationId: Long,
    val subwayLineId: Long,
    val upDownType: UpDownType?,
    val windowMinutes: Int?,
    val incidentLimit: Int?,
    val signalLimit: Int?,
) {
}

