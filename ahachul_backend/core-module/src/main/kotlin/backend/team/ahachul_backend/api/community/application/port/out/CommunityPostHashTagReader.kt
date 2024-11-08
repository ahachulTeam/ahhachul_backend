package backend.team.ahachul_backend.api.community.application.port.out

import backend.team.ahachul_backend.api.community.domain.entity.CommunityPostHashTagEntity


interface CommunityPostHashTagReader {

    fun findAllByPostId(postId: Long): List<CommunityPostHashTagEntity>
}