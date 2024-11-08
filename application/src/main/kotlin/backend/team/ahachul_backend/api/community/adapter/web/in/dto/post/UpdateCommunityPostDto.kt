package backend.team.ahachul_backend.api.community.adapter.web.`in`.dto.post

import backend.team.ahachul_backend.api.community.domain.entity.CommunityPostEntity
import backend.team.ahachul_backend.api.community.domain.model.CommunityCategoryType
import backend.team.ahachul_backend.common.dto.ImageDto
import org.springframework.web.multipart.MultipartFile

class UpdateCommunityPostDto {

    data class Request(
        val title: String,
        val content: String,
        val categoryType: CommunityCategoryType,
        val hashTags: List<String>,
        val uploadFiles: List<MultipartFile> = listOf(),
        val removeFileIds: List<Long> = listOf()
    ) {
        fun toCommand(postId: Long): UpdateCommunityPostCommand {
            return UpdateCommunityPostCommand(
                id = postId,
                title = title,
                content = content,
                categoryType = categoryType,
                hashTags = hashTags,
                uploadFiles = uploadFiles,
                removeFileIds = removeFileIds
            )
        }
    }

    data class Response(
        val id: Long,
        val title: String,
        val content: String,
        val categoryType: CommunityCategoryType,
        val images: List<ImageDto> = arrayListOf()
    ) {
        companion object {
            fun of(entity: CommunityPostEntity, images: List<ImageDto>): Response {
                return Response(
                    id = entity.id,
                    title = entity.title,
                    content = entity.content,
                    categoryType = entity.categoryType,
                    images = images
                )
            }
        }
    }
}
