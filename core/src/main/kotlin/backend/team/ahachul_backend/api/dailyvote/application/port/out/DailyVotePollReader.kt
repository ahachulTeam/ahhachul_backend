package backend.team.ahachul_backend.api.dailyvote.application.port.out

import backend.team.ahachul_backend.api.dailyvote.domain.entity.DailyVotePollEntity
import backend.team.ahachul_backend.api.dailyvote.domain.model.DailyVoteContextType
import backend.team.ahachul_backend.api.dailyvote.domain.model.DailyVoteKindType
import backend.team.ahachul_backend.api.dailyvote.domain.model.DailyVoteSlotType
import backend.team.ahachul_backend.common.response.ResponseCode
import backend.team.ahachul_backend.common.exception.AdapterException
import java.time.LocalDate

interface DailyVotePollReader {

    fun find(
        pollDate: LocalDate,
        pollSlot: DailyVoteSlotType,
        pollContext: DailyVoteContextType,
        pollKind: DailyVoteKindType,
        stationId: Long,
        subwayLineId: Long,
        isPrimary: Boolean,
    ): DailyVotePollEntity?

    fun findById(id: Long): DailyVotePollEntity?

    fun findStationBoardOpenPolls(stationId: Long, subwayLineId: Long?): List<DailyVotePollEntity>

    fun getById(id: Long): DailyVotePollEntity {
        return findById(id)
            ?: throw AdapterException(ResponseCode.DAILY_VOTE_POLL_NOT_FOUND)
    }
}
