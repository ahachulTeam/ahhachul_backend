package backend.team.ahachul_backend.api.comment.adapter.web.out

import backend.team.ahachul_backend.api.comment.domain.entity.CommentLikeEntity
import backend.team.ahachul_backend.common.domain.model.YNType
import org.springframework.data.jpa.repository.JpaRepository

interface CommentLikeRepository: JpaRepository<CommentLikeEntity, Long> {

    fun findByCommentIdAndMemberId(commentId: Long, memberId: Long): CommentLikeEntity?

    fun deleteByCommentIdAndMemberId(commentId: Long, memberId: Long)

    fun existsByCommentIdAndMemberId(commentId: Long, memberId: Long): Boolean

    fun countByCommentIdAndLikeYn(commentId: Long, likeYn: YNType): Int
}
