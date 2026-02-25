package backend.team.ahachul_backend.api.comment.adapter.web.`in`.dto

import backend.team.ahachul_backend.api.comment.application.command.GetCommentsCommand
import backend.team.ahachul_backend.api.comment.domain.model.CommentType
import backend.team.ahachul_backend.api.comment.domain.model.PostType
import org.springframework.data.domain.Sort
import java.time.LocalDateTime

class GetCommentsDto {

    data class Request(
        val sort: String? = null
    ) {
        fun toCommand(postId: Long, postType: PostType): GetCommentsCommand {
            return GetCommentsCommand(
                postId = postId,
                postType = postType,
                sort = toSort()
            )
        }

        private fun toSort(): Sort {
            val normalizedSort = sort?.trim().takeUnless { it.isNullOrBlank() } ?: "createdAt,desc"
            val parts = normalizedSort.split(",")
            val property = parts.getOrNull(0)?.takeUnless { it.isBlank() } ?: "createdAt"
            val direction = runCatching {
                Sort.Direction.fromString(parts.getOrNull(1)?.trim() ?: "desc")
            }.getOrDefault(Sort.Direction.DESC)

            return Sort.by(direction, property)
        }
    }

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
        val imageUrls: List<String> = emptyList(),
        val status: CommentType,
        val createdAt: LocalDateTime,
        val createdBy: String,
        val writer: String,
        val isPrivate: Boolean,
        val likeCnt: Long,
        val likedByMe: Boolean = false,
    )
}
