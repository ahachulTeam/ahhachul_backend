package backend.team.ahachul_backend.api.dailyvote.adapter.web.out

import backend.team.ahachul_backend.api.dailyvote.domain.entity.DailyVoteCommentEntity
import backend.team.ahachul_backend.api.dailyvote.domain.model.DailyVoteCommentStatusType
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface DailyVoteCommentRepository : JpaRepository<DailyVoteCommentEntity, Long> {

    @EntityGraph(attributePaths = ["member"])
    fun findByPollIdAndStatusOrderByCreatedAtDesc(
        pollId: Long,
        status: DailyVoteCommentStatusType,
    ): List<DailyVoteCommentEntity>

    fun countByPollIdAndStatus(pollId: Long, status: DailyVoteCommentStatusType): Long

    @Query(
        """
        select c.poll.id as pollId, count(c.id) as commentCount
        from DailyVoteCommentEntity c
        where c.poll.id in :pollIds
          and c.status = :status
        group by c.poll.id
        """,
    )
    fun countByPollIdsAndStatus(
        @Param("pollIds") pollIds: List<Long>,
        @Param("status") status: DailyVoteCommentStatusType,
    ): List<DailyVotePollCommentCountProjection>
}

interface DailyVotePollCommentCountProjection {
    val pollId: Long
    val commentCount: Long
}
