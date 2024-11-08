package backend.team.ahachul_backend.api.community.application.port.out

import backend.team.ahachul_backend.api.community.domain.entity.CommunityPostLikeEntity

interface CommunityPostLikeWriter {

    fun save(entity: CommunityPostLikeEntity): CommunityPostLikeEntity

    fun delete(postId: Long, memberId: Long)
}