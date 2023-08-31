package backend.team.ahachul_backend.api.train.adapter.`in`

import backend.team.ahachul_backend.api.train.adapter.`in`.dto.GetCongestionDto
import backend.team.ahachul_backend.api.train.adapter.`in`.dto.GetTrainDto
import backend.team.ahachul_backend.api.train.adapter.`in`.dto.GetTrainRealTimesDto
import backend.team.ahachul_backend.api.train.application.port.`in`.TrainUseCase
import backend.team.ahachul_backend.api.train.application.service.TrainCongestionService
import backend.team.ahachul_backend.common.annotation.Authentication
import backend.team.ahachul_backend.common.response.CommonResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class TrainController(
    private val trainUseCase: TrainUseCase,
    private val trainCongestionService: TrainCongestionService
) {

    @Authentication
    @GetMapping("/v1/trains/{trainNo}")
    fun getTrain(@PathVariable trainNo: String): CommonResponse<GetTrainDto.Response> {
        return CommonResponse.success(trainUseCase.getTrain(trainNo))
    }
    
    @GetMapping("/v1/trains/real-times")
    fun getTrainRealTimes(request: GetTrainRealTimesDto.Request): CommonResponse<GetTrainRealTimesDto.Response> {
        val result = trainUseCase.getTrainRealTimes(request.stationId)
        return CommonResponse.success(GetTrainRealTimesDto.Response(result))
    }

    @Authentication
    @GetMapping("/v1/trains/real-times/congestion/{stationId}")
    fun getCongestion(@PathVariable("stationId") stationId: Long): CommonResponse<GetCongestionDto.Response> {
        val result = trainCongestionService.getTrainCongestion(stationId)
        return CommonResponse.success(result)
    }
}
