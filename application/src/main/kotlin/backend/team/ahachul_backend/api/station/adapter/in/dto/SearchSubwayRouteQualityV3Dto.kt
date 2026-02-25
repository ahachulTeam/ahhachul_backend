package backend.team.ahachul_backend.api.station.adapter.`in`.dto

import backend.team.ahachul_backend.api.station.application.port.`in`.dto.SearchSubwayRouteQualityV3Command

class SearchSubwayRouteQualityV3Dto {

    data class Request(
        val sourceStationId: Long,
        val destinationStationId: Long,
        val strategy: SearchSubwayRouteDto.RouteSearchStrategy? = null,
        val alternatives: Int? = null,
        val walkingPreference: RouteWalkingPreference? = null,
        val stationTimeWeekType: StationTimeWeekType? = null,
    ) {
        fun toCommand(): SearchSubwayRouteQualityV3Command {
            return SearchSubwayRouteQualityV3Command(
                sourceStationId = sourceStationId,
                destinationStationId = destinationStationId,
                strategy = strategy ?: SearchSubwayRouteDto.RouteSearchStrategy.BALANCED,
                alternatives = (alternatives ?: 2).coerceIn(1, 4),
                walkingPreference = walkingPreference ?: RouteWalkingPreference.FAST,
                stationTimeWeekType = stationTimeWeekType ?: StationTimeWeekType.WEEKDAY,
            )
        }
    }

    data class Response(
        val modelVersion: String,
        val generatedAt: String,
        val sourceStationId: Long,
        val destinationStationId: Long,
        val strategy: SearchSubwayRouteDto.RouteSearchStrategy,
        val walkingPreference: RouteWalkingPreference,
        val stationTimeWeekType: StationTimeWeekType,
        val routes: List<Route>,
    )

    data class Route(
        val rank: Int,
        val nodes: List<Node>,
        val edges: List<Edge>,
        val summary: Summary,
        val quality: Quality,
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

    data class Quality(
        val totalScore: Int,
        val transferRiskScore: Int,
        val walkingScore: Int,
        val lastTrainSafetyScore: Int,
        val delayResilienceScore: Int,
        val delayProbabilityPercent: Int,
        val confidenceLevel: RouteQualityConfidenceLevel,
        val badges: List<RouteQualityBadge>,
        val reasons: List<String>,
    )

    enum class RouteWalkingPreference {
        FAST,
        LESS_STAIRS,
    }

    enum class RouteQualityConfidenceLevel {
        HIGH,
        MEDIUM,
        LOW,
    }

    enum class RouteQualityBadge {
        BEST_RECOMMENDED,
        TRANSFER_HEAVY,
        WALKING_HEAVY,
        LAST_TRAIN_RISK,
        DELAY_RISK,
        DATA_LIMITED,
    }
}
