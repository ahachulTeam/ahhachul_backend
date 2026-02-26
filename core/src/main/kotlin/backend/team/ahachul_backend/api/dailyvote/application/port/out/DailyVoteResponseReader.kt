package backend.team.ahachul_backend.api.dailyvote.application.port.out

import backend.team.ahachul_backend.api.dailyvote.domain.entity.DailyVoteResponseEntity

interface DailyVoteResponseReader {

    fun findByPollAndMember(pollId: Long, memberId: Long): DailyVoteResponseEntity?

    fun findByPollId(pollId: Long): List<DailyVoteResponseEntity>

    fun findByPollIds(pollIds: List<Long>): List<DailyVoteResponseEntity>

    fun countByPollIds(pollIds: List<Long>): Map<Long, Long>
}
