package backend.team.ahachul_backend.api.complaint.adapter.web.`in`.dto

import backend.team.ahachul_backend.api.complaint.application.command.`in`.SearchComplaintPostCommand
import backend.team.ahachul_backend.api.complaint.domain.model.ComplaintPostType
import backend.team.ahachul_backend.api.complaint.domain.model.ComplaintType
import backend.team.ahachul_backend.api.complaint.domain.model.ShortContentType
import backend.team.ahachul_backend.common.dto.ImageDto

class SearchComplaintPostDto {

    data class Request(
        val subwayLineId: Long?,
        val keyword: String?,
    ) {
        fun toCommand(pageToken: String?, pageSize: Int): SearchComplaintPostCommand {
            return SearchComplaintPostCommand(
                subwayLineId = subwayLineId,
                keyword = keyword,
                pageToken = pageToken,
                pageSize = pageSize
            )
        }
    }

    data class Response(
        val id: Long,
        val complaintType: ComplaintType,
        val shortContentType: ShortContentType?,
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
        val image: ImageDto?,
    )
}