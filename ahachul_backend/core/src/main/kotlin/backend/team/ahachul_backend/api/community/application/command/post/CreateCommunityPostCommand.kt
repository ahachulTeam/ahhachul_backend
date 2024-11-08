package backend.team.ahachul_backend.api.community.adapter.web.`in`.dto.post

import backend.team.ahachul_backend.api.community.domain.model.CommunityCategoryType
import org.springframework.web.multipart.MultipartFile

class CreateCommunityPostCommand(
    val title: String,
    val content: String,
    val categoryType: CommunityCategoryType,
    val hashTags: List<String> = arrayListOf(),
    val subwayLineId: Long,
    val imageFiles: List<MultipartFile> = arrayListOf(),
) {
}