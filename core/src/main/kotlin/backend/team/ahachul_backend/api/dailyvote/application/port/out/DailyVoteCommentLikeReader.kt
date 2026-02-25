package backend.team.ahachul_backend.api.dailyvote.application.port.out

import backend.team.ahachul_backend.api.dailyvote.domain.entity.DailyVoteCommentLikeEntity

interface DailyVoteCommentLikeReader {

    fun find(commentId: Long, memberId: Long): DailyVoteCommentLikeEntity?

    fun countByCommentIds(commentIds: List<Long>): Map<Long, Long>

    fun findLikedCommentIds(commentIds: List<Long>, memberId: Long): Set<Long>
}
