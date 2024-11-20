package backend.team.ahachul_backend.api.comment.adapter.web.`in`.dto

import backend.team.ahachul_backend.api.comment.application.command.CreateCommentCommand
import backend.team.ahachul_backend.api.comment.domain.entity.CommentEntity import backend.team.ahachul_backend.api.comment.domain.model.CommentVisibility
import backend.team.ahachul_backend.api.comment.domain.model.PostType

class CreateCommentDto {

    data class Request(
        val postId: Long,
        val upperCommentId: Long?,
        val content: String,
        val isPrivate: Boolean?
    ) {
        fun toCommand(postType: PostType): CreateCommentCommand {
            return CreateCommentCommand(
                postId = postId,
                postType = postType,
                upperCommentId = upperCommentId,
                content = content,
                visibility = CommentVisibility.fromIsPrivate(isPrivate)
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