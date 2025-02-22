package backend.team.ahachul_backend.api.complaint.application.port.`in`

import backend.team.ahachul_backend.api.complaint.domain.entity.ComplaintPostEntity
import backend.team.ahachul_backend.common.dto.ImageDto
import org.springframework.web.multipart.MultipartFile

interface ComplaintPostFileUseCase {
    fun deleteComplaintPostFiles(fileIds: List<Long>)
    fun createComplaintPostFiles(post: ComplaintPostEntity, files: List<MultipartFile>): List<ImageDto>
}