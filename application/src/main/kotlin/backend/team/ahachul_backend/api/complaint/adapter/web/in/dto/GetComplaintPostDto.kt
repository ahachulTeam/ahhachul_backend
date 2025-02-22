package backend.team.ahachul_backend.api.complaint.adapter.web.`in`.dto

import backend.team.ahachul_backend.api.complaint.domain.entity.ComplaintPostEntity
import backend.team.ahachul_backend.api.complaint.domain.model.ComplaintPostType
import backend.team.ahachul_backend.api.complaint.domain.model.ComplaintType
import backend.team.ahachul_backend.api.complaint.domain.model.ShortContentType
import backend.team.ahachul_backend.common.dto.ImageDto
import java.time.format.DateTimeFormatter

class GetComplaintPostDto {

    data class Response(
        val id: Long,
        val complaintType: ComplaintType,
        val shortContentType: ShortContentType,
        val content: String,
        val phoneNumber: String?,
        val trainNo: String?,
        val location: Int?,
        val status: ComplaintPostType,
        val commentCnt: Int,
        val subwayLineId: Long,
        val createdBy: String,
        val createdAt: String,
        val writer: String?,
        val images: List<ImageDto>?,
    ) {
        companion object {
            fun of(
                complaintPost: ComplaintPostEntity,
                commentCnt: Int,
                images: List<ImageDto>
            ): Response {
                return Response(
                    id = complaintPost.id,
                    complaintType = complaintPost.complaintType,
                    shortContentType = complaintPost.shortContentType,
                    content = complaintPost.content,
                    phoneNumber = complaintPost.phoneNumber,
                    trainNo = complaintPost.trainNo,
                    location = complaintPost.location,
                    status = complaintPost.status,
                    commentCnt = commentCnt,
                    subwayLineId = complaintPost.subwayLine.id,
                    createdBy = complaintPost.createdBy,
                    createdAt = complaintPost.createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")),
                    writer = complaintPost.member?.nickname,
                    images = images,
                )
            }
        }
    }
}