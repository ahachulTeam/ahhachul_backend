package backend.team.ahachul_backend.api.station.adapter.`in`.dto

import backend.team.ahachul_backend.api.station.application.port.`in`.dto.GetStationNearbyPlacesCommand

class GetStationNearbyPlacesDto {

    data class Request(
        val stationId: Long,
        val subwayLineId: Long,
        val exitNo: String?,
        val limit: Int?,
    ) {
        fun toCommand(): GetStationNearbyPlacesCommand {
            return GetStationNearbyPlacesCommand(
                stationId = stationId,
                subwayLineId = subwayLineId,
                exitNo = exitNo,
                limit = limit,
            )
        }
    }

    data class Response(
        val generatedAt: String,
        val stationId: Long,
        val subwayLineId: Long,
        val exitNo: String?,
        val summary: String,
        val places: List<Place>,
    )

    data class Place(
        val name: String,
        val category: String,
        val essentialType: NearbyEssentialType,
        val walkingMinutes: Int,
        val openNow: Boolean,
        val supportsEnglishMenu: Boolean,
        val confidenceLevel: NearbyPlaceConfidenceLevel,
        val reliabilityScore: Int,
        val reliabilityReason: String,
        val sourceCount: Int,
        val lastVerifiedAt: String,
    )

    enum class NearbyEssentialType {
        CONVENIENCE_STORE, RESTROOM, ATM, LATE_NIGHT_FOOD
    }

    enum class NearbyPlaceConfidenceLevel {
        HIGH, MEDIUM, LOW
    }
}
