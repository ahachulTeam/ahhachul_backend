package backend.team.ahachul_backend.api.dailyvote.adapter.web.out

import backend.team.ahachul_backend.api.dailyvote.domain.entity.DailyVoteCommentLikeEntity
import org.springframework.data.jpa.repository.JpaRepository

interface DailyVoteCommentLikeRepository : JpaRepository<DailyVoteCommentLikeEntity, Long> {

    fun findByCommentIdAndMemberId(commentId: Long, memberId: Long): DailyVoteCommentLikeEntity?

    fun deleteByCommentIdAndMemberId(commentId: Long, memberId: Long)

    fun findAllByCommentIdIn(commentIds: List<Long>): List<DailyVoteCommentLikeEntity>

    fun findAllByCommentIdInAndMemberId(commentIds: List<Long>, memberId: Long): List<DailyVoteCommentLikeEntity>
}
