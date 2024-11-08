package backend.team.ahachul_backend.api.community.adapter.web.out

import backend.team.ahachul_backend.api.community.application.port.out.CommunityPostLikeReader
import backend.team.ahachul_backend.api.community.application.port.out.CommunityPostLikeWriter
import backend.team.ahachul_backend.api.community.domain.entity.CommunityPostLikeEntity
import backend.team.ahachul_backend.common.domain.model.YNType
import org.springframework.stereotype.Component

@Component
class CommunityPostLikePersistence(
    private val repository: CommunityPostLikeRepository,
): CommunityPostLikeReader, CommunityPostLikeWriter {

    override fun save(entity: CommunityPostLikeEntity): CommunityPostLikeEntity {
        return repository.save(entity)
    }

    override fun delete(postId: Long, memberId: Long) {
        repository.deleteByCommunityPostIdAndMemberId(postId, memberId)
    }

    override fun find(postId: Long, memberId: Long): CommunityPostLikeEntity? {
        return repository.findByCommunityPostIdAndMemberId(postId, memberId)
    }

    override fun exist(postId: Long, memberId: Long): Boolean {
        return repository.existsByCommunityPostIdAndMemberId(postId, memberId)
    }

    override fun count(postId: Long, ynType: YNType): Int {
        return repository.countByCommunityPostIdAndLikeYn(postId, ynType)
    }
}
