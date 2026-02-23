package backend.team.ahachul_backend.api.station.application.service

import backend.team.ahachul_backend.api.station.adapter.`in`.dto.GetStationNearbyPlacesDto
import backend.team.ahachul_backend.api.station.application.port.`in`.StationNearbyPlacesUseCase
import backend.team.ahachul_backend.api.station.application.port.`in`.dto.GetStationNearbyPlacesCommand
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class StationNearbyPlacesService : StationNearbyPlacesUseCase {

    override fun getNearbyPlaces(command: GetStationNearbyPlacesCommand): GetStationNearbyPlacesDto.Response {
        val places = StationNearbyPlacesRecommendationGenerator.generate(
            stationId = command.stationId,
            subwayLineId = command.subwayLineId,
            exitNo = command.exitNo,
            limit = command.limit ?: 3,
        )

        return GetStationNearbyPlacesDto.Response(
            stationId = command.stationId,
            subwayLineId = command.subwayLineId,
            exitNo = command.exitNo,
            places = places,
        )
    }
}
