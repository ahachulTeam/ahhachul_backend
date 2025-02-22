package backend.team.ahachul_backend.api.complaint.adapter.web.`in`.dto

import backend.team.ahachul_backend.api.complaint.domain.entity.ComplaintPostEntity

class DeleteComplaintPostDto {

    data class Response(
        val id: Long,
    ) {
        companion object {
            fun of(complaintPost: ComplaintPostEntity): Response {
                return Response(
                    id = complaintPost.id
                )
            }
        }
    }
}