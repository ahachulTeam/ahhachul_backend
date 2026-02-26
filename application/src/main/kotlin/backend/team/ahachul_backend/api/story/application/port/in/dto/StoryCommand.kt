package backend.team.ahachul_backend.api.story.application.port.`in`.dto

import org.springframework.web.multipart.MultipartFile

data class CreateStoryCommand(
    val caption: String?,
    val stationId: Long?,
    val subwayLineId: Long?,
    val imageFile: MultipartFile,
)

data class GetMemberStoriesCommand(
    val nickname: String,
    val asPublic: Boolean,
    val limit: Int,
)
