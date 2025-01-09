package backend.team.ahachul_backend.api.community.domain.entity

import backend.team.ahachul_backend.api.community.application.command.`in`.CreateCommunityPostCommand
import backend.team.ahachul_backend.api.community.application.command.`in`.UpdateCommunityPostCommand
import backend.team.ahachul_backend.api.community.domain.model.CommunityCategoryType
import backend.team.ahachul_backend.api.community.domain.model.CommunityPostType
import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity
import backend.team.ahachul_backend.api.report.domain.ReportEntity
import backend.team.ahachul_backend.common.domain.entity.SubwayLineEntity
import backend.team.ahachul_backend.common.domain.entity.BaseEntity
import backend.team.ahachul_backend.common.domain.model.RegionType
import backend.team.ahachul_backend.common.domain.model.YNType
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class CommunityPostEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "community_post_id")
    val id: Long = 0,

    var title: String,

    @Lob
    @Column(columnDefinition = "text")
    var content: String,

    @Enumerated(EnumType.STRING)
    var categoryType: CommunityCategoryType,

    var views: Int = 0,

    @Enumerated(EnumType.STRING)
    var status: CommunityPostType = CommunityPostType.CREATED,

    @Enumerated(EnumType.STRING)
    var regionType: RegionType = RegionType.METROPOLITAN,

    @Enumerated(EnumType.STRING)
    var hotPostYn: YNType = YNType.N,

    var hotPostSelectedDate: LocalDateTime? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    var member: MemberEntity? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subway_line_id")
    var subwayLineEntity: SubwayLineEntity,

    @OneToMany(mappedBy = "communityPost", fetch = FetchType.EAGER)
    var communityPostHashTags: MutableList<CommunityPostHashTagEntity> = mutableListOf(),

    @OneToMany(mappedBy = "communityPost")
    var communityPostReports: MutableList<ReportEntity> = mutableListOf(),


    ): BaseEntity() {

    companion object {
        const val MIN_BLOCK_REPORT_COUNT = 5
        fun of(command: CreateCommunityPostCommand, memberEntity: MemberEntity, subwayLineEntity: SubwayLineEntity): CommunityPostEntity {
            return CommunityPostEntity(
                title = command.title,
                content = command.content,
                categoryType = command.categoryType,
                member = memberEntity,
                subwayLineEntity = subwayLineEntity
            )
        }
    }

    fun update(command: UpdateCommunityPostCommand) {
        title = command.title
        content = command.content
        categoryType = command.categoryType
    }

    fun delete() {
        status = CommunityPostType.DELETED
    }

    fun hasDuplicateReportByMember(member: MemberEntity): Boolean{
        return communityPostReports.stream()
            .anyMatch {x -> x.sourceMember.id == member.id}
    }

    fun exceedMinReportCount(): Boolean {
        return communityPostReports.size >= MIN_BLOCK_REPORT_COUNT
    }

    fun block() {
        status = CommunityPostType.BLOCKED
    }
}
