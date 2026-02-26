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
        val accessibilityMode: RouteAccessibilityMode? = null,
        val crowdingPreference: RouteCrowdingPreference? = null,
        val luggageMode: RouteLuggageMode? = null,
        val travelerContext: RouteTravelerContext? = null,
        val locale: String? = null,
    ) {
        private fun normalizeLocale(): String {
            val normalized = locale?.trim()?.lowercase()
            return if (normalized in SUPPORTED_LOCALES) {
                normalized!!
            } else {
                "ko"
            }
        }

        fun toCommand(): SearchSubwayRouteQualityV3Command {
            return SearchSubwayRouteQualityV3Command(
                sourceStationId = sourceStationId,
                destinationStationId = destinationStationId,
                strategy = strategy ?: SearchSubwayRouteDto.RouteSearchStrategy.BALANCED,
                alternatives = (alternatives ?: 2).coerceIn(1, 4),
                walkingPreference = walkingPreference ?: RouteWalkingPreference.FAST,
                stationTimeWeekType = stationTimeWeekType ?: StationTimeWeekType.WEEKDAY,
                accessibilityMode = accessibilityMode ?: RouteAccessibilityMode.BALANCED,
                crowdingPreference = crowdingPreference ?: RouteCrowdingPreference.BALANCED,
                luggageMode = luggageMode ?: RouteLuggageMode.NORMAL,
                travelerContext = travelerContext ?: RouteTravelerContext.COMMUTE,
                locale = normalizeLocale(),
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
        val accessibilityMode: RouteAccessibilityMode,
        val crowdingPreference: RouteCrowdingPreference,
        val luggageMode: RouteLuggageMode,
        val travelerContext: RouteTravelerContext,
        val locale: String,
        val oneClickActions: List<OneClickAction>,
        val routes: List<Route>,
    )

    data class Route(
        val rank: Int,
        val nodes: List<Node>,
        val edges: List<Edge>,
        val summary: Summary,
        val quality: Quality,
        val accessibilityProfile: AccessibilityProfile,
        val boardingGuide: BoardingGuide,
        val crowdingGuide: CrowdingGuide,
        val nearbyEssentials: NearbyEssentials,
        val travelModeTags: List<RouteTravelModeTag>,
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
        val accessibilityScore: Int,
        val inStationDifficultyScore: Int,
        val crowdingComfortScore: Int,
        val delayProbabilityPercent: Int,
        val confidenceLevel: RouteQualityConfidenceLevel,
        val badges: List<RouteQualityBadge>,
        val reasons: List<String>,
    )

    data class AccessibilityProfile(
        val mode: RouteAccessibilityMode,
        val elevatorFriendlyTransferCount: Int,
        val estimatedStairSections: Int,
        val inStationDifficultyLevel: InStationDifficultyLevel,
        val mobilityNote: String,
    )

    data class BoardingGuide(
        val primaryCarNo: String,
        val transferOptimizedCarNo: String?,
        val recommendedDoorPosition: String,
        val reason: String,
        val confidenceLevel: BoardingGuideConfidenceLevel,
    )

    data class CrowdingGuide(
        val predictedLevel: RouteCrowdingLevel,
        val lessCrowdedCars: List<String>,
        val recommendation: String,
        val basedOn: String,
    )

    data class NearbyEssentials(
        val stationId: Long,
        val stationName: String,
        val items: List<NearbyEssentialItem>,
    )

    data class NearbyEssentialItem(
        val essentialType: NearbyEssentialType,
        val name: String,
        val walkingMinutes: Int,
        val openNow: Boolean,
        val operatingHours: String,
        val crowdLevel: RouteCrowdingLevel,
        val crowdUpdatedAt: String,
        val poiAccuracyScore: Int,
        val poiAccuracyReason: String,
        val reliabilityScore: Int,
        val reliabilityReason: String,
    )

    data class OneClickAction(
        val actionType: OneClickActionType,
        val title: String,
        val description: String,
        val deepLink: String,
        val payloadTemplate: String?,
    )

    enum class RouteWalkingPreference {
        FAST,
        LESS_STAIRS,
    }

    enum class RouteAccessibilityMode {
        BALANCED,
        ELEVATOR_PRIORITY,
        STAIRS_MINIMIZED,
        WHEELCHAIR,
        STROLLER,
    }

    enum class RouteCrowdingPreference {
        BALANCED,
        LESS_CROWDED,
    }

    enum class RouteLuggageMode {
        NORMAL,
        HEAVY_LUGGAGE,
        AIRPORT_TRAVEL,
    }

    enum class RouteTravelerContext {
        COMMUTE,
        SCHOOL,
        TRAVEL,
    }

    enum class InStationDifficultyLevel {
        EASY,
        MODERATE,
        HARD,
    }

    enum class BoardingGuideConfidenceLevel {
        HIGH,
        MEDIUM,
        LOW,
    }

    enum class RouteCrowdingLevel {
        LOW,
        MEDIUM,
        HIGH,
        VERY_HIGH,
    }

    enum class NearbyEssentialType {
        CONVENIENCE_STORE,
        RESTROOM,
        ATM,
        LATE_NIGHT_FOOD,
    }

    enum class RouteTravelModeTag {
        AIRPORT_FRIENDLY,
        TOURIST_FRIENDLY,
        ACCESSIBILITY_PRIORITY,
        LESS_CROWDED_RECOMMENDED,
    }

    enum class OneClickActionType {
        CALL_EMERGENCY_112,
        OPEN_LOST_REPORT,
        OPEN_COMPLAINT_REPORT,
        COPY_EMERGENCY_PHRASE,
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
        ACCESSIBILITY_RECOMMENDED,
        CROWDING_AVOIDANCE,
        AIRPORT_FRIENDLY,
        TOURIST_FRIENDLY,
    }

    companion object {
        private val SUPPORTED_LOCALES = setOf("ko", "en", "th", "cn")
    }
}
