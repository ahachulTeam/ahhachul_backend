package backend.team.ahachul_backend.api.complaint.adapter.`in`.dto

import backend.team.ahachul_backend.api.complaint.domain.model.ComplaintMessageStatusType
import backend.team.ahachul_backend.api.complaint.domain.model.ComplaintType
import backend.team.ahachul_backend.api.complaint.domain.model.ShortContentType
import java.time.LocalDateTime

class SearchComplaintMessagesDto {

    data class Response(
        val hasNext: Boolean,
        val nextPageNum: Int?,
        val complaintMessages: List<ComplaintMessage>
    ) {
        companion object {
            fun of(hasNext: Boolean, complaintMessages: List<ComplaintMessage>, currentPageNum: Int): Response {
                return Response(
                    hasNext = hasNext,
                    nextPageNum = if (hasNext) currentPageNum + 1 else null,
                    complaintMessages = complaintMessages
                )
            }
        }
    }

    data class ComplaintMessage(
        val content: String?,
        val complainType: ComplaintType,
        val shortContentType: ShortContentType? = null,
        val complaintMessageStatusType: ComplaintMessageStatusType,
        val phoneNumber: String?,
        val trainNo: String,
        val location: Int,
        val subwayLineId: Long,
        val createdAt: LocalDateTime,
        val createdBy: String,
        val writer: String,
        val images: List<ImageDto>,
    )
}
