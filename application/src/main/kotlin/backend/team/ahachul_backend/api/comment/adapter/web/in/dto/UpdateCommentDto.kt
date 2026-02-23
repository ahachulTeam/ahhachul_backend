package backend.team.ahachul_backend.api.comment.adapter.web.`in`.dto

import backend.team.ahachul_backend.api.comment.application.command.UpdateCommentCommand
import backend.team.ahachul_backend.api.comment.domain.entity.CommentEntity
import backend.team.ahachul_backend.api.comment.domain.model.PostType

class UpdateCommentDto {

    data class Request(
        val content: String,
    ) {
        fun toCommand(id: Long, postId: Long? = null, postType: PostType? = null): UpdateCommentCommand {
            return UpdateCommentCommand(
                id = id,
                content = content,
                postId = postId,
                postType = postType,
            )
        }
    }

    data class Response(
        val id: Long,
        val content: String,
    ) {
        companion object {
            fun from(entity: CommentEntity): Response {
                return Response(
                    id = entity.id,
                    content = entity.content
                )
            }
        }
    }
}
