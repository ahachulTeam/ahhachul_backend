package backend.team.ahachul_backend.api.station.adapter.`in`

import backend.team.ahachul_backend.api.station.adapter.`in`.dto.GetStationTimesDto
import backend.team.ahachul_backend.api.station.application.port.`in`.StationUseCase
import backend.team.ahachul_backend.common.response.CommonResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class StationController(
    private val stationUseCase: StationUseCase
) {

    @GetMapping("/v1/stations/times")
    fun getStationTimes(request: GetStationTimesDto.Request): CommonResponse<GetStationTimesDto.Response> {
        val result = stationUseCase.getStationTimes(request.toCommand())
        return CommonResponse.success(result)
    }

    @GetMapping("/v2/stations/times/summary")
    fun getStationTimesSummary(request: GetStationTimesDto.SummaryRequest): CommonResponse<GetStationTimesDto.SummaryResponse> {
        val result = stationUseCase.getStationTimesSummary(request.toCommand())
        return CommonResponse.success(result)
    }

    @GetMapping("/v2/stations/times/last-train-risk")
    fun getLastTrainRisk(request: GetStationTimesDto.LastTrainRiskRequest): CommonResponse<GetStationTimesDto.LastTrainRiskResponse> {
        val result = stationUseCase.getLastTrainRisk(request.toCommand())
        return CommonResponse.success(result)
    }
}
