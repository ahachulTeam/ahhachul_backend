package backend.team.ahachul_backend.api.comment.application.service

import backend.team.ahachul_backend.api.comment.adapter.web.`in`.dto.CreateCommentDto
import backend.team.ahachul_backend.api.comment.adapter.web.`in`.dto.DeleteCommentDto
import backend.team.ahachul_backend.api.comment.adapter.web.`in`.dto.GetCommentsDto
import backend.team.ahachul_backend.api.comment.adapter.web.`in`.dto.UpdateCommentDto
import backend.team.ahachul_backend.api.comment.application.command.CreateCommentCommand
import backend.team.ahachul_backend.api.comment.application.command.DeleteCommentCommand
import backend.team.ahachul_backend.api.comment.application.command.GetCommentsCommand
import backend.team.ahachul_backend.api.comment.application.command.UpdateCommentCommand
import backend.team.ahachul_backend.api.comment.application.port.`in`.CommentUseCase
import backend.team.ahachul_backend.api.comment.application.port.out.CommentLikeReader
import backend.team.ahachul_backend.api.comment.application.port.out.CommentReader
import backend.team.ahachul_backend.api.comment.application.port.out.CommentWriter
import backend.team.ahachul_backend.api.comment.domain.entity.CommentEntity
import backend.team.ahachul_backend.api.comment.domain.model.PostType
import backend.team.ahachul_backend.api.community.application.port.out.CommunityPostReader
import backend.team.ahachul_backend.api.complaint.application.port.out.ComplaintPostReader
import backend.team.ahachul_backend.api.lost.application.port.out.LostPostReader
import backend.team.ahachul_backend.api.member.application.port.out.MemberReader
import backend.team.ahachul_backend.common.utils.RequestUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CommentService(
    private val commentWriter: CommentWriter,
    private val commentReader: CommentReader,
    private val commentLikeReader: CommentLikeReader,
    private val communityPostReader: CommunityPostReader,
    private val lostPostReader: LostPostReader,
    private val complaintPostReader: ComplaintPostReader,
    private val memberReader: MemberReader,
): CommentUseCase {

    override fun getComments(command: GetCommentsCommand): GetCommentsDto.Response {
        val postWriterId = when (command.postType) {
            PostType.COMMUNITY -> communityPostReader.getCommunityPost(command.postId).createdBy
            PostType.LOST -> lostPostReader.getLostPost(command.postId).createdBy
            PostType.COMPLAINT -> complaintPostReader.getComplaintPost(command.postId).createdBy
        }.toLongOrNull()

        val loginMemberId = RequestUtils.getAttribute(RequestUtils.Attribute.MEMBER_ID)?.toLong()
        val isPostWriterEqualToLoginMember = postWriterId != null && loginMemberId == postWriterId

        val searchedComments = commentReader.searchComments(command)
        val likedCommentIds = if (loginMemberId != null) {
            commentLikeReader.findLikedCommentIds(
                commentIds = searchedComments.map { it.id },
                memberId = loginMemberId,
            )
        } else {
            emptySet()
        }

        val comments = searchedComments.map {
                GetCommentsDto.Comment(
                    it.id,
                    it.upperComment?.id,
                    if (it.validateReadPermission(loginMemberId)
                        || isPostWriterEqualToLoginMember) it.content else "",
                    it.status,
                    it.createdAt,
                    it.createdBy,
                    it.member.nickname!!,
                    it.visibility.isPrivate,
                    it.likeCnt,
                    likedByMe = likedCommentIds.contains(it.id),
                )
            }

        val parentComments = mutableListOf<GetCommentsDto.Comment>()
        val childCommentMap = HashMap<Long, MutableList<GetCommentsDto.Comment>>()

        comments.forEach { comment ->
            val parentId = comment.upperCommentId
            if (parentId == null) {
                parentComments.add(comment)
                return@forEach
            }
            childCommentMap.getOrPut(parentId) { mutableListOf() }.add(comment)
        }

        return GetCommentsDto.Response(
            parentComments.map {
                GetCommentsDto.CommentList(
                    it,
                    childCommentMap[it.id]
                        ?.sortedBy { child -> child.createdAt }
                        ?.toList()
                        ?: listOf()
                )
            }
        )
    }

    @Transactional
    override fun createComment(command: CreateCommentCommand): CreateCommentDto.Response {
        val memberId = RequestUtils.getAttribute(RequestUtils.Attribute.MEMBER_ID)!!
        val upperComment = command.upperCommentId?.let { commentReader.findById(it) }
        upperComment?.validateBelongsTo(command.postType, command.postId)
        val member = memberReader.getMember(memberId.toLong())
        val post = getPost(command.postType, command.postId)

        val entity = commentWriter.save(CommentEntity.of(command, upperComment, post, member))
        return CreateCommentDto.Response.from(entity)
    }

    @Transactional
    override fun updateComment(command: UpdateCommentCommand): UpdateCommentDto.Response {
        val memberId = RequestUtils.getAttribute(RequestUtils.Attribute.MEMBER_ID)!!
        val comment = commentReader.getById(command.id)
        val scopedPostId = command.postId
        val scopedPostType = command.postType
        if (scopedPostId != null && scopedPostType != null) {
            comment.validateBelongsTo(scopedPostType, scopedPostId)
        }
        comment.checkMe(memberId)
        comment.update(command.content)
        return UpdateCommentDto.Response.from(comment)
    }

    @Transactional
    override fun deleteComment(command: DeleteCommentCommand): DeleteCommentDto.Response {
        val memberId = RequestUtils.getAttribute(RequestUtils.Attribute.MEMBER_ID)!!
        val comment = commentReader.getById(command.id)
        val scopedPostId = command.postId
        val scopedPostType = command.postType
        if (scopedPostId != null && scopedPostType != null) {
            comment.validateBelongsTo(scopedPostType, scopedPostId)
        }
        comment.checkMe(memberId)
        comment.delete()
        return DeleteCommentDto.Response(comment.id)
    }

    private fun getPost(postType: PostType, postId: Long): Any {
        return when (postType) {
            PostType.COMMUNITY -> communityPostReader.getCommunityPost(postId)
            PostType.LOST -> lostPostReader.getLostPost(postId)
            PostType.COMPLAINT -> complaintPostReader.getComplaintPost(postId)
        }
    }
}
