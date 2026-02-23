package backend.team.ahachul_backend.api.station.application.port.`in`

import backend.team.ahachul_backend.api.station.adapter.`in`.dto.GetStationNearbyPlacesDto
import backend.team.ahachul_backend.api.station.application.port.`in`.dto.GetStationNearbyPlacesCommand

interface StationNearbyPlacesUseCase {

    fun getNearbyPlaces(command: GetStationNearbyPlacesCommand): GetStationNearbyPlacesDto.Response
}
