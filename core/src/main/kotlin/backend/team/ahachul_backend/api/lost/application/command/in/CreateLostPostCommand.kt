package backend.team.ahachul_backend.api.lost.application.service.command.`in`

import backend.team.ahachul_backend.api.lost.domain.model.LostType
import org.springframework.web.multipart.MultipartFile

class CreateLostPostCommand(
    // TODO 교환 희망 장소, 분실 세부 장소
    val title: String,
    val content: String,
    val subwayLine: Long,
    val lostType: LostType,
    val categoryName: String,
    var imageFiles: List<MultipartFile>? = listOf()
)
