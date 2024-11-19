package backend.team.ahachul_backend.api.comment.adapter.web.`in`.dto

import backend.team.ahachul_backend.api.comment.application.command.UpdateCommentCommand
import backend.team.ahachul_backend.api.comment.domain.entity.CommentEntity

class UpdateCommentDto {

    data class Request(
        val content: String,
    ) {
        fun toCommand(id: Long): UpdateCommentCommand {
            return UpdateCommentCommand(
                id = id,
                content = content
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