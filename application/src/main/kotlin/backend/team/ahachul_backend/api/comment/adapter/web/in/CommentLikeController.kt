package backend.team.ahachul_backend.api.comment.adapter.web.`in`

import backend.team.ahachul_backend.api.comment.application.port.`in`.CommentLikeUseCase
import backend.team.ahachul_backend.common.annotation.Authentication
import backend.team.ahachul_backend.common.response.CommonResponse
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class CommentLikeController(
    private val commentLikeUseCase: CommentLikeUseCase
) {

    @Authentication
    @PostMapping("/v1/comments/{commentId}/likes")
    fun like(@PathVariable commentId: Long): CommonResponse<*> {
        commentLikeUseCase.like(commentId)
        return CommonResponse.success()
    }

    @Authentication
    @DeleteMapping("/v1/comments/{commentId}/likes")
    fun notLike(@PathVariable commentId: Long): CommonResponse<*> {
        commentLikeUseCase.notLike(commentId)
        return CommonResponse.success()
    }
}