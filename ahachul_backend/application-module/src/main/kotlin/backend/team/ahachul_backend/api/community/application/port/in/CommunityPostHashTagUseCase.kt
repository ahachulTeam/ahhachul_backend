package backend.team.ahachul_backend.api.community.application.port.`in`

import backend.team.ahachul_backend.api.community.domain.entity.CommunityPostEntity

interface CommunityPostHashTagUseCase {

    fun createCommunityPostHashTag(communityPost: CommunityPostEntity, hashTags: List<String>)
}