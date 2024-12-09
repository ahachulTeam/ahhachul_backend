package backend.team.ahachul_backend.api.comment.application.command

import backend.team.ahachul_backend.api.comment.domain.model.PostType

class GetCommentsCommand(
    val postId: Long,
    val postType: PostType
) {
}