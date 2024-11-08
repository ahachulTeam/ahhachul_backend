package backend.team.ahachul_backend.api.community.domain.entity

import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity
import backend.team.ahachul_backend.common.domain.entity.BaseEntity
import backend.team.ahachul_backend.common.domain.model.YNType
import jakarta.persistence.*

@Entity
class CommunityPostLikeEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "community_post_like_id")
    val id: Long = 0,

    @Enumerated(EnumType.STRING)
    var likeYn: YNType,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_post_id")
    var communityPost: CommunityPostEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    var member: MemberEntity,

    ): BaseEntity() {

        companion object {

            fun of(communityPost: CommunityPostEntity, member: MemberEntity, isLike: YNType): CommunityPostLikeEntity {
                return CommunityPostLikeEntity(
                    communityPost = communityPost,
                    likeYn = isLike,
                    member = member,
                )
            }
        }

    fun like() {
        likeYn = YNType.Y
    }

    fun hate() {
        likeYn = YNType.N
    }
}
