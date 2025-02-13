package backend.team.ahachul_backend.api.community.adapter.web.`in`

import backend.team.ahachul_backend.api.community.adapter.web.`in`.dto.*
import backend.team.ahachul_backend.api.community.application.command.`in`.DeleteCommunityPostCommand
import backend.team.ahachul_backend.api.community.application.command.`in`.GetCommunityPostCommand
import backend.team.ahachul_backend.api.community.application.port.`in`.CommunityPostUseCase
import backend.team.ahachul_backend.api.lost.adapter.web.`in`.dto.CreateLostPostDto
import backend.team.ahachul_backend.common.annotation.Authentication
import backend.team.ahachul_backend.common.dto.PageInfoDto
import backend.team.ahachul_backend.common.response.CommonResponse
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
class CommunityPostController(
    private val communityPostUseCase: CommunityPostUseCase
) {

    @Authentication(required = false)
    @GetMapping("/v1/community-posts")
    fun searchCommunityPosts(
        @RequestParam(required = false) pageToken: String?,
        @RequestParam pageSize: Int,
        request: SearchCommunityPostDto.Request
    ): CommonResponse<PageInfoDto<SearchCommunityPostDto.Response>> {
        return CommonResponse.success(communityPostUseCase.searchCommunityPosts(request.toCommand(pageToken, pageSize)))
    }

    @Authentication(required = false)
    @GetMapping("/v1/community-hot-posts")
    fun searchCommunityHotPosts(
        @RequestParam(required = false) pageToken: String?,
        @RequestParam pageSize: Int,
        request: SearchCommunityHotPostDto.Request
    ): CommonResponse<PageInfoDto<SearchCommunityPostDto.Response>> {
        return CommonResponse.success(communityPostUseCase.searchCommunityHotPosts(request.toCommand(pageToken, pageSize)))
    }

    @Authentication(required = false)
    @GetMapping("/v1/community-posts/{postId}")
    fun getCommunityPost(@PathVariable postId: Long): CommonResponse<GetCommunityPostDto.Response> {
        return CommonResponse.success(communityPostUseCase.getCommunityPost(GetCommunityPostCommand(postId)))
    }

    @Authentication
    @PostMapping("/v1/community-posts")
    fun createCommunityPost(
        @RequestPart(value = "content") request: CreateCommunityPostDto.Request,
        @RequestPart(value = "files", required = false) imageFiles: List<MultipartFile>?
    ): CommonResponse<CreateCommunityPostDto.Response> {
        return CommonResponse.success(communityPostUseCase.createCommunityPost(request.toCommand(imageFiles)))
    }

    @Authentication
    @PostMapping("/v1/community-posts/{postId}")
    fun updateCommunityPost(
        @PathVariable postId: Long,
        @RequestPart(value = "content") request: UpdateCommunityPostDto.Request,
        @RequestPart(value = "files", required = false) imageFiles: List<MultipartFile>?
    ): CommonResponse<UpdateCommunityPostDto.Response> {
        return CommonResponse.success(communityPostUseCase.updateCommunityPost(request.toCommand(postId, imageFiles)))
    }

    @Authentication
    @DeleteMapping("/v1/community-posts/{postId}")
    fun deleteCommunityPost(@PathVariable postId: Long): CommonResponse<DeleteCommunityPostDto.Response> {
        return CommonResponse.success(communityPostUseCase.deleteCommunityPost(DeleteCommunityPostCommand(postId)))
    }
}
