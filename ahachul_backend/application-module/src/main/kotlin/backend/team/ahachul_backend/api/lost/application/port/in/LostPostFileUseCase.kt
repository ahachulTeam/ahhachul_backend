package backend.team.ahachul_backend.api.lost.application.port.`in`

import backend.team.ahachul_backend.api.lost.domain.entity.LostPostEntity
import org.springframework.web.multipart.MultipartFile

interface LostPostFileUseCase {

    fun createLostPostFiles(post: LostPostEntity, files: List<MultipartFile>): List<ImageDto>

    fun deleteLostPostFiles(fileIds: List<Long>)
}
