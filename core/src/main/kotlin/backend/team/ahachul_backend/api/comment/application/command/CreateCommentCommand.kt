package backend.team.ahachul_backend.api.comment.application.command

import backend.team.ahachul_backend.api.comment.domain.model.CommentVisibility
import backend.team.ahachul_backend.api.comment.domain.model.PostType

class CreateCommentCommand(
    val postId: Long,
    val postType: PostType,
    val upperCommentId: Long?,
    val content: String,
    val visibility: CommentVisibility,
    val isPrivate: Boolean
) {
}