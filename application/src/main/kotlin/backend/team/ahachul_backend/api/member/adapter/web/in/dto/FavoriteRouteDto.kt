package backend.team.ahachul_backend.api.member.adapter.web.`in`.dto

import backend.team.ahachul_backend.api.member.application.command.CreateFavoriteRouteCommand

class FavoriteRouteDto {

    data class GraphResponse(
        val routes: List<Route>,
    )

    data class CreateRequest(
        val sourceStationId: Long,
        val destinationStationId: Long,
        val title: String? = null,
    ) {
        fun toCommand(): CreateFavoriteRouteCommand {
            return CreateFavoriteRouteCommand(
                sourceStationId = sourceStationId,
                destinationStationId = destinationStationId,
                title = title?.trim()?.ifBlank { null },
            )
        }
    }

    data class DeleteResponse(
        val routeId: Long,
    )

    enum class RouteType {
        RECOMMENDED,
        CUSTOM,
    }

    data class Route(
        val routeId: Long?,
        val routeType: RouteType,
        val title: String?,
        val sourceStationId: Long,
        val sourceStationName: String,
        val destinationStationId: Long,
        val destinationStationName: String,
        val nodes: List<Node>,
        val edges: List<Edge>,
        val summary: Summary,
    )

    data class Node(
        val stationId: Long,
        val stationName: String,
        val order: Int,
        val favorite: Boolean,
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
}
