package backend.team.ahachul_backend.api.station.adapter.`in`

import backend.team.ahachul_backend.api.station.adapter.`in`.dto.GetStationNearbyPlacesDto
import backend.team.ahachul_backend.api.station.application.port.`in`.StationNearbyPlacesUseCase
import backend.team.ahachul_backend.common.response.CommonResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class StationNearbyPlacesController(
    private val stationNearbyPlacesUseCase: StationNearbyPlacesUseCase,
) {

    @GetMapping("/v2/stations/nearby-places")
    fun getNearbyPlaces(request: GetStationNearbyPlacesDto.Request): CommonResponse<GetStationNearbyPlacesDto.Response> {
        val result = stationNearbyPlacesUseCase.getNearbyPlaces(request.toCommand())
        return CommonResponse.success(result)
    }
}
