package backend.team.ahachul_backend.api.comment.adapter.web.out

import backend.team.ahachul_backend.api.comment.application.port.out.CommentReader
import backend.team.ahachul_backend.api.comment.application.port.out.CommentWriter
import backend.team.ahachul_backend.api.comment.domain.entity.CommentEntity
import backend.team.ahachul_backend.common.exception.AdapterException
import backend.team.ahachul_backend.common.response.ResponseCode
import org.springframework.stereotype.Component

@Component
class CommentPersistence(
    private val repository: CommentRepository
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

    override fun findAllByPostId(postId: Long): List<CommentEntity> {
        return repository.findAllByCommunityPostId(postId)
    }

    override fun count(postId: Long): Int {
        return repository.countByCommunityPostId(postId)
    }
}