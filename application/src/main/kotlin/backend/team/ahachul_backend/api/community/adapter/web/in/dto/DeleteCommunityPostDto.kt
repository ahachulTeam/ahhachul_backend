package backend.team.ahachul_backend.api.community.adapter.web.`in`.dto

import backend.team.ahachul_backend.api.community.application.command.`in`.DeleteCommunityPostCommand

class DeleteCommunityPostDto {

    data class Response(
        val id: Long,
    ) {
        fun toCommand(): DeleteCommunityPostCommand {
            return DeleteCommunityPostCommand(
                id = id
            )
        }
    }
}