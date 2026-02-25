package backend.team.ahachul_backend.api.station.application.port.`in`.dto

import backend.team.ahachul_backend.api.station.adapter.`in`.dto.SearchSubwayRouteDto
import backend.team.ahachul_backend.api.station.adapter.`in`.dto.SearchSubwayRouteQualityV3Dto
import backend.team.ahachul_backend.api.station.adapter.`in`.dto.StationTimeWeekType

data class SearchSubwayRouteQualityV3Command(
    val sourceStationId: Long,
    val destinationStationId: Long,
    val strategy: SearchSubwayRouteDto.RouteSearchStrategy,
    val alternatives: Int,
    val walkingPreference: SearchSubwayRouteQualityV3Dto.RouteWalkingPreference,
    val stationTimeWeekType: StationTimeWeekType,
)
