package backend.team.ahachul_backend.api.community.adapter.web.`in`

import backend.team.ahachul_backend.api.comment.adapter.web.`in`.dto.CreateCommentDto
import backend.team.ahachul_backend.api.comment.adapter.web.`in`.dto.GetCommentsDto
import backend.team.ahachul_backend.api.comment.application.command.GetCommentsCommand
import backend.team.ahachul_backend.api.comment.application.port.`in`.CommentUseCase
import backend.team.ahachul_backend.api.comment.domain.model.PostType
import backend.team.ahachul_backend.common.annotation.Authentication
import backend.team.ahachul_backend.common.response.CommonResponse
import org.springframework.web.bind.annotation.*

@RestController
class CommunityPostCommentController(
    private val commentUseCase: CommentUseCase
) {

    @GetMapping("/v1/community-posts/{postId}/comments")
    fun getCommunityPostComments(@PathVariable postId: Long): CommonResponse<GetCommentsDto.Response> {
        return CommonResponse.success(commentUseCase.getComments(GetCommentsCommand(postId)))
    }

    @Authentication
    @PostMapping("/v1/community-posts/{postId}/comments")
    fun createCommunityPostComment(@PathVariable postId: Long, @RequestBody request: CreateCommentDto.Request): CommonResponse<CreateCommentDto.Response> {
        return CommonResponse.success(commentUseCase.createComment(request.toCommand(postId, PostType.COMMUNITY)))
    }
}