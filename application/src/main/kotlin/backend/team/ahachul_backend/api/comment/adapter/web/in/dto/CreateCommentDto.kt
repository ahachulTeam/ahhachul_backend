package backend.team.ahachul_backend.api.comment.adapter.web.`in`.dto

import backend.team.ahachul_backend.api.comment.application.command.CreateCommentCommand
import backend.team.ahachul_backend.api.comment.domain.entity.CommentEntity

class CreateCommentDto {

    data class Request(
        val postId: Long,
        val upperCommentId: Long?,
        val content: String,
    ) {
        fun toCommand(): CreateCommentCommand {
            return CreateCommentCommand(
                postId = postId,
                upperCommentId = upperCommentId,
                content = content
            )
        }
    }

    data class Response(
        val id: Long,
        val upperCommentId: Long?,
        val content: String,
    ) {
        companion object {
            fun from(entity: CommentEntity): Response {
                return Response(
                    id = entity.id,
                    upperCommentId = entity.upperComment?.id,
                    content = entity.content
                )
            }
        }
    }
}