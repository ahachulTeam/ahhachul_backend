package backend.team.ahachul_backend.api.community.adapter.web.`in`.dto.comment

import backend.team.ahachul_backend.api.community.domain.entity.CommunityCommentEntity

class CreateCommunityCommentDto {

    data class Request(
        val postId: Long,
        val upperCommentId: Long?,
        val content: String,
    ) {
        fun toCommand(): CreateCommunityCommentCommand {
            return CreateCommunityCommentCommand(
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
            fun from(entity: CommunityCommentEntity): Response {
                return Response(
                    id = entity.id,
                    upperCommentId = entity.upperCommunityComment?.id,
                    content = entity.content
                )
            }
        }
    }
}