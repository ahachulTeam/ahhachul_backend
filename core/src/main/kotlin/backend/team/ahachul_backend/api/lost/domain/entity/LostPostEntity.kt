package backend.team.ahachul_backend.api.lost.domain.entity

import backend.team.ahachul_backend.api.lost.application.service.command.`in`.CreateLostPostCommand
import backend.team.ahachul_backend.api.lost.application.service.command.`in`.UpdateLostPostCommand
import backend.team.ahachul_backend.api.lost.domain.model.*
import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity
import backend.team.ahachul_backend.api.report.domain.ReportEntity
import backend.team.ahachul_backend.common.domain.entity.SubwayLineEntity
import backend.team.ahachul_backend.common.domain.entity.BaseEntity
import jakarta.persistence.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Entity
@Table(indexes = [
    Index(name = "received_date_desc_idx", columnList = "receivedDate DESC"),
    Index(name = "created_date_desc_idx", columnList = "createdAt DESC")
])
class LostPostEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lost_post_id")
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    var member: MemberEntity? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subway_line_id")
    var subwayLine: SubwayLineEntity?,

    @OneToMany(mappedBy = "lostPost")
    var lostPostReports: MutableList<ReportEntity> = mutableListOf(),

    @JoinColumn(name = "category_id")
    @OneToOne(fetch = FetchType.LAZY)
    var category: CategoryEntity?,

    var title: String,

    @Lob
    @Column(columnDefinition = "text")
    var content: String,

    @Enumerated(value = EnumType.STRING)
    var status: LostStatus = LostStatus.PROGRESS,

    @Enumerated(value = EnumType.STRING)
    var origin: LostOrigin = LostOrigin.AHACHUL,

    @Enumerated(value = EnumType.STRING)
    var lostType: LostType,

    @Enumerated(value = EnumType.STRING)
    var type: LostPostType = LostPostType.CREATED,

    var storage: String? = null,

    var storageNumber: String? = null,

    var pageUrl: String? = null,

    var receivedDate: LocalDateTime = LocalDateTime.now(),

    var externalSourceFileUrl: String? = null

): BaseEntity() {

    companion object {
        const val MIN_BLOCK_REPORT_COUNT = 5

        fun of(command: CreateLostPostCommand, member: MemberEntity,
               subwayLine: SubwayLineEntity?, category: CategoryEntity?
        ): LostPostEntity {
            return LostPostEntity(
                title = command.title,
                content = command.content,
                subwayLine = subwayLine,
                lostType = command.lostType,
                member = member,
                category = category
            )
        }

        fun ofLost112(data: Lost112Data, subwayLine: SubwayLineEntity?, category: CategoryEntity?, fileUrl: String?): LostPostEntity {
            return LostPostEntity(
                title = data.title,
                content = data.context,
                lostType = LostType.ACQUIRE,
                origin = LostOrigin.LOST112,
                storageNumber = data.phone,
                storage = data.storagePlace,
                subwayLine = subwayLine,
                pageUrl = data.page,
                receivedDate = LocalDateTime.parse(data.getDate,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH시경")),
                category = category,
                externalSourceFileUrl = fileUrl
            )
        }
    }

    fun update(command: UpdateLostPostCommand, subwayLine: SubwayLineEntity?, category: CategoryEntity?) {
        command.title?.let { this.title = it }
        command.content?.let { this.content = it }
        command.status?.let { this.status= it }
        subwayLine?.let { this.subwayLine = subwayLine }
        category?.let { this.category = category }
    }

    fun updateStatus(status: LostStatus) {
        this.status = status
    }

    fun delete() {
        type = LostPostType.DELETED
    }

    fun hasDuplicateReportByMember(member: MemberEntity): Boolean{
        return lostPostReports.stream()
            .anyMatch {x -> x.sourceMember.id == member.id}
    }

    fun exceedMinReportCount(): Boolean {
        return lostPostReports.size >= MIN_BLOCK_REPORT_COUNT
    }

    fun isFromLost112(): Boolean {
        return origin == LostOrigin.LOST112
    }

    fun block() {
        type = LostPostType.BLOCKED
    }

    val date: LocalDateTime
        get() = when (lostType) {
            LostType.ACQUIRE -> receivedDate
            else -> createdAt
        }
}
