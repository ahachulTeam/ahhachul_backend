package backend.team.ahachul_backend.api.community.adapter.web.out

import backend.team.ahachul_backend.api.community.application.port.out.CommunityPostHashTagReader
import backend.team.ahachul_backend.api.community.application.port.out.CommunityPostHashTagWriter
import backend.team.ahachul_backend.api.community.domain.entity.CommunityPostHashTagEntity
import org.springframework.stereotype.Component

@Component
class CommunityPostHashTagPersistence(
    private val repository: CommunityPostHashTagRepository
): CommunityPostHashTagReader, CommunityPostHashTagWriter {

    override fun save(entity: CommunityPostHashTagEntity): CommunityPostHashTagEntity {
        return repository.save(entity)
    }

    override fun findAllByPostId(postId: Long): List<CommunityPostHashTagEntity> {
        return repository.findAllByCommunityPostId(postId)
    }
}