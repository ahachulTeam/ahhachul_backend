package backend.team.ahachul_backend.api.comment.domain.entity

import backend.team.ahachul_backend.api.comment.application.command.CreateCommentCommand
import backend.team.ahachul_backend.api.community.domain.entity.CommunityPostEntity
import backend.team.ahachul_backend.api.comment.domain.model.CommentType
import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity
import backend.team.ahachul_backend.common.domain.entity.BaseEntity
import jakarta.persistence.*

@Entity
class CommentEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "community_comment_id")
    val id: Long = 0,

    var content: String,

    @Enumerated(EnumType.STRING)
    var status: CommentType = CommentType.CREATED,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "upper_comment_id")
    var upperComment: CommentEntity?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_post_id")
    var communityPost: CommunityPostEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    var member: MemberEntity,

    ): BaseEntity() {

    companion object {

        fun of(command: CreateCommentCommand, commentEntity: CommentEntity?, communityPostEntity: CommunityPostEntity, memberEntity: MemberEntity): CommentEntity {
            return CommentEntity(
                content = command.content,
                upperComment = commentEntity,
                communityPost = communityPostEntity,
                member = memberEntity
            )
        }
    }

    fun update(content: String) {
        this.content = content
    }

    fun delete() {
        status = CommentType.DELETED
    }
}
