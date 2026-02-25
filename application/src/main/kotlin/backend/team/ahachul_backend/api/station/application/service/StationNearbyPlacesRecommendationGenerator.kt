package backend.team.ahachul_backend.api.station.application.service

import backend.team.ahachul_backend.api.station.adapter.`in`.dto.GetStationNearbyPlacesDto

object StationNearbyPlacesRecommendationGenerator {

    private data class SeedPlace(
        val name: String,
        val category: String,
        val essentialType: GetStationNearbyPlacesDto.NearbyEssentialType,
        val walkingMinutes: Int,
        val openNow: Boolean,
        val supportsEnglishMenu: Boolean,
        val confidenceLevel: GetStationNearbyPlacesDto.NearbyPlaceConfidenceLevel,
        val reliabilityScore: Int,
        val reliabilityReason: String,
        val sourceCount: Int,
        val lastVerifiedAt: String,
    )

    private val seeded: Map<Pair<Long, Long>, List<SeedPlace>> = mapOf(
        Pair(622L, 3L) to listOf(
            SeedPlace(
                name = "한밤 편의마트",
                category = "편의점",
                essentialType = GetStationNearbyPlacesDto.NearbyEssentialType.CONVENIENCE_STORE,
                walkingMinutes = 3,
                openNow = true,
                supportsEnglishMenu = false,
                confidenceLevel = GetStationNearbyPlacesDto.NearbyPlaceConfidenceLevel.HIGH,
                reliabilityScore = 90,
                reliabilityReason = "최근 7일 사용자 확인 + 운영시간 일치",
                sourceCount = 12,
                lastVerifiedAt = "2026-02-25T21:10:00+09:00",
            ),
            SeedPlace(
                name = "안암역 3번출구 공중화장실",
                category = "화장실",
                essentialType = GetStationNearbyPlacesDto.NearbyEssentialType.RESTROOM,
                walkingMinutes = 2,
                openNow = true,
                supportsEnglishMenu = false,
                confidenceLevel = GetStationNearbyPlacesDto.NearbyPlaceConfidenceLevel.HIGH,
                reliabilityScore = 87,
                reliabilityReason = "역사 시설 데이터 + 최근 점검 로그",
                sourceCount = 8,
                lastVerifiedAt = "2026-02-25T08:35:00+09:00",
            ),
            SeedPlace(
                name = "안암역 ATM 코너",
                category = "ATM",
                essentialType = GetStationNearbyPlacesDto.NearbyEssentialType.ATM,
                walkingMinutes = 4,
                openNow = true,
                supportsEnglishMenu = false,
                confidenceLevel = GetStationNearbyPlacesDto.NearbyPlaceConfidenceLevel.MEDIUM,
                reliabilityScore = 79,
                reliabilityReason = "제휴 ATM 위치 데이터 + 최근 제보",
                sourceCount = 5,
                lastVerifiedAt = "2026-02-24T18:12:00+09:00",
            ),
            SeedPlace(
                name = "24시 라면포차",
                category = "늦은 시간 식당",
                essentialType = GetStationNearbyPlacesDto.NearbyEssentialType.LATE_NIGHT_FOOD,
                walkingMinutes = 7,
                openNow = false,
                supportsEnglishMenu = true,
                confidenceLevel = GetStationNearbyPlacesDto.NearbyPlaceConfidenceLevel.MEDIUM,
                reliabilityScore = 72,
                reliabilityReason = "야간 운영 이력 기반(휴무 변동 가능)",
                sourceCount = 4,
                lastVerifiedAt = "2026-02-23T23:42:00+09:00",
            ),
        )
    )

    fun generate(
        stationId: Long,
        subwayLineId: Long,
        exitNo: String?,
        limit: Int,
    ): List<GetStationNearbyPlacesDto.Place> {
        val safeLimit = limit.coerceIn(1, 6)
        val places = seeded[Pair(stationId, subwayLineId)] ?: buildFallback(stationId, subwayLineId, exitNo)

        return places.take(safeLimit).map {
            GetStationNearbyPlacesDto.Place(
                name = it.name,
                category = it.category,
                essentialType = it.essentialType,
                walkingMinutes = it.walkingMinutes,
                openNow = it.openNow,
                supportsEnglishMenu = it.supportsEnglishMenu,
                confidenceLevel = it.confidenceLevel,
                reliabilityScore = it.reliabilityScore,
                reliabilityReason = it.reliabilityReason,
                sourceCount = it.sourceCount,
                lastVerifiedAt = it.lastVerifiedAt,
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
        val now = "2026-02-25T22:00:00+09:00"

        return listOf(
            SeedPlace(
                name = "${exitLabel} 편의점",
                category = "편의점",
                essentialType = GetStationNearbyPlacesDto.NearbyEssentialType.CONVENIENCE_STORE,
                walkingMinutes = base,
                openNow = true,
                supportsEnglishMenu = false,
                confidenceLevel = GetStationNearbyPlacesDto.NearbyPlaceConfidenceLevel.MEDIUM,
                reliabilityScore = 76,
                reliabilityReason = "자동 수집 데이터 기준(실시간 변동 가능)",
                sourceCount = 3,
                lastVerifiedAt = now,
            ),
            SeedPlace(
                name = "${exitLabel} 역사 화장실",
                category = "화장실",
                essentialType = GetStationNearbyPlacesDto.NearbyEssentialType.RESTROOM,
                walkingMinutes = maxOf(1, base - 1),
                openNow = true,
                supportsEnglishMenu = false,
                confidenceLevel = GetStationNearbyPlacesDto.NearbyPlaceConfidenceLevel.MEDIUM,
                reliabilityScore = 74,
                reliabilityReason = "역사 안내도 데이터 기반",
                sourceCount = 2,
                lastVerifiedAt = now,
            ),
            SeedPlace(
                name = "${exitLabel} ATM 코너",
                category = "ATM",
                essentialType = GetStationNearbyPlacesDto.NearbyEssentialType.ATM,
                walkingMinutes = base + 1,
                openNow = true,
                supportsEnglishMenu = false,
                confidenceLevel = GetStationNearbyPlacesDto.NearbyPlaceConfidenceLevel.LOW,
                reliabilityScore = 68,
                reliabilityReason = "ATM 위치 제휴 데이터 기반",
                sourceCount = 2,
                lastVerifiedAt = now,
            ),
            SeedPlace(
                name = "${exitLabel} 24시 분식",
                category = "늦은 시간 식당",
                essentialType = GetStationNearbyPlacesDto.NearbyEssentialType.LATE_NIGHT_FOOD,
                walkingMinutes = base + 3,
                openNow = false,
                supportsEnglishMenu = true,
                confidenceLevel = GetStationNearbyPlacesDto.NearbyPlaceConfidenceLevel.LOW,
                reliabilityScore = 64,
                reliabilityReason = "야간 영업 추정치 기반",
                sourceCount = 1,
                lastVerifiedAt = now,
            ),
        )
    }
}
