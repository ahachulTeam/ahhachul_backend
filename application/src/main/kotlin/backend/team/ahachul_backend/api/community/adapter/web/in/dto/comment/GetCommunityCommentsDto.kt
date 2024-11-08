package backend.team.ahachul_backend.api.community.adapter.web.`in`.dto.comment

import backend.team.ahachul_backend.api.community.domain.model.CommunityCommentType
import java.time.LocalDateTime

class GetCommunityCommentsDto {

    data class Response(
        val comments: List<CommunityCommentList>
    )

    data class CommunityCommentList(
        val parentComment: CommunityComment,
        val childComments: List<CommunityComment>
    )

    data class CommunityComment(
        val id: Long,
        val upperCommentId: Long?,
        val content: String,
        val status: CommunityCommentType,
        val createdAt: LocalDateTime,
        val createdBy: String,
        val writer: String,
    )
}