package backend.team.ahachul_backend.api.community.adapter.web.out

import backend.team.ahachul_backend.api.community.application.port.out.CommunityCommentReader
import backend.team.ahachul_backend.api.community.application.port.out.CommunityCommentWriter
import backend.team.ahachul_backend.api.community.domain.entity.CommunityCommentEntity
import backend.team.ahachul_backend.common.exception.AdapterException
import backend.team.ahachul_backend.common.response.ResponseCode
import org.springframework.stereotype.Component

@Component
class CommunityCommentPersistence(
    private val repository: CommunityCommentRepository
): CommunityCommentWriter, CommunityCommentReader {

    override fun save(entity: CommunityCommentEntity): CommunityCommentEntity {
        return repository.save(entity)
    }

    override fun getById(id: Long): CommunityCommentEntity {
        return repository.findById(id)
            .orElseThrow { throw AdapterException(ResponseCode.INVALID_DOMAIN) }
    }

    override fun findById(id: Long): CommunityCommentEntity? {
        return repository.findById(id).orElse(null)
    }

    override fun findAllByPostId(postId: Long): List<CommunityCommentEntity> {
        return repository.findAllByCommunityPostId(postId)
    }

    override fun count(postId: Long): Int {
        return repository.countByCommunityPostId(postId)
    }
}