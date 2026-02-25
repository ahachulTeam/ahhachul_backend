package backend.team.ahachul_backend.api.dailyvote.adapter.web.out

import backend.team.ahachul_backend.api.dailyvote.domain.entity.DailyVoteResponseEntity
import org.springframework.data.jpa.repository.JpaRepository

interface DailyVoteResponseRepository : JpaRepository<DailyVoteResponseEntity, Long> {

    fun findByPollIdAndMemberId(pollId: Long, memberId: Long): DailyVoteResponseEntity?

    fun findByPollId(pollId: Long): List<DailyVoteResponseEntity>
}
