package backend.team.ahachul_backend.api.foreigner.adapter.`in`

import backend.team.ahachul_backend.api.foreigner.adapter.`in`.dto.ForeignerModeDto
import backend.team.ahachul_backend.api.foreigner.application.port.`in`.ForeignerModeUseCase
import backend.team.ahachul_backend.common.annotation.Authentication
import backend.team.ahachul_backend.common.response.CommonResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class ForeignerModeController(
    private val foreignerModeUseCase: ForeignerModeUseCase,
) {

    @GetMapping("/v2/foreigner/stations/guide")
    fun getStationGuide(
        request: ForeignerModeDto.StationGuideRequest,
    ): CommonResponse<ForeignerModeDto.StationGuideResponse> {
        val result = foreignerModeUseCase.getStationGuide(request.toCommand())
        return CommonResponse.success(result)
    }

    @Authentication(required = false)
    @GetMapping("/v2/foreigner/community-posts/{postId}/translation")
    fun translateCommunityPost(
        @PathVariable postId: Long,
        request: ForeignerModeDto.CommunityPostTranslationRequest,
    ): CommonResponse<ForeignerModeDto.CommunityPostTranslationResponse> {
        val result = foreignerModeUseCase.translateCommunityPost(request.toCommand(postId))
        return CommonResponse.success(result)
    }
}
