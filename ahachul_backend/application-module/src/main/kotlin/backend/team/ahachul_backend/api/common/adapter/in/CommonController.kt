package backend.team.ahachul_backend.api.common.adapter.`in`

import backend.team.ahachul_backend.api.common.adapter.`in`.dto.SearchSubwayLineDto
import backend.team.ahachul_backend.api.common.application.port.`in`.SubwayLineUseCase
import backend.team.ahachul_backend.common.response.CommonResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class CommonController(
    private val subwayLineUseCase: SubwayLineUseCase
) {

    @GetMapping("/v1/subway-lines")
    fun searchSubwayLines(): CommonResponse<SearchSubwayLineDto.Response> {
        return CommonResponse.success(subwayLineUseCase.searchSubwayLines())
    }
}
