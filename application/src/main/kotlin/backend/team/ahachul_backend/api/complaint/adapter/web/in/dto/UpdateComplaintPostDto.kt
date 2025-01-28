package backend.team.ahachul_backend.api.complaint.adapter.web.`in`.dto

import backend.team.ahachul_backend.api.complaint.application.command.`in`.UpdateComplaintPostCommand
import backend.team.ahachul_backend.api.complaint.domain.entity.ComplaintPostEntity
import backend.team.ahachul_backend.api.complaint.domain.model.ComplaintPostType
import backend.team.ahachul_backend.api.complaint.domain.model.ComplaintType
import backend.team.ahachul_backend.api.complaint.domain.model.ShortContentType
import org.springframework.web.multipart.MultipartFile

class UpdateComplaintPostDto {

    data class Request(
        val complaintType: ComplaintType?,
        val shortContentType: ShortContentType?,
        val content: String?,
        val phoneNumber: String?,
        val trainNo: String?,
        val location: Int?,
        val subwayLineId: Long?,
        val status: ComplaintPostType?,
        val removeFileIds: List<Long>? = arrayListOf(),
    ) {
        fun toCommand(postId: Long, imageFiles: List<MultipartFile>?): UpdateComplaintPostCommand {
            return UpdateComplaintPostCommand(
                id = postId,
                complaintType = complaintType,
                shortContentType = shortContentType,
                phoneNumber = phoneNumber,
                trainNo = trainNo,
                location = location,
                content = content,
                subwayLineId = subwayLineId,
                status = status,
                imageFiles = imageFiles,
                removeFileIds = removeFileIds,
            )
        }
    }

    data class Response(
        val id: Long,
        val complaintType: ComplaintType,
        val shortContentType: ShortContentType,
        val content: String,
        val phoneNumber: String?,
        val trainNo: String?,
        val location: Int?,
        val status: ComplaintPostType,
        val subwayLineId: Long,
    ) {
        companion object {
            fun of(complaintPost: ComplaintPostEntity): Response {
                return Response(
                    id = complaintPost.id,
                    complaintType = complaintPost.complaintType,
                    shortContentType = complaintPost.shortContentType,
                    content = complaintPost.content,
                    phoneNumber = complaintPost.phoneNumber,
                    trainNo = complaintPost.trainNo,
                    location = complaintPost.location,
                    status = complaintPost.status,
                    subwayLineId = complaintPost.subwayLine.id,
                )
            }
        }
    }
}