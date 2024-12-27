package backend.team.ahachul_backend.api.comment.domain

import backend.team.ahachul_backend.api.comment.domain.entity.CommentEntity
import backend.team.ahachul_backend.api.comment.domain.model.CommentType
import backend.team.ahachul_backend.api.comment.domain.model.CommentVisibility
import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity
import java.time.LocalDateTime

data class SearchComment(
    val id: Long,
    val upperComment: CommentEntity?,
    val content: String,
    val status: CommentType,
    val createdAt: LocalDateTime,
    val createdBy: String,
    val member: MemberEntity,
    val visibility: CommentVisibility,
    val likeCnt: Long
) {

    fun validateReadPermission(loginMemberId: Long?) : Boolean {
        if (visibility == CommentVisibility.PUBLIC) {
            return true
        }

        return if (upperComment != null) {
            upperComment.id == loginMemberId
        } else {
            member.id == loginMemberId
        }
    }
}
