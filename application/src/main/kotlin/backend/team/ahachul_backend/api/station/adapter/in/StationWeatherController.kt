package backend.team.ahachul_backend.api.station.adapter.`in`

import backend.team.ahachul_backend.api.station.adapter.`in`.dto.GetStationWeatherBriefDto
import backend.team.ahachul_backend.api.station.application.port.`in`.StationWeatherUseCase
import backend.team.ahachul_backend.common.response.CommonResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class StationWeatherController(
    private val stationWeatherUseCase: StationWeatherUseCase,
) {

    @GetMapping("/v2/stations/weather/brief")
    fun getStationWeatherBrief(request: GetStationWeatherBriefDto.Request): CommonResponse<GetStationWeatherBriefDto.Response> {
        val result = stationWeatherUseCase.getStationWeatherBrief(request.toCommand())
        return CommonResponse.success(result)
    }
}
