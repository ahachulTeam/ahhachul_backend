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
import backend.team.ahachul_backend.api.comment.application.port.out.CommentReader
import backend.team.ahachul_backend.api.comment.application.port.out.CommentWriter
import backend.team.ahachul_backend.api.comment.domain.entity.CommentEntity
import backend.team.ahachul_backend.api.comment.domain.model.PostType
import backend.team.ahachul_backend.api.community.application.port.out.CommunityPostReader
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
    private val communityPostReader: CommunityPostReader,
    private val lostPostReader: LostPostReader,
    private val memberReader: MemberReader,
): CommentUseCase {

    override fun getComments(command: GetCommentsCommand): GetCommentsDto.Response {


        val comments = when (command.postType) {
            PostType.COMMUNITY -> commentReader.findAllByCommunityPostId(command.postId)
            PostType.LOST -> commentReader.findAllByLostPostId(command.postId)
        }.map {
                GetCommentsDto.Comment(
                    it.id,
                    it.upperComment?.id,
                    it.content,
                    it.status,
                    it.createdAt,
                    it.createdBy,
                    it.member.nickname!!
                )
            }

        val parentComments = mutableListOf<GetCommentsDto.Comment>()
        val childCommentMap = HashMap<Long, MutableList<GetCommentsDto.Comment>>()

        comments.forEach { comment ->
            val parentId = comment.upperCommentId ?: run {
                parentComments.add(comment)
                childCommentMap[comment.id] = mutableListOf()
                return@forEach
            }
            childCommentMap[parentId]?.add(comment)
        }

        return GetCommentsDto.Response(
            parentComments.map {
                GetCommentsDto.CommentList(
                    it,
                    childCommentMap[it.id]?.toList() ?: listOf()
                )
            }
        )
    }

    @Transactional
    override fun createComment(command: CreateCommentCommand): CreateCommentDto.Response {
        val memberId = RequestUtils.getAttribute("memberId")!!
        val upperComment = command.upperCommentId?.let { commentReader.findById(it) }
        val member = memberReader.getMember(memberId.toLong())
        val post = getPost(command.postType, command.postId)

        val entity = commentWriter.save(CommentEntity.of(command, upperComment, post, member))
        return CreateCommentDto.Response.from(entity)
    }

    @Transactional
    override fun updateComment(command: UpdateCommentCommand): UpdateCommentDto.Response {
        val memberId = RequestUtils.getAttribute("memberId")!!
        val comment = commentReader.getById(command.id)
        comment.checkMe(memberId)
        comment.update(command.content)
        return UpdateCommentDto.Response.from(comment)
    }

    @Transactional
    override fun deleteComment(command: DeleteCommentCommand): DeleteCommentDto.Response {
        val memberId = RequestUtils.getAttribute("memberId")!!
        val comment = commentReader.getById(command.id)
        comment.checkMe(memberId)
        comment.delete()
        return DeleteCommentDto.Response(comment.id)
    }

    private fun getPost(postType: PostType, postId: Long): Any {
        return when (postType) {
            PostType.COMMUNITY -> communityPostReader.getCommunityPost(postId)
            PostType.LOST -> lostPostReader.getLostPost(postId)
        }
    }
}
