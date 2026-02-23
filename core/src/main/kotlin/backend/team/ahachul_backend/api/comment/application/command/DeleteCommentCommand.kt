package backend.team.ahachul_backend.api.comment.application.command

import backend.team.ahachul_backend.api.comment.domain.model.PostType

class DeleteCommentCommand(
    val id: Long,
    val postId: Long? = null,
    val postType: PostType? = null,
) {
}
