package backend.team.ahachul_backend.api.comment.application.service

import backend.team.ahachul_backend.api.comment.application.port.`in`.CommentLikeUseCase
import backend.team.ahachul_backend.api.comment.application.port.out.CommentLikeReader
import backend.team.ahachul_backend.api.comment.application.port.out.CommentLikeWriter
import backend.team.ahachul_backend.api.comment.application.port.out.CommentReader
import backend.team.ahachul_backend.api.comment.domain.entity.CommentLikeEntity
import backend.team.ahachul_backend.api.member.application.port.out.MemberReader
import backend.team.ahachul_backend.common.domain.model.YNType
import backend.team.ahachul_backend.common.exception.CommonException
import backend.team.ahachul_backend.common.response.ResponseCode
import backend.team.ahachul_backend.common.utils.RequestUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CommentLikeService(
    private val commentLikeWriter: CommentLikeWriter,
    private val commentLikeReader: CommentLikeReader,
    private val commentReader: CommentReader,
    private val memberReader: MemberReader
) : CommentLikeUseCase {

    @Transactional
    override fun like(commentId: Long) {
        val memberId = RequestUtils.getAttribute("memberId")!!.toLong()

        commentLikeReader.find(commentId, memberId)?.let {
            throw CommonException(ResponseCode.BAD_REQUEST)
        }

        commentLikeWriter.save(
            CommentLikeEntity.of(
                comment = commentReader.getById(commentId),
                member = memberReader.getMember(memberId),
                YNType.Y
            )
        )
    }

    @Transactional
    override fun notLike(commentId: Long) {
        val memberId = RequestUtils.getAttribute("memberId")!!.toLong()

        commentLikeReader.find(commentId, memberId) ?: throw CommonException(ResponseCode.BAD_REQUEST)

        commentLikeWriter.delete(commentId, memberId)
    }
}
