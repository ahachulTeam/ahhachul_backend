package backend.team.ahachul_backend.api.community.adapter.web.`in`

import backend.team.ahachul_backend.api.community.adapter.web.`in`.dto.*
import backend.team.ahachul_backend.api.community.adapter.web.`in`.dto.post.*
import backend.team.ahachul_backend.api.community.application.port.`in`.CommunityPostLikeUseCase
import backend.team.ahachul_backend.common.annotation.Authentication
import backend.team.ahachul_backend.common.response.CommonResponse
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class CommunityPostLikeController(
    private val communityPostLikeUseCase: CommunityPostLikeUseCase
) {

    @Authentication
    @PostMapping("/v1/community-posts/{postId}/like")
    fun like(@PathVariable postId: Long): CommonResponse<*> {
        communityPostLikeUseCase.like(postId)
        return CommonResponse.success()
    }

    @Authentication
    @DeleteMapping("/v1/community-posts/{postId}/like")
    fun notLike(@PathVariable postId: Long): CommonResponse<*> {
        communityPostLikeUseCase.notLike(postId)
        return CommonResponse.success()
    }

    @Authentication
    @PostMapping("/v1/community-posts/{postId}/hate")
    fun hate(@PathVariable postId: Long): CommonResponse<*> {
        communityPostLikeUseCase.hate(postId)
        return CommonResponse.success()
    }

    @Authentication
    @DeleteMapping("/v1/community-posts/{postId}/hate")
    fun notHate(@PathVariable postId: Long): CommonResponse<*> {
        communityPostLikeUseCase.notHate(postId)
        return CommonResponse.success()
    }
}