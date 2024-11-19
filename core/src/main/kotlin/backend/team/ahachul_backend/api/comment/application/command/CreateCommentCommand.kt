package backend.team.ahachul_backend.api.comment.application.command

class CreateCommentCommand(
    val postId: Long,
    val upperCommentId: Long?,
    val content: String,
) {
}