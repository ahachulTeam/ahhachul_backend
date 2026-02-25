package backend.team.ahachul_backend.api.station.adapter.`in`.dto

import backend.team.ahachul_backend.api.station.application.port.`in`.dto.SearchSubwayRouteCommand

class SearchSubwayRouteDto {

    data class Request(
        val sourceStationId: Long,
        val destinationStationId: Long,
        val strategy: RouteSearchStrategy? = null,
        val alternatives: Int? = null,
    ) {
        fun toCommand(): SearchSubwayRouteCommand {
            return SearchSubwayRouteCommand(
                sourceStationId = sourceStationId,
                destinationStationId = destinationStationId,
                strategy = strategy ?: RouteSearchStrategy.BALANCED,
                alternatives = (alternatives ?: 2).coerceIn(1, 3),
            )
        }
    }

    data class Response(
        val generatedAt: String,
        val sourceStationId: Long,
        val destinationStationId: Long,
        val strategy: RouteSearchStrategy,
        val routes: List<Route>,
    )

    data class Route(
        val rank: Int,
        val nodes: List<Node>,
        val edges: List<Edge>,
        val summary: Summary,
    )

    data class Node(
        val stationId: Long,
        val stationName: String,
        val order: Int,
        val isTransfer: Boolean,
    )

    data class Edge(
        val fromStationId: Long,
        val toStationId: Long,
        val subwayLineId: Long,
        val subwayLineName: String,
    )

    data class Summary(
        val totalStops: Int,
        val transferCount: Int,
        val estimatedMinutes: Int,
    )

    enum class RouteSearchStrategy {
        BALANCED,
        MIN_TRANSFER,
        MIN_STOP,
    }
}
