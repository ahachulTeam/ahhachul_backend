package backend.team.ahachul_backend.api.community.domain.entity

import backend.team.ahachul_backend.api.community.adapter.web.`in`.dto.comment.CreateCommunityCommentCommand
import backend.team.ahachul_backend.api.community.domain.model.CommunityCommentType
import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity
import backend.team.ahachul_backend.common.domain.entity.BaseEntity
import jakarta.persistence.*

@Entity
class CommunityCommentEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "community_comment_id")
    val id: Long = 0,

    var content: String,

    @Enumerated(EnumType.STRING)
    var status: CommunityCommentType = CommunityCommentType.CREATED,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "upper_comment_id")
    var upperCommunityComment: CommunityCommentEntity?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_post_id")
    var communityPost: CommunityPostEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    var member: MemberEntity,

): BaseEntity() {

    companion object {

        fun of(command: CreateCommunityCommentCommand, communityCommentEntity: CommunityCommentEntity?,communityPostEntity: CommunityPostEntity, memberEntity: MemberEntity): CommunityCommentEntity {
            return CommunityCommentEntity(
                content = command.content,
                upperCommunityComment = communityCommentEntity,
                communityPost = communityPostEntity,
                member = memberEntity
            )
        }
    }

    fun update(content: String) {
        this.content = content
    }

    fun delete() {
        status = CommunityCommentType.DELETED
    }
}
