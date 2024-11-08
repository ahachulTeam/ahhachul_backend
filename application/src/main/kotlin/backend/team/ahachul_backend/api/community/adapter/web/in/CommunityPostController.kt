package backend.team.ahachul_backend.api.community.adapter.web.`in`

import backend.team.ahachul_backend.api.community.adapter.web.`in`.dto.*
import backend.team.ahachul_backend.api.community.adapter.web.`in`.dto.post.*
import backend.team.ahachul_backend.api.community.application.port.`in`.CommunityPostUseCase
import backend.team.ahachul_backend.common.annotation.Authentication
import backend.team.ahachul_backend.common.response.CommonResponse
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*

@RestController
class CommunityPostController(
    private val communityPostUseCase: CommunityPostUseCase
) {

    @Authentication(required = false)
    @GetMapping("/v1/community-posts")
    fun searchCommunityPosts(
        pageable: Pageable,
        request: SearchCommunityPostDto.Request
    ): CommonResponse<SearchCommunityPostDto.Response> {
        return CommonResponse.success(communityPostUseCase.searchCommunityPosts(request.toCommand(pageable)))
    }

    @Authentication(required = false)
    @GetMapping("/v1/community-posts/{postId}")
    fun getCommunityPost(@PathVariable postId: Long): CommonResponse<GetCommunityPostDto.Response> {
        return CommonResponse.success(communityPostUseCase.getCommunityPost(GetCommunityPostCommand(postId)))
    }

    @Authentication
    @PostMapping("/v1/community-posts")
    fun createCommunityPost(request: CreateCommunityPostDto.Request): CommonResponse<CreateCommunityPostDto.Response> {
        return CommonResponse.success(communityPostUseCase.createCommunityPost(request.toCommand()))
    }

    @Authentication
    @PostMapping("/v1/community-posts/{postId}")
    fun updateCommunityPost(@PathVariable postId: Long, request: UpdateCommunityPostDto.Request): CommonResponse<UpdateCommunityPostDto.Response> {
        return CommonResponse.success(communityPostUseCase.updateCommunityPost(request.toCommand(postId)))
    }

    @Authentication
    @DeleteMapping("/v1/community-posts/{postId}")
    fun deleteCommunityPost(@PathVariable postId: Long): CommonResponse<DeleteCommunityPostDto.Response> {
        return CommonResponse.success(communityPostUseCase.deleteCommunityPost(DeleteCommunityPostCommand(postId)))
    }
}
