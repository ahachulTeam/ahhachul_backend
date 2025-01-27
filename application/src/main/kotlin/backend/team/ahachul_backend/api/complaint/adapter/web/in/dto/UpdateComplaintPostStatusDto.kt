package backend.team.ahachul_backend.api.complaint.adapter.web.`in`.dto

import backend.team.ahachul_backend.api.complaint.application.command.`in`.UpdateComplaintPostStatusCommand
import backend.team.ahachul_backend.api.complaint.domain.entity.ComplaintPostEntity
import backend.team.ahachul_backend.api.complaint.domain.model.ComplaintPostType
import backend.team.ahachul_backend.api.complaint.domain.model.ComplaintType
import backend.team.ahachul_backend.api.complaint.domain.model.ShortContentType

class UpdateComplaintPostStatusDto {

    data class Request(
        val status: ComplaintPostType
    ) {
        fun toCommand(postId: Long): UpdateComplaintPostStatusCommand {
            return UpdateComplaintPostStatusCommand(
                id = postId,
                status = status
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