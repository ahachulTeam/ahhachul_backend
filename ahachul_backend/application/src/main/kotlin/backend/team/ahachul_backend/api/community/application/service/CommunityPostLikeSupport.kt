package backend.team.ahachul_backend.api.community.application.service

import backend.team.ahachul_backend.api.community.application.port.out.CommunityPostLikeReader
import backend.team.ahachul_backend.api.community.domain.entity.CommunityPostEntity
import backend.team.ahachul_backend.common.domain.model.YNType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CommunityPostLikeSupport(
    private val communityPostLikeReader: CommunityPostLikeReader,
) {

    companion object {
        const val HOT_SELECTED_LIMIT = 20
    }

    fun isPossibleHotPost(communityPost: CommunityPostEntity): Boolean {
        return communityPostLikeReader.count(communityPost.id, YNType.Y) >= HOT_SELECTED_LIMIT && communityPost.hotPostYn.isN()
    }
}
