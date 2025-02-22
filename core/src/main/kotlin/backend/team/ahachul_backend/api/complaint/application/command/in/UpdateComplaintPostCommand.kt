package backend.team.ahachul_backend.api.complaint.application.command.`in`

import backend.team.ahachul_backend.api.complaint.domain.model.ComplaintPostType
import backend.team.ahachul_backend.api.complaint.domain.model.ComplaintType
import backend.team.ahachul_backend.api.complaint.domain.model.ShortContentType
import org.springframework.web.multipart.MultipartFile

class UpdateComplaintPostCommand(
    val id: Long,
    val complaintType: ComplaintType?,
    val shortContentType: ShortContentType?,
    val phoneNumber: String?,
    val trainNo: String?,
    val location: Int?,
    val content: String?,
    val subwayLineId: Long?,
    val status: ComplaintPostType?,
    val imageFiles: List<MultipartFile>? = arrayListOf(),
    val removeFileIds: List<Long>? = arrayListOf(),
) {

}