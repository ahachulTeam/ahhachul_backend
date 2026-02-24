package backend.team.ahachul_backend.api.comment.adapter.web.out

import backend.team.ahachul_backend.api.comment.application.command.GetCommentsCommand
import backend.team.ahachul_backend.api.comment.application.port.out.CommentReader
import backend.team.ahachul_backend.api.comment.application.port.out.CommentWriter
import backend.team.ahachul_backend.api.comment.domain.SearchComment
import backend.team.ahachul_backend.api.comment.domain.entity.CommentEntity
import backend.team.ahachul_backend.common.exception.AdapterException
import backend.team.ahachul_backend.common.response.ResponseCode
import backend.team.ahachul_backend.api.comment.domain.model.CommentType
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component

@Component
class CommentPersistence(
    private val repository: CommentRepository,
    private val customRepository: CustomCommentRepository
): CommentWriter, CommentReader {

    override fun save(entity: CommentEntity): CommentEntity {
        return repository.save(entity)
    }

    override fun getById(id: Long): CommentEntity {
        return repository.findById(id)
            .orElseThrow { throw AdapterException(ResponseCode.INVALID_DOMAIN) }
    }

    override fun findById(id: Long): CommentEntity? {
        return repository.findById(id).orElse(null)
    }

    override fun countCommunity(postId: Long): Int {
        return repository.countByCommunityPostId(postId)
    }

    override fun countLost(postId: Long): Int {
        return repository.countByLostPostId(postId)
    }

    override fun countLostByPostIds(postIds: List<Long>): Map<Long, Int> {
        return customRepository.countLostCommentsByPostIds(postIds)
    }

    override fun countComplaint(postId: Long): Int {
        return repository.countByComplaintPostId(postId)
    }

    override fun searchComments(command: GetCommentsCommand): List<SearchComment> {
        return customRepository.searchComments(command)
    }

    override fun getRecentCommentsByMemberId(memberId: Long, limit: Int): List<CommentEntity> {
        return repository.findByMemberIdAndStatusOrderByCreatedAtDesc(
            memberId = memberId,
            status = CommentType.CREATED,
            pageable = PageRequest.of(0, limit.coerceAtLeast(1))
        )
    }
}
}
