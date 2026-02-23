package backend.team.ahachul_backend.api.station.application.port.`in`.dto

import backend.team.ahachul_backend.api.station.adapter.`in`.dto.StationTimeWeekType

class GetStationTimesSummaryCommand(
    val stationId: Long,
    val subwayLineId: Long,
    val stationTimeWeekType: StationTimeWeekType,
)
