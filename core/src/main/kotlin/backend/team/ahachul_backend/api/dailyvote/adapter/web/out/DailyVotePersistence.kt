package backend.team.ahachul_backend.api.dailyvote.adapter.web.out

import backend.team.ahachul_backend.api.dailyvote.application.port.out.DailyVoteCommentLikeReader
import backend.team.ahachul_backend.api.dailyvote.application.port.out.DailyVoteCommentLikeWriter
import backend.team.ahachul_backend.api.dailyvote.application.port.out.DailyVoteCommentReader
import backend.team.ahachul_backend.api.dailyvote.application.port.out.DailyVoteCommentWriter
import backend.team.ahachul_backend.api.dailyvote.application.port.out.DailyVotePollReader
import backend.team.ahachul_backend.api.dailyvote.application.port.out.DailyVotePollWriter
import backend.team.ahachul_backend.api.dailyvote.application.port.out.DailyVoteResponseReader
import backend.team.ahachul_backend.api.dailyvote.application.port.out.DailyVoteResponseWriter
import backend.team.ahachul_backend.api.dailyvote.domain.entity.DailyVoteCommentEntity
import backend.team.ahachul_backend.api.dailyvote.domain.entity.DailyVoteCommentLikeEntity
import backend.team.ahachul_backend.api.dailyvote.domain.entity.DailyVotePollEntity
import backend.team.ahachul_backend.api.dailyvote.domain.entity.DailyVoteResponseEntity
import backend.team.ahachul_backend.api.dailyvote.domain.model.DailyVoteCommentStatusType
import backend.team.ahachul_backend.api.dailyvote.domain.model.DailyVoteContextType
import backend.team.ahachul_backend.api.dailyvote.domain.model.DailyVoteKindType
import backend.team.ahachul_backend.api.dailyvote.domain.model.DailyVotePollStatusType
import backend.team.ahachul_backend.api.dailyvote.domain.model.DailyVoteSlotType
import backend.team.ahachul_backend.common.domain.model.YNType
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class DailyVotePersistence(
    private val dailyVotePollRepository: DailyVotePollRepository,
    private val dailyVoteResponseRepository: DailyVoteResponseRepository,
    private val dailyVoteCommentRepository: DailyVoteCommentRepository,
    private val dailyVoteCommentLikeRepository: DailyVoteCommentLikeRepository,
) : DailyVotePollReader, DailyVotePollWriter, DailyVoteResponseReader, DailyVoteResponseWriter,
    DailyVoteCommentReader, DailyVoteCommentWriter, DailyVoteCommentLikeReader, DailyVoteCommentLikeWriter {

    override fun find(
        pollDate: LocalDate,
        pollSlot: DailyVoteSlotType,
        pollContext: DailyVoteContextType,
        pollKind: DailyVoteKindType,
        stationId: Long,
        subwayLineId: Long,
        isPrimary: Boolean,
    ): DailyVotePollEntity? {
        return dailyVotePollRepository.findByPollDateAndPollSlotAndPollContextAndPollKindAndStationIdAndSubwayLineIdAndPrimaryYn(
            pollDate = pollDate,
            pollSlot = pollSlot,
            pollContext = pollContext,
            pollKind = pollKind,
            stationId = stationId,
            subwayLineId = subwayLineId,
            primaryYn = if (isPrimary) YNType.Y else YNType.N,
        )
    }

    override fun findById(id: Long): DailyVotePollEntity? {
        return dailyVotePollRepository.findById(id).orElse(null)
    }

    override fun findStationBoardOpenPolls(stationId: Long, subwayLineId: Long?): List<DailyVotePollEntity> {
        if (subwayLineId == null) {
            return dailyVotePollRepository.findByPollKindAndStationIdAndStatusOrderByCreatedAtDesc(
                pollKind = DailyVoteKindType.STATION_BOARD,
                stationId = stationId,
                status = DailyVotePollStatusType.OPEN,
            )
        }

        return dailyVotePollRepository.findByPollKindAndStationIdAndSubwayLineIdAndStatusOrderByCreatedAtDesc(
            pollKind = DailyVoteKindType.STATION_BOARD,
            stationId = stationId,
            subwayLineId = subwayLineId,
            status = DailyVotePollStatusType.OPEN,
        )
    }

    override fun save(entity: DailyVotePollEntity): DailyVotePollEntity {
        return dailyVotePollRepository.save(entity)
    }

    override fun findByPollAndMember(pollId: Long, memberId: Long): DailyVoteResponseEntity? {
        return dailyVoteResponseRepository.findByPollIdAndMemberId(pollId, memberId)
    }

    override fun findByPollId(pollId: Long): List<DailyVoteResponseEntity> {
        return dailyVoteResponseRepository.findByPollId(pollId)
    }

    override fun findByPollIds(pollIds: List<Long>): List<DailyVoteResponseEntity> {
        if (pollIds.isEmpty()) {
            return emptyList()
        }
        return dailyVoteResponseRepository.findByPollIdIn(pollIds)
    }

    override fun countByPollIds(pollIds: List<Long>): Map<Long, Long> {
        if (pollIds.isEmpty()) {
            return emptyMap()
        }
        return dailyVoteResponseRepository.countByPollIds(pollIds)
            .associate { projection -> projection.pollId to projection.voteCount }
    }

    override fun save(entity: DailyVoteResponseEntity): DailyVoteResponseEntity {
        return dailyVoteResponseRepository.save(entity)
    }

    override fun findCommentById(commentId: Long): DailyVoteCommentEntity? {
        return dailyVoteCommentRepository.findById(commentId).orElse(null)
    }

    override fun findByPollIdAndStatusDesc(
        pollId: Long,
        status: DailyVoteCommentStatusType,
    ): List<DailyVoteCommentEntity> {
        return dailyVoteCommentRepository.findByPollIdAndStatusOrderByCreatedAtDesc(
            pollId = pollId,
            status = status,
        )
    }

    override fun countByPollIdAndStatus(pollId: Long, status: DailyVoteCommentStatusType): Long {
        return dailyVoteCommentRepository.countByPollIdAndStatus(pollId, status)
    }

    override fun countByPollIdsAndStatus(
        pollIds: List<Long>,
        status: DailyVoteCommentStatusType,
    ): Map<Long, Long> {
        if (pollIds.isEmpty()) {
            return emptyMap()
        }
        return dailyVoteCommentRepository.countByPollIdsAndStatus(pollIds, status)
            .associate { projection -> projection.pollId to projection.commentCount }
    }

    override fun save(entity: DailyVoteCommentEntity): DailyVoteCommentEntity {
        return dailyVoteCommentRepository.save(entity)
    }

    override fun find(commentId: Long, memberId: Long): DailyVoteCommentLikeEntity? {
        return dailyVoteCommentLikeRepository.findByCommentIdAndMemberId(commentId, memberId)
    }

    override fun countByCommentIds(commentIds: List<Long>): Map<Long, Long> {
        if (commentIds.isEmpty()) {
            return emptyMap()
        }

        return dailyVoteCommentLikeRepository.findAllByCommentIdIn(commentIds)
            .groupingBy { it.comment.id }
            .eachCount()
            .mapValues { it.value.toLong() }
    }

    override fun findLikedCommentIds(commentIds: List<Long>, memberId: Long): Set<Long> {
        if (commentIds.isEmpty()) {
            return emptySet()
        }

        return dailyVoteCommentLikeRepository.findAllByCommentIdInAndMemberId(commentIds, memberId)
            .map { it.comment.id }
            .toSet()
    }

    override fun save(entity: DailyVoteCommentLikeEntity): DailyVoteCommentLikeEntity {
        return dailyVoteCommentLikeRepository.save(entity)
    }

    override fun delete(commentId: Long, memberId: Long) {
        dailyVoteCommentLikeRepository.deleteByCommentIdAndMemberId(commentId, memberId)
    }
}
