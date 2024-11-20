package backend.team.ahachul_backend.api.comment.adapter.web.`in`

import backend.team.ahachul_backend.api.comment.adapter.web.`in`.dto.DeleteCommentDto
import backend.team.ahachul_backend.api.comment.adapter.web.`in`.dto.UpdateCommentDto
import backend.team.ahachul_backend.api.comment.application.command.DeleteCommentCommand
import backend.team.ahachul_backend.api.comment.application.port.`in`.CommentUseCase
import backend.team.ahachul_backend.common.annotation.Authentication
import backend.team.ahachul_backend.common.response.CommonResponse
import org.springframework.web.bind.annotation.*

@RestController
class CommentController(
    private val commentUseCase: CommentUseCase
) {

    @Authentication
    @PatchMapping("/v1/comments/{commentId}")
    fun updateComment(@PathVariable commentId: Long, @RequestBody request: UpdateCommentDto.Request): CommonResponse<UpdateCommentDto.Response> {
        return CommonResponse.success(commentUseCase.updateComment(request.toCommand(commentId)))
    }

    @Authentication
    @DeleteMapping("/v1/comments/{commentId}")
    fun deleteComment(@PathVariable commentId: Long): CommonResponse<DeleteCommentDto.Response> {
        return CommonResponse.success(commentUseCase.deleteComment(DeleteCommentCommand(commentId)))
    }
}