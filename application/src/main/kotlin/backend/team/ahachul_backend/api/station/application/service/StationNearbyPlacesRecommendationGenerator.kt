package backend.team.ahachul_backend.api.station.application.service

import backend.team.ahachul_backend.api.station.adapter.`in`.dto.GetStationNearbyPlacesDto

object StationNearbyPlacesRecommendationGenerator {

    private data class SeedPlace(
        val name: String,
        val category: String,
        val walkingMinutes: Int,
        val openNow: Boolean,
        val supportsEnglishMenu: Boolean,
        val confidenceLevel: GetStationNearbyPlacesDto.NearbyPlaceConfidenceLevel,
    )

    private val seeded: Map<Pair<Long, Long>, List<SeedPlace>> = mapOf(
        Pair(622L, 3L) to listOf(
            SeedPlace("안암 김밥스테이션", "분식", 4, true, true, GetStationNearbyPlacesDto.NearbyPlaceConfidenceLevel.MEDIUM),
            SeedPlace("한밤 편의마트", "편의점", 3, true, false, GetStationNearbyPlacesDto.NearbyPlaceConfidenceLevel.HIGH),
            SeedPlace("역앞 샌드랩", "샌드위치", 6, true, true, GetStationNearbyPlacesDto.NearbyPlaceConfidenceLevel.MEDIUM),
            SeedPlace("24시 라면포차", "분식", 7, false, true, GetStationNearbyPlacesDto.NearbyPlaceConfidenceLevel.LOW),
        )
    )

    fun generate(
        stationId: Long,
        subwayLineId: Long,
        exitNo: String?,
        limit: Int,
    ): List<GetStationNearbyPlacesDto.Place> {
        val safeLimit = limit.coerceIn(1, 5)
        val places = seeded[Pair(stationId, subwayLineId)] ?: buildFallback(stationId, subwayLineId, exitNo)

        return places.take(safeLimit).map {
            GetStationNearbyPlacesDto.Place(
                name = it.name,
                category = it.category,
                walkingMinutes = it.walkingMinutes,
                openNow = it.openNow,
                supportsEnglishMenu = it.supportsEnglishMenu,
                confidenceLevel = it.confidenceLevel,
            )
        }
    }

    private fun buildFallback(
        stationId: Long,
        subwayLineId: Long,
        exitNo: String?,
    ): List<SeedPlace> {
        val base = ((stationId + subwayLineId) % 5 + 3).toInt()
        val exitLabel = exitNo ?: "주출구"

        return listOf(
            SeedPlace(
                name = "${exitLabel} 편의점",
                category = "편의점",
                walkingMinutes = base,
                openNow = true,
                supportsEnglishMenu = false,
                confidenceLevel = GetStationNearbyPlacesDto.NearbyPlaceConfidenceLevel.MEDIUM,
            ),
            SeedPlace(
                name = "${exitLabel} 김밥하우스",
                category = "분식",
                walkingMinutes = base + 2,
                openNow = true,
                supportsEnglishMenu = true,
                confidenceLevel = GetStationNearbyPlacesDto.NearbyPlaceConfidenceLevel.LOW,
            ),
            SeedPlace(
                name = "${exitLabel} 카페", 
                category = "카페",
                walkingMinutes = base + 3,
                openNow = false,
                supportsEnglishMenu = true,
                confidenceLevel = GetStationNearbyPlacesDto.NearbyPlaceConfidenceLevel.LOW,
            ),
        )
    }
}
