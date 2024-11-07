package backend.team.ahachul_backend.api.complaint.application.port.`in`

import backend.team.ahachul_backend.api.complaint.domain.entity.ComplaintMessageHistoryEntity
import org.springframework.web.multipart.MultipartFile

interface ComplaintFileUseCase {

    fun createComplaintMessageFiles(complaint: ComplaintMessageHistoryEntity, files: List<MultipartFile>): List<ImageDto>
}
