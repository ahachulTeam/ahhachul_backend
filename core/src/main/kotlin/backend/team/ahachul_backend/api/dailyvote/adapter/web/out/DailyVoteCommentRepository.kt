package backend.team.ahachul_backend.api.dailyvote.adapter.web.out

import backend.team.ahachul_backend.api.dailyvote.domain.entity.DailyVoteCommentEntity
import backend.team.ahachul_backend.api.dailyvote.domain.model.DailyVoteCommentStatusType
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface DailyVoteCommentRepository : JpaRepository<DailyVoteCommentEntity, Long> {

    @EntityGraph(attributePaths = ["member"])
    fun findByPollIdAndStatusOrderByCreatedAtDesc(
        pollId: Long,
        status: DailyVoteCommentStatusType,
    ): List<DailyVoteCommentEntity>

    fun countByPollIdAndStatus(pollId: Long, status: DailyVoteCommentStatusType): Long
}
