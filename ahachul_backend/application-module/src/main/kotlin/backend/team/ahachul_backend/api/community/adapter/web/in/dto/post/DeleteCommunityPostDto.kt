package backend.team.ahachul_backend.api.community.adapter.web.`in`.dto.post

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