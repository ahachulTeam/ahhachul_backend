package backend.team.ahachul_backend.api.complaint.application.command

import backend.team.ahachul_backend.api.complaint.domain.entity.ComplaintMessageHistoryEntity
import backend.team.ahachul_backend.api.complaint.domain.model.ComplaintType
import backend.team.ahachul_backend.api.complaint.domain.model.ShortContentType
import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity
import backend.team.ahachul_backend.common.domain.entity.SubwayLineEntity
import org.springframework.web.multipart.MultipartFile

class SendComplaintMessageCommand(
    val content: String?,
    val complainType: ComplaintType,
    val shortContentType: ShortContentType? = null,
    val phoneNumber: String?,
    val trainNo: String,
    val location: Int,
    val subwayLineId: Long,
    val imageFiles: List<MultipartFile> = listOf()
) {

    fun toEntity(member: MemberEntity?, subwayLine: SubwayLineEntity): ComplaintMessageHistoryEntity {
        return ComplaintMessageHistoryEntity(
            sentContent = content,
            complaintType = complainType,
            shortContentType = shortContentType,
            sentPhoneNumber = phoneNumber,
            sentTrainNo = trainNo,
            member = member,
            location = location,
            subwayLine = subwayLine,
        )
    }
}
