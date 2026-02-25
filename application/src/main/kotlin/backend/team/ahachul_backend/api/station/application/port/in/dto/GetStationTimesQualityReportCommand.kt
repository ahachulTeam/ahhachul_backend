package backend.team.ahachul_backend.api.station.application.port.`in`.dto

import backend.team.ahachul_backend.api.station.adapter.`in`.dto.StationTimeWeekType

class GetStationTimesQualityReportCommand(
    val stationTimeWeekType: StationTimeWeekType,
    val samplePerLine: Int,
)
