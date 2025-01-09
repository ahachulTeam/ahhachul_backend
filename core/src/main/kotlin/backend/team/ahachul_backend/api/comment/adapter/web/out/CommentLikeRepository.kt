package backend.team.ahachul_backend.api.comment.adapter.web.out

import backend.team.ahachul_backend.api.comment.domain.entity.CommentLikeEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CommentLikeRepository: JpaRepository<CommentLikeEntity, Long> {

    fun findByCommentIdAndMemberId(commentId: Long, memberId: Long): CommentLikeEntity?

    fun deleteByCommentIdAndMemberId(commentId: Long, memberId: Long)
}
