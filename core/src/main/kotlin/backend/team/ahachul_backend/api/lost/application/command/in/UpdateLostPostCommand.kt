package backend.team.ahachul_backend.api.lost.application.service.command.`in`

import backend.team.ahachul_backend.api.lost.domain.model.LostStatus
import org.springframework.web.multipart.MultipartFile

class UpdateLostPostCommand (
    val id: Long,
    val title: String?,
    val content: String?,
    val subwayLine: Long?,
    val status: LostStatus?,
    val imageFiles: List<MultipartFile>? = arrayListOf(),
    val removeFileIds: List<Long>? = arrayListOf(),
    val categoryName: String?
)
