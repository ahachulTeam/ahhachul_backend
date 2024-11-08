package backend.team.ahachul_backend.api.community.adapter.web.`in`

import backend.team.ahachul_backend.api.community.adapter.web.`in`.dto.comment.*
import backend.team.ahachul_backend.api.community.application.port.`in`.CommunityCommentUseCase
import backend.team.ahachul_backend.common.annotation.Authentication
import backend.team.ahachul_backend.common.response.CommonResponse
import org.springframework.web.bind.annotation.*

@RestController
class CommunityCommentController(
    private val communityCommentUseCase: CommunityCommentUseCase
) {

    @GetMapping("/v1/community-comments")
    fun getCommunityComments(@RequestParam postId: Long): CommonResponse<GetCommunityCommentsDto.Response> {
        return CommonResponse.success(communityCommentUseCase.getCommunityComments(GetCommunityCommentsCommand(postId)))
    }

    @Authentication
    @PostMapping("/v1/community-comments")
    fun createCommunityComment(@RequestBody request: CreateCommunityCommentDto.Request): CommonResponse<CreateCommunityCommentDto.Response> {
        return CommonResponse.success(communityCommentUseCase.createCommunityComment(request.toCommand()))
    }

    @Authentication
    @PatchMapping("/v1/community-comments/{commentId}")
    fun updateCommunityComment(@PathVariable commentId: Long, @RequestBody request: UpdateCommunityCommentDto.Request): CommonResponse<UpdateCommunityCommentDto.Response> {
        return CommonResponse.success(communityCommentUseCase.updateCommunityComment(request.toCommand(commentId)))
    }

    @Authentication
    @DeleteMapping("/v1/community-comments/{commentId}")
    fun deleteCommunityComment(@PathVariable commentId: Long): CommonResponse<DeleteCommunityCommentDto.Response> {
        return CommonResponse.success(communityCommentUseCase.deleteCommunityComment(DeleteCommunityCommentCommand(commentId)))
    }
}