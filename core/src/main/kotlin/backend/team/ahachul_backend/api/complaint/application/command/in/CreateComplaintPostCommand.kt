package backend.team.ahachul_backend.api.complaint.application.command.`in`

import backend.team.ahachul_backend.api.complaint.domain.model.ComplaintType
import backend.team.ahachul_backend.api.complaint.domain.model.ShortContentType
import org.springframework.web.multipart.MultipartFile

class CreateComplaintPostCommand(
    val complaintType: ComplaintType,
    val shortContentType: ShortContentType,
    val content: String,
    val phoneNumber: String?,
    val trainNo: String?,
    val location: Int?,
    val subwayLineId: Long,
    val imageFiles: List<MultipartFile>? = listOf()
) {

}