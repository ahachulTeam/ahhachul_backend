package backend.team.ahachul_backend.api.delayproof.adapter.`in`

import backend.team.ahachul_backend.api.delayproof.adapter.`in`.dto.DelayProofDto
import backend.team.ahachul_backend.api.delayproof.application.port.`in`.DelayProofUseCase
import backend.team.ahachul_backend.common.annotation.Authentication
import backend.team.ahachul_backend.common.response.CommonResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class DelayProofController(
    private val delayProofUseCase: DelayProofUseCase,
) {

    @Authentication
    @PostMapping("/v2/delay-proofs")
    fun createDelayProof(@RequestBody request: DelayProofDto.CreateRequest): CommonResponse<DelayProofDto.CreateResponse> {
        return CommonResponse.success(delayProofUseCase.createDelayProof(request.toCommand()))
    }

    @GetMapping("/v2/delay-proofs/{proofId}")
    fun getDelayProof(@PathVariable proofId: String): CommonResponse<DelayProofDto.GetResponse> {
        return CommonResponse.success(delayProofUseCase.getDelayProof(proofId))
    }

    @GetMapping("/v2/subway/incidents")
    fun getSubwayIncidents(request: DelayProofDto.GetSubwayIncidentsRequest): CommonResponse<DelayProofDto.GetSubwayIncidentsResponse> {
        return CommonResponse.success(delayProofUseCase.getSubwayIncidents(request.toCommand()))
    }

    @GetMapping("/v2/community/delay-signals")
    fun getCommunityDelaySignals(request: DelayProofDto.GetCommunityDelaySignalsRequest): CommonResponse<DelayProofDto.GetCommunityDelaySignalsResponse> {
        return CommonResponse.success(delayProofUseCase.getCommunityDelaySignals(request.toCommand()))
    }
}
