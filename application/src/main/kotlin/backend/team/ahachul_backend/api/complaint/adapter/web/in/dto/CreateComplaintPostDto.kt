package backend.team.ahachul_backend.api.complaint.adapter.web.`in`.dto

import backend.team.ahachul_backend.api.complaint.application.command.`in`.CreateComplaintPostCommand
import backend.team.ahachul_backend.api.complaint.domain.model.ComplaintType
import backend.team.ahachul_backend.api.complaint.domain.model.ShortContentType
import backend.team.ahachul_backend.common.dto.ImageDto
import org.springframework.web.multipart.MultipartFile

class CreateComplaintPostDto {

    data class Request(
        val complaintType: ComplaintType,
        val shortContentType: ShortContentType,
        val content: String,
        val phoneNumber: String?,
        val trainNo: String?,
        val location: Int?,
        val subwayLineId: Long
    ) {
        fun toCommand(imageFiles: List<MultipartFile>?): CreateComplaintPostCommand {
            return CreateComplaintPostCommand(
                complaintType = complaintType,
                shortContentType = shortContentType,
                content = content,
                phoneNumber = phoneNumber,
                trainNo = trainNo,
                location = location,
                subwayLineId = subwayLineId,
                imageFiles = imageFiles,
            )
        }
    }

    data class Response(
        val id: Long,
        val images: List<ImageDto>?
    ) {
        companion object {
            fun of(id: Long, images: List<ImageDto>?): Response {
                return Response(
                    id = id,
                    images = images
                )
            }
        }
    }
}