package backend.team.ahachul_backend.api.station.application.service

import backend.team.ahachul_backend.api.station.adapter.`in`.dto.GetStationNearbyPlacesDto
import backend.team.ahachul_backend.api.station.application.port.`in`.StationNearbyPlacesUseCase
import backend.team.ahachul_backend.api.station.application.port.`in`.dto.GetStationNearbyPlacesCommand
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

@Service
@Transactional(readOnly = true)
class StationNearbyPlacesService : StationNearbyPlacesUseCase {

    override fun getNearbyPlaces(command: GetStationNearbyPlacesCommand): GetStationNearbyPlacesDto.Response {
        val places = StationNearbyPlacesRecommendationGenerator.generate(
            stationId = command.stationId,
            subwayLineId = command.subwayLineId,
            exitNo = command.exitNo,
            limit = command.limit ?: 4,
        )

        return GetStationNearbyPlacesDto.Response(
            generatedAt = OffsetDateTime.now().toString(),
            stationId = command.stationId,
            subwayLineId = command.subwayLineId,
            exitNo = command.exitNo,
            summary = "편의점/화장실/ATM/늦은 식당 중심으로 신뢰도 기반 추천을 제공합니다.",
            places = places,
        )
    }
}
