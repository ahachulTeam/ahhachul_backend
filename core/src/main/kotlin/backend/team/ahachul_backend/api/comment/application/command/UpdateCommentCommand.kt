package backend.team.ahachul_backend.api.comment.application.command

import backend.team.ahachul_backend.api.comment.domain.model.PostType

class UpdateCommentCommand(
    val id: Long,
    val content: String,
    val postId: Long? = null,
    val postType: PostType? = null,
) {
}
