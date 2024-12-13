package backend.team.ahachul_backend.api.lost.application.service.command.`in`

import backend.team.ahachul_backend.api.lost.domain.model.LostStatus
import org.springframework.web.multipart.MultipartFile

class UpdateLostPostStatusCommand (
    val id: Long,
    val status: LostStatus
)
