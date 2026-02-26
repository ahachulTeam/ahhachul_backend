package backend.team.ahachul_backend.api.dailyvote.adapter.web.out

import backend.team.ahachul_backend.api.dailyvote.domain.entity.DailyVotePollEntity
import backend.team.ahachul_backend.api.dailyvote.domain.model.DailyVoteContextType
import backend.team.ahachul_backend.api.dailyvote.domain.model.DailyVoteKindType
import backend.team.ahachul_backend.api.dailyvote.domain.model.DailyVotePollStatusType
import backend.team.ahachul_backend.api.dailyvote.domain.model.DailyVoteSlotType
import backend.team.ahachul_backend.common.domain.model.YNType
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface DailyVotePollRepository : JpaRepository<DailyVotePollEntity, Long> {

    @EntityGraph(attributePaths = ["station", "subwayLine"])
    fun findByPollDateAndPollSlotAndPollContextAndPollKindAndStationIdAndSubwayLineIdAndPrimaryYn(
        pollDate: LocalDate,
        pollSlot: DailyVoteSlotType,
        pollContext: DailyVoteContextType,
        pollKind: DailyVoteKindType,
        stationId: Long,
        subwayLineId: Long,
        primaryYn: YNType,
    ): DailyVotePollEntity?

    @EntityGraph(attributePaths = ["station", "subwayLine", "member"])
    fun findByPollKindAndStationIdAndStatusOrderByCreatedAtDesc(
        pollKind: DailyVoteKindType,
        stationId: Long,
        status: DailyVotePollStatusType,
    ): List<DailyVotePollEntity>

    @EntityGraph(attributePaths = ["station", "subwayLine", "member"])
    fun findByPollKindAndStationIdAndSubwayLineIdAndStatusOrderByCreatedAtDesc(
        pollKind: DailyVoteKindType,
        stationId: Long,
        subwayLineId: Long,
        status: DailyVotePollStatusType,
    ): List<DailyVotePollEntity>

    @EntityGraph(attributePaths = ["station", "subwayLine", "member"])
    override fun findById(id: Long): java.util.Optional<DailyVotePollEntity>
}
