package backend.team.ahachul_backend.api.community.adapter.web.`in`.dto

import backend.team.ahachul_backend.api.community.application.command.`in`.CreateCommunityPostCommand
import backend.team.ahachul_backend.api.community.domain.entity.CommunityPostEntity
import backend.team.ahachul_backend.api.community.domain.model.CommunityCategoryType
import backend.team.ahachul_backend.common.domain.model.RegionType
import backend.team.ahachul_backend.common.dto.ImageDto
import org.springframework.web.multipart.MultipartFile

class CreateCommunityPostDto {

    data class Request(
        val title: String,
        val content: String,
        val categoryType: CommunityCategoryType,
        val hashTags: List<String> = listOf(),
        val subwayLineId: Long,
    ) {
        fun toCommand(imageFiles: List<MultipartFile>?): CreateCommunityPostCommand {
            return CreateCommunityPostCommand(
                title = title,
                content = content,
                categoryType = categoryType,
                hashTags = hashTags,
                subwayLineId = subwayLineId,
                imageFiles = imageFiles
            )
        }
    }

    data class Response(
        val id: Long,
        val title: String,
        val content: String,
        val categoryType: CommunityCategoryType,
        val region: RegionType,
        val subwayLineId: Long,
        val images: List<ImageDto>? = arrayListOf()
    ) {
        companion object {
            fun of(entity: CommunityPostEntity, images: List<ImageDto>?): Response {
                return Response(
                    id = entity.id,
                    title = entity.title,
                    content = entity.content,
                    categoryType = entity.categoryType,
                    region = entity.regionType,
                    subwayLineId = entity.subwayLineEntity.id,
                    images = images
                )
            }
        }
    }
}
