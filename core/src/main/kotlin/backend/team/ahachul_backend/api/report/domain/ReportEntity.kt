package backend.team.ahachul_backend.api.report.domain

import backend.team.ahachul_backend.api.community.domain.entity.CommunityPostEntity
import backend.team.ahachul_backend.api.lost.domain.entity.LostPostEntity
import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity
import backend.team.ahachul_backend.common.domain.entity.BaseEntity
import jakarta.persistence.*


@Entity
class ReportEntity (

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_member_id")
    val sourceMember: MemberEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_member_id")
    var targetMember: MemberEntity? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_post_id")
    var communityPost: CommunityPostEntity? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lost_post_id")
    var lostPost: LostPostEntity? = null

): BaseEntity() {

    companion object {
        fun from(sourceMember: MemberEntity, targetMember: MemberEntity, target: Any): ReportEntity {
            val entity = ReportEntity(sourceMember = sourceMember)
            entity.addTargetMember(targetMember)
            entity.addCommunityPost(target)
            entity.addLostPost(target)
            return entity
        }
    }

    private fun addTargetMember(targetMember: MemberEntity) {
        this.targetMember = targetMember
        targetMember.memberReports.add(this)
    }

    private fun addCommunityPost(target: Any) {
        this.communityPost = target as? CommunityPostEntity
        communityPost?.communityPostReports?.add(this)
    }

    private fun addLostPost(target: Any) {
        this.lostPost = target as? LostPostEntity
        lostPost?.lostPostReports?.add(this)
    }
}
