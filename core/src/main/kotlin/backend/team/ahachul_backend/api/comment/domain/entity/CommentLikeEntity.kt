package backend.team.ahachul_backend.api.comment.domain.entity

import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity
import backend.team.ahachul_backend.common.domain.entity.BaseEntity
import backend.team.ahachul_backend.common.domain.model.YNType
import jakarta.persistence.*

@Entity
class CommentLikeEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_like_id")
    val id: Long = 0,

    @Enumerated(EnumType.STRING)
    var likeYn: YNType,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_comment_id")
    var comment: CommentEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    var member: MemberEntity,

    ): BaseEntity() {

        companion object {

            fun of(comment: CommentEntity, member: MemberEntity, isLike: YNType): CommentLikeEntity {
                return CommentLikeEntity(
                    comment = comment,
                    likeYn = isLike,
                    member = member,
                )
            }
        }

    fun like() {
        likeYn = YNType.Y
    }
}
