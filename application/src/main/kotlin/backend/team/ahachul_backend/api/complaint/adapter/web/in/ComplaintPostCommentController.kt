package backend.team.ahachul_backend.api.complaint.adapter.web.`in`

import backend.team.ahachul_backend.api.comment.adapter.web.`in`.dto.CreateCommentDto
import backend.team.ahachul_backend.api.comment.adapter.web.`in`.dto.GetCommentsDto
import backend.team.ahachul_backend.api.comment.application.port.`in`.CommentUseCase
import backend.team.ahachul_backend.api.comment.domain.model.PostType
import backend.team.ahachul_backend.common.annotation.Authentication
import backend.team.ahachul_backend.common.response.CommonResponse
import org.springframework.web.bind.annotation.*

@RestController
class ComplaintPostCommentController(
    private val commentUseCase: CommentUseCase
) {

    @Authentication(required = false)
    @GetMapping("/v1/complaint-posts/{postId}/comments")
    fun getComplaintPostComments(@PathVariable postId: Long, request: GetCommentsDto.Request): CommonResponse<GetCommentsDto.Response> {
        return CommonResponse.success(commentUseCase.getComments(request.toCommand(postId, PostType.COMPLAINT)))
    }

    @Authentication
    @PostMapping("/v1/complaint-posts/{postId}/comments")
    fun createComplaintPostComment(@PathVariable postId: Long, @RequestBody request: CreateCommentDto.Request): CommonResponse<CreateCommentDto.Response> {
        return CommonResponse.success(commentUseCase.createComment(request.toCommand(postId, PostType.COMPLAINT)))
    }
}