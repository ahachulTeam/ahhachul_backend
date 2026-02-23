package backend.team.ahachul_backend.api.train.adapter.`in`

import backend.team.ahachul_backend.api.train.adapter.`in`.dto.GetCongestionDto
import backend.team.ahachul_backend.api.train.adapter.`in`.dto.GetTrainDto
import backend.team.ahachul_backend.api.train.adapter.`in`.dto.GetTrainRealTimesDto
import backend.team.ahachul_backend.api.train.adapter.`in`.dto.GetTrainRealTimesV2Dto
import backend.team.ahachul_backend.api.train.application.port.`in`.TrainUseCase
import backend.team.ahachul_backend.common.annotation.Authentication
import backend.team.ahachul_backend.common.response.CommonResponse
import org.springframework.web.bind.annotation.*

@RestController
class TrainController(
    private val trainUseCase: TrainUseCase
) {

    @Authentication
    @GetMapping("/v1/trains/{trainNo}")
    fun getTrain(@PathVariable trainNo: String): CommonResponse<GetTrainDto.Response> {
        return CommonResponse.success(trainUseCase.getTrain(trainNo))
    }

    @GetMapping("/v1/trains/real-times")
    fun getTrainRealTimes(request: GetTrainRealTimesDto.Request): CommonResponse<GetTrainRealTimesDto.Response> {
        val result = trainUseCase.getTrainRealTimes(request.stationId, request.subwayLineId, request.upDownType)
        return CommonResponse.success(GetTrainRealTimesDto.Response(result))
    }

    @GetMapping("/v2/trains/real-times")
    fun getTrainRealTimesV2(request: GetTrainRealTimesV2Dto.Request): CommonResponse<GetTrainRealTimesV2Dto.Response> {
        val result = trainUseCase.getTrainRealTimesV2(
            request.stationId,
            request.subwayLineId,
            request.upDownType,
            request.limit,
        )
        return CommonResponse.success(result)
    }

    @Authentication
    @GetMapping("/v1/trains/real-times/congestion")
    fun getCongestion(request: GetCongestionDto.Request): CommonResponse<GetCongestionDto.Response> {
        val result = trainUseCase.getTrainCongestion(request.toCommand())
        return CommonResponse.success(result)
    }
}
