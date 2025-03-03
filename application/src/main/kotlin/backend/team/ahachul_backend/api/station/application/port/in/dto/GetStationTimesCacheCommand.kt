package backend.team.ahachul_backend.api.station.application.port.`in`.dto

import backend.team.ahachul_backend.api.train.domain.model.UpDownType
import backend.team.ahachul_backend.api.station.adapter.`in`.dto.StationTimeWeekType

class GetStationTimesCacheCommand(
    val stationCode: String,
    val upDownType: UpDownType,
    val stationTimeWeekType: StationTimeWeekType,
) {

}