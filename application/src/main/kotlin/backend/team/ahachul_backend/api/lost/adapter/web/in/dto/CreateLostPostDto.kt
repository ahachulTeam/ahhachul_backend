package backend.team.ahachul_backend.api.lost.adapter.web.`in`.dto

import backend.team.ahachul_backend.api.lost.application.service.command.`in`.CreateLostPostCommand
import backend.team.ahachul_backend.api.lost.domain.model.LostType
import backend.team.ahachul_backend.common.dto.ImageDto
import org.jetbrains.annotations.NotNull
import org.springframework.web.multipart.MultipartFile

class
CreateLostPostDto{

    data class Request(
        @NotNull val title: String,
        @NotNull val content: String,
        @NotNull val subwayLine: Long,
        @NotNull val lostType: LostType,
        @NotNull val categoryName: String
    ) {
        fun toCommand(imageFiles: List<MultipartFile>?): CreateLostPostCommand {
            return CreateLostPostCommand(
                title = title,
                content = content,
                subwayLine = subwayLine,
                lostType = lostType,
                imageFiles = imageFiles,
                categoryName = categoryName
            )
        }
    }

    data class Response(
        val id: Long,
        val images: List<ImageDto>?
    ) {
        companion object {
            fun from(id: Long, images: List<ImageDto>?): Response {
                return Response(
                    id = id,
                    images = images)
            }
        }
    }
}
