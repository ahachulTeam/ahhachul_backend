package backend.team.ahachul_backend.api.comment.domain

import backend.team.ahachul_backend.api.comment.domain.model.CommentType
import backend.team.ahachul_backend.api.comment.domain.model.CommentVisibility
import java.time.LocalDateTime

data class SearchComment(
    val id: Long,
    val upperCommentId: Long?,
    val content: String,
    val status: CommentType,
    val createdAt: LocalDateTime,
    val createdBy: String,
    val writer: String,
    val visibility: CommentVisibility,
    val likeCnt: Long
) {
}
