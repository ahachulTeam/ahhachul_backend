package backend.team.ahachul_backend.api.comment.application.command

import backend.team.ahachul_backend.api.comment.domain.model.PostType
import org.springframework.data.domain.Sort

class GetCommentsCommand(
    val postId: Long,
    val postType: PostType,
    val sort: Sort
) {
}