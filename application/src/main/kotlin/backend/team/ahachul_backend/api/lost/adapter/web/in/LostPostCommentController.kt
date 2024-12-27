package backend.team.ahachul_backend.api.lost.adapter.web.`in`

import backend.team.ahachul_backend.api.comment.adapter.web.`in`.dto.CreateCommentDto
import backend.team.ahachul_backend.api.comment.adapter.web.`in`.dto.GetCommentsDto
import backend.team.ahachul_backend.api.comment.application.port.`in`.CommentUseCase
import backend.team.ahachul_backend.api.comment.domain.model.PostType
import backend.team.ahachul_backend.common.annotation.Authentication
import backend.team.ahachul_backend.common.response.CommonResponse
import org.springframework.web.bind.annotation.*

@RestController
class LostPostCommentController(
    private val commentUseCase: CommentUseCase
) {

    @Authentication(required = false)
    @GetMapping("/v1/lost-posts/{lostId}/comments")
    fun getLostPostComments(@PathVariable lostId: Long, request: GetCommentsDto.Request): CommonResponse<GetCommentsDto.Response> {
        return CommonResponse.success(commentUseCase.getComments(request.toCommand(lostId, PostType.LOST)))
    }

    @Authentication
    @PostMapping("/v1/lost-posts/{lostId}/comments")
    fun createLostPostComment(@PathVariable lostId: Long, @RequestBody request: CreateCommentDto.Request): CommonResponse<CreateCommentDto.Response> {
        return CommonResponse.success(commentUseCase.createComment(request.toCommand(lostId, PostType.LOST)))
    }
}