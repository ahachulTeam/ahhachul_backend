package backend.team.ahachul_backend.api.dailyvote.application.port.out

import backend.team.ahachul_backend.api.dailyvote.domain.entity.DailyVoteCommentEntity

interface DailyVoteCommentWriter {

    fun save(entity: DailyVoteCommentEntity): DailyVoteCommentEntity
}
