package backend.team.ahachul_backend.api.comment.domain.entity

import backend.team.ahachul_backend.api.comment.application.command.CreateCommentCommand
import backend.team.ahachul_backend.api.community.domain.entity.CommunityPostEntity
import backend.team.ahachul_backend.api.comment.domain.model.CommentType
import backend.team.ahachul_backend.api.comment.domain.model.CommentVisibility
import backend.team.ahachul_backend.api.complaint.domain.entity.ComplaintPostEntity
import backend.team.ahachul_backend.api.lost.domain.entity.LostPostEntity
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

    @Enumerated(EnumType.STRING)
    var visibility: CommentVisibility,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "upper_comment_id")
    var upperComment: CommentEntity?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_post_id")
    var communityPost: CommunityPostEntity?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lost_post_id")
    var lostPost: LostPostEntity?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "complaint_post_id")
    var complaintPost: ComplaintPostEntity?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    var member: MemberEntity,

    ): BaseEntity() {

    companion object {

        fun of(command: CreateCommentCommand, commentEntity: CommentEntity?, post: Any, memberEntity: MemberEntity): CommentEntity {
            return CommentEntity(
                content = command.content,
                visibility = commentEntity?.visibility ?: command.visibility,
                upperComment = commentEntity,
                communityPost = post as? CommunityPostEntity,
                lostPost = post as? LostPostEntity,
                complaintPost = post as? ComplaintPostEntity,
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
