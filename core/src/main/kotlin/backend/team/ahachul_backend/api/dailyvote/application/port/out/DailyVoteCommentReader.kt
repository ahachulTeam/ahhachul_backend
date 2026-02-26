package backend.team.ahachul_backend.api.dailyvote.application.port.out

import backend.team.ahachul_backend.api.dailyvote.domain.entity.DailyVoteCommentEntity
import backend.team.ahachul_backend.api.dailyvote.domain.model.DailyVoteCommentStatusType
import backend.team.ahachul_backend.common.exception.AdapterException
import backend.team.ahachul_backend.common.response.ResponseCode

interface DailyVoteCommentReader {

    fun findCommentById(commentId: Long): DailyVoteCommentEntity?

    fun findByPollIdAndStatusDesc(pollId: Long, status: DailyVoteCommentStatusType): List<DailyVoteCommentEntity>

    fun countByPollIdAndStatus(pollId: Long, status: DailyVoteCommentStatusType): Long

    fun countByPollIdsAndStatus(pollIds: List<Long>, status: DailyVoteCommentStatusType): Map<Long, Long>

    fun getCommentById(commentId: Long): DailyVoteCommentEntity {
        return findCommentById(commentId)
            ?: throw AdapterException(ResponseCode.DAILY_VOTE_COMMENT_NOT_FOUND)
    }
}
