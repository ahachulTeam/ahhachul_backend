package backend.team.ahachul_backend.api.dailyvote.domain.entity

import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity
import backend.team.ahachul_backend.common.domain.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "tb_daily_vote_comment_like")
class DailyVoteCommentLikeEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "daily_vote_comment_like_id")
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_vote_comment_id")
    val comment: DailyVoteCommentEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: MemberEntity,
) : BaseEntity() {

    companion object {
        fun of(comment: DailyVoteCommentEntity, member: MemberEntity): DailyVoteCommentLikeEntity {
            return DailyVoteCommentLikeEntity(
                comment = comment,
                member = member,
            )
        }
    }
}
