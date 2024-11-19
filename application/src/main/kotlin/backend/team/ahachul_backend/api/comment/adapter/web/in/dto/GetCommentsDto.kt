package backend.team.ahachul_backend.api.comment.adapter.web.`in`.dto

import backend.team.ahachul_backend.api.comment.domain.model.CommentType
import java.time.LocalDateTime

class GetCommentsDto {

    data class Response(
        val comments: List<CommentList>
    )

    data class CommentList(
        val parentComment: Comment,
        val childComments: List<Comment>
    )

    data class Comment(
        val id: Long,
        val upperCommentId: Long?,
        val content: String,
        val status: CommentType,
        val createdAt: LocalDateTime,
        val createdBy: String,
        val writer: String,
    )
}