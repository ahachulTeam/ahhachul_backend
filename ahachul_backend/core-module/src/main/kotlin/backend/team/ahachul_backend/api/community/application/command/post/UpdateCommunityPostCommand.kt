package backend.team.ahachul_backend.api.community.adapter.web.`in`.dto.post

import backend.team.ahachul_backend.api.community.domain.model.CommunityCategoryType
import org.springframework.web.multipart.MultipartFile

class UpdateCommunityPostCommand(
    val id: Long,
    val title: String,
    val content: String,
    val categoryType: CommunityCategoryType,
    val hashTags: List<String> = arrayListOf(),
    val uploadFiles: List<MultipartFile> = arrayListOf(),
    val removeFileIds: List<Long> = arrayListOf()
) {
}