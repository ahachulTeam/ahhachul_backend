package backend.team.ahachul_backend.api.lost.application.service.command.`in`

import backend.team.ahachul_backend.api.lost.domain.model.LostStatus

class UpdateLostPostStatusCommand (
    val id: Long,
    val status: LostStatus
)
