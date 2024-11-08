package backend.team.ahachul_backend.api.community.application.port.out

import backend.team.ahachul_backend.api.community.domain.entity.CommunityCommentEntity

interface CommunityCommentReader {

    fun getById(id: Long): CommunityCommentEntity

    fun findById(id: Long): CommunityCommentEntity?

    fun findAllByPostId(postId: Long): List<CommunityCommentEntity>

    fun count(postId: Long): Int
}