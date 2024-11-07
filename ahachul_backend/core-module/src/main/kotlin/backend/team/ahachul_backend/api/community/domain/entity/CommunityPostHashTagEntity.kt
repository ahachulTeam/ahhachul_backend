package backend.team.ahachul_backend.api.community.domain.entity

import backend.team.ahachul_backend.common.domain.entity.BaseEntity
import backend.team.ahachul_backend.common.domain.entity.HashTagEntity
import jakarta.persistence.*

@Entity
class CommunityPostHashTagEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "community_post_hash_tag_id")
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_post_id")
    var communityPost: CommunityPostEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hash_tag_id")
    var hashTag: HashTagEntity,

    ): BaseEntity() {
}
