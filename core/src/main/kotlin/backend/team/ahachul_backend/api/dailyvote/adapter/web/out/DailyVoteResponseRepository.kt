package backend.team.ahachul_backend.api.dailyvote.adapter.web.out

import backend.team.ahachul_backend.api.dailyvote.domain.entity.DailyVoteResponseEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface DailyVoteResponseRepository : JpaRepository<DailyVoteResponseEntity, Long> {

    fun findByPollIdAndMemberId(pollId: Long, memberId: Long): DailyVoteResponseEntity?

    fun findByPollId(pollId: Long): List<DailyVoteResponseEntity>

    fun findByPollIdIn(pollIds: List<Long>): List<DailyVoteResponseEntity>

    @Query(
        """
        select r.poll.id as pollId, count(r.id) as voteCount
        from DailyVoteResponseEntity r
        where r.poll.id in :pollIds
        group by r.poll.id
        """,
    )
    fun countByPollIds(@Param("pollIds") pollIds: List<Long>): List<DailyVotePollVoteCountProjection>
}

interface DailyVotePollVoteCountProjection {
    val pollId: Long
    val voteCount: Long
}
