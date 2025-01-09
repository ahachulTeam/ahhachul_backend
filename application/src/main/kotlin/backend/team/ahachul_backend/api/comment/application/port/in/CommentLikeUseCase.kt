package backend.team.ahachul_backend.api.comment.application.port.`in`

interface CommentLikeUseCase {

    fun like(commentId: Long)

    fun notLike(commentId: Long)
}