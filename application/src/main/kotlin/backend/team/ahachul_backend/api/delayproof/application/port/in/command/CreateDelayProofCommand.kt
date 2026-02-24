package backend.team.ahachul_backend.api.delayproof.application.port.`in`.command

import backend.team.ahachul_backend.api.train.domain.model.UpDownType

class CreateDelayProofCommand(
    val stationId: Long,
    val subwayLineId: Long,
    val upDownType: UpDownType?,
    val expectedArrivalAt: String?,
    val customMessage: String?,
) {
}
