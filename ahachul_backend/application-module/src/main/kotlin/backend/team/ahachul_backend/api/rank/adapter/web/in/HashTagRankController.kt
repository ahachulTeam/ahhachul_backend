package backend.team.ahachul_backend.api.rank.adapter.web.`in`

import backend.team.ahachul_backend.api.rank.adapter.web.`in`.dto.GetHashTagRankDto
import backend.team.ahachul_backend.api.rank.application.service.HashTagRankService
import backend.team.ahachul_backend.common.response.CommonResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HashTagRankController(
    private val hashTagRankService: HashTagRankService
) {

    @GetMapping("/v1/ranks/hashtags")
    fun getHashTagRank(): CommonResponse<GetHashTagRankDto.Response> {
        return CommonResponse.success(hashTagRankService.getRank())
    }
}
