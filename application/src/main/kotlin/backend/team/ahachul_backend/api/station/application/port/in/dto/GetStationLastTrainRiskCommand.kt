package backend.team.ahachul_backend.api.station.application.port.`in`.dto

import backend.team.ahachul_backend.api.station.adapter.`in`.dto.StationTimeWeekType
import backend.team.ahachul_backend.api.train.domain.model.UpDownType

class GetStationLastTrainRiskCommand(
    val stationId: Long,
    val subwayLineId: Long,
    val upDownType: UpDownType,
    val stationTimeWeekType: StationTimeWeekType,
    val walkingMinutes: Int,
)
