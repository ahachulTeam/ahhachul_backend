package backend.team.ahachul_backend.api.community.adapter.web.`in`.dto

import backend.team.ahachul_backend.api.community.application.command.`in`.UpdateCommunityPostCommand
import backend.team.ahachul_backend.api.community.domain.entity.CommunityPostEntity
import backend.team.ahachul_backend.api.community.domain.model.CommunityCategoryType
import backend.team.ahachul_backend.common.dto.ImageDto
import org.springframework.web.multipart.MultipartFile

class UpdateCommunityPostDto {

    data class Request(
        val title: String,
        val content: String,
        val categoryType: CommunityCategoryType,
        val subwayLineId: Long? = null,
        val stationId: Long? = null,
        val hashTags: List<String> = listOf(),
        val removeFileIds: List<Long> = listOf()
    ) {
        fun toCommand(postId: Long, uploadFiles: List<MultipartFile>?): UpdateCommunityPostCommand {
            return UpdateCommunityPostCommand(
                id = postId,
                title = title,
                content = content,
                categoryType = categoryType,
                subwayLineId = subwayLineId,
                stationId = stationId,
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
        val subwayLineId: Long,
        val stationId: Long?,
        val images: List<ImageDto> = arrayListOf()
    ) {
        companion object {
            fun of(entity: CommunityPostEntity, images: List<ImageDto>): Response {
                return Response(
                    id = entity.id,
                    title = entity.title,
                    content = entity.content,
                    categoryType = entity.categoryType,
                    subwayLineId = entity.subwayLineEntity.id,
                    stationId = entity.station?.id,
                    images = images
                )
            }
        }
    }
}
