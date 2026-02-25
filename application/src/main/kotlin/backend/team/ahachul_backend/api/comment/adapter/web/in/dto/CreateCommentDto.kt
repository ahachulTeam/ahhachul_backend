package backend.team.ahachul_backend.api.comment.adapter.web.`in`.dto

import backend.team.ahachul_backend.api.comment.application.command.CreateCommentCommand
import backend.team.ahachul_backend.api.comment.domain.entity.CommentEntity
import backend.team.ahachul_backend.api.comment.domain.model.CommentVisibility
import backend.team.ahachul_backend.api.comment.domain.model.PostType
import backend.team.ahachul_backend.common.exception.CommonException
import backend.team.ahachul_backend.common.response.ResponseCode

class CreateCommentDto {

    data class Request(
        val upperCommentId: Long?,
        val content: String,
        val isPrivate: Boolean?,
        val imageUrls: List<String>? = null,
    ) {
        init {
            validateChildComment()
        }

        fun toCommand(postId: Long, postType: PostType): CreateCommentCommand {
            return CreateCommentCommand(
                postId = postId,
                postType = postType,
                upperCommentId = upperCommentId,
                content = content,
                imageUrls = imageUrls,
                visibility = CommentVisibility.from(isPrivate)
            )
        }

        private fun validateChildComment() {
            if (isInvalidChildComment()) {
                throw CommonException(ResponseCode.BAD_REQUEST)
            }
        }

        private fun isInvalidChildComment(): Boolean {
            return upperCommentId != null && isPrivate != null
        }
    }

    data class Response(
        val id: Long,
        val upperCommentId: Long?,
        val content: String,
        val imageUrls: List<String> = emptyList(),
    ) {
        companion object {
            fun from(entity: CommentEntity, imageUrls: List<String> = emptyList()): Response {
                return Response(
                    id = entity.id,
                    upperCommentId = entity.upperComment?.id,
                    content = entity.content,
                    imageUrls = imageUrls,
                )
            }
        }
    }
}
