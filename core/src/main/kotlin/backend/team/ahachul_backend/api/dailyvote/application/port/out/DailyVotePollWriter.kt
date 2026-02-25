package backend.team.ahachul_backend.api.dailyvote.application.port.out

import backend.team.ahachul_backend.api.dailyvote.domain.entity.DailyVotePollEntity

interface DailyVotePollWriter {

    fun save(entity: DailyVotePollEntity): DailyVotePollEntity
}
