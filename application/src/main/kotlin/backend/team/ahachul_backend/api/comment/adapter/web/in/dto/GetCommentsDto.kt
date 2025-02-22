package backend.team.ahachul_backend.api.comment.adapter.web.`in`.dto

import backend.team.ahachul_backend.api.comment.application.command.GetCommentsCommand
import backend.team.ahachul_backend.api.comment.domain.model.CommentType
import backend.team.ahachul_backend.api.comment.domain.model.PostType
import org.springframework.data.domain.Sort
import java.time.LocalDateTime

class GetCommentsDto {

    data class Request(
        val sort: String
    ) {
        fun toCommand(postId: Long, postType: PostType): GetCommentsCommand {
            return GetCommentsCommand(
                postId = postId,
                postType = postType,
                sort = toSort()
            )
        }

        private fun toSort(): Sort {
            val parts = sort.split(",")
            return Sort.by(Sort.Direction.fromString(parts[1]), parts[0])
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
        val status: CommentType,
        val createdAt: LocalDateTime,
        val createdBy: String,
        val writer: String,
        val isPrivate: Boolean,
        val likeCnt: Long
    )
}