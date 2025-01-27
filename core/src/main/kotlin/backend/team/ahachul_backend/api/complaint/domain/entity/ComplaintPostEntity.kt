package backend.team.ahachul_backend.api.complaint.domain.entity

import backend.team.ahachul_backend.api.complaint.application.command.`in`.CreateComplaintPostCommand
import backend.team.ahachul_backend.api.complaint.application.command.`in`.UpdateComplaintPostCommand
import backend.team.ahachul_backend.api.complaint.domain.model.ComplaintPostType
import backend.team.ahachul_backend.api.complaint.domain.model.ComplaintType
import backend.team.ahachul_backend.api.complaint.domain.model.ShortContentType
import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity
import backend.team.ahachul_backend.common.domain.entity.BaseEntity
import backend.team.ahachul_backend.common.domain.entity.SubwayLineEntity
import jakarta.persistence.*

@Entity
class ComplaintPostEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "complaint_post_id")
    var id: Long = 0,

    @Enumerated(EnumType.STRING)
    var complaintType: ComplaintType,

    @Enumerated(EnumType.STRING)
    var shortContentType: ShortContentType,

    @Column(columnDefinition = "text")
    var content: String,

    var phoneNumber: String?,

    var trainNo: String?,

    var location: Int?,

    @Enumerated(EnumType.STRING)
    var status: ComplaintPostType = ComplaintPostType.CREATED,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    var member: MemberEntity? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subway_line_id")
    var subwayLine: SubwayLineEntity,
): BaseEntity() {

    companion object {
        fun of(
            command: CreateComplaintPostCommand,
            member: MemberEntity,
            subwayLine: SubwayLineEntity
        ): ComplaintPostEntity {
            return ComplaintPostEntity(
                complaintType = command.complaintType,
                shortContentType = command.shortContentType,
                content = command.content,
                phoneNumber = command.phoneNumber,
                trainNo = command.trainNo,
                location = command.location,
                member = member,
                subwayLine = subwayLine,
            )
        }
    }

    fun update(command: UpdateComplaintPostCommand, subwayLine: SubwayLineEntity?) {
        command.complaintType?.let { this.complaintType = it }
        command.shortContentType?.let { this.shortContentType = it }
        command.content?.let { this.content = it }
        command.phoneNumber?.let { this.phoneNumber = it }
        command.trainNo?.let { this.trainNo = it }
        command.location?.let { this.location = it }
        command.status?.let { this.status= it }
        subwayLine?.let { this.subwayLine = subwayLine }
    }

    fun updateStatus(status: ComplaintPostType) {
        this.status = status
    }

    fun delete() {
        this.status = ComplaintPostType.DELETED
    }
}
