package backend.team.ahachul_backend.api.community.adapter.web.`in`.dto.comment

import backend.team.ahachul_backend.api.community.domain.entity.CommunityCommentEntity

class UpdateCommunityCommentDto {

    data class Request(
        val content: String,
    ) {
        fun toCommand(id: Long): UpdateCommunityCommentCommand {
            return UpdateCommunityCommentCommand(
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
            fun from(entity: CommunityCommentEntity): Response {
                return Response(
                    id = entity.id,
                    content = entity.content
                )
            }
        }
    }
}