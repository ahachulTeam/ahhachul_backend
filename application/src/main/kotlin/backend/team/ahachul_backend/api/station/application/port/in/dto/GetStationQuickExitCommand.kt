package backend.team.ahachul_backend.api.station.application.port.`in`.dto

import backend.team.ahachul_backend.api.train.domain.model.UpDownType

class GetStationQuickExitCommand(
    val stationId: Long,
    val subwayLineId: Long,
    val upDownType: UpDownType,
)
