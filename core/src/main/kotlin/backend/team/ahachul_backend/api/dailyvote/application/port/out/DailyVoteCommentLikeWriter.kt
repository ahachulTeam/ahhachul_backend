package backend.team.ahachul_backend.api.dailyvote.application.port.out

import backend.team.ahachul_backend.api.dailyvote.domain.entity.DailyVoteCommentLikeEntity

interface DailyVoteCommentLikeWriter {

    fun save(entity: DailyVoteCommentLikeEntity): DailyVoteCommentLikeEntity

    fun delete(commentId: Long, memberId: Long)
}
