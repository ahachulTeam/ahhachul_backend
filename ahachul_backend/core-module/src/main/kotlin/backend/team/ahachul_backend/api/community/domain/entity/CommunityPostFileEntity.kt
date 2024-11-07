package backend.team.ahachul_backend.api.community.domain.entity

import backend.team.ahachul_backend.common.domain.entity.FileEntity
import backend.team.ahachul_backend.common.domain.entity.BaseEntity
import jakarta.persistence.*

@Entity
class CommunityPostFileEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "community_post_file_id")
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_post_id")
    var commentPost: CommunityPostEntity,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "file_id")
    var file: FileEntity
): BaseEntity() {

    companion object {
        fun of(post: CommunityPostEntity, file: FileEntity): CommunityPostFileEntity {
            return CommunityPostFileEntity(
                commentPost = post,
                file = file
            )
        }
    }
}
