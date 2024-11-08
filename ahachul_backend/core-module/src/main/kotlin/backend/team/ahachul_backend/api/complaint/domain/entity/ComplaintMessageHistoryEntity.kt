package backend.team.ahachul_backend.api.complaint.domain.entity

import backend.team.ahachul_backend.api.complaint.domain.model.ComplaintMessageStatusType
import backend.team.ahachul_backend.api.complaint.domain.model.ComplaintType
import backend.team.ahachul_backend.api.complaint.domain.model.ShortContentType
import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity
import backend.team.ahachul_backend.common.domain.entity.SubwayLineEntity
import backend.team.ahachul_backend.common.domain.entity.BaseEntity
import jakarta.persistence.*

@Entity
class ComplaintMessageHistoryEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "complaint_message_history_id")
    var id: Long = 0,

    @Enumerated(EnumType.STRING)
    val complaintType: ComplaintType,

    @Enumerated(EnumType.STRING)
    val shortContentType: ShortContentType?,

    val sentContent: String?,

    val sentPhoneNumber: String?,

    val sentTrainNo: String,

    val location: Int,

    @Enumerated(EnumType.STRING)
    val complaintMessageStatusType: ComplaintMessageStatusType = ComplaintMessageStatusType.CREATED,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sent_member_id")
    val member: MemberEntity? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sent_subway_line_id")
    val subwayLine: SubwayLineEntity,
): BaseEntity() {
}
