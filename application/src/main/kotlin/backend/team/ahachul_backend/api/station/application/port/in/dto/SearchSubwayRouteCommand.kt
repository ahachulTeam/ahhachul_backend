package backend.team.ahachul_backend.api.station.application.port.`in`.dto

import backend.team.ahachul_backend.api.station.adapter.`in`.dto.SearchSubwayRouteDto

data class SearchSubwayRouteCommand(
    val sourceStationId: Long,
    val destinationStationId: Long,
    val strategy: SearchSubwayRouteDto.RouteSearchStrategy,
    val alternatives: Int,
)
