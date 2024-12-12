package backend.team.ahachul_backend.api.comment.application.port.out

import backend.team.ahachul_backend.api.comment.domain.entity.CommentEntity

interface CommentReader {

    fun getById(id: Long): CommentEntity

    fun findById(id: Long): CommentEntity?

    fun findAllByCommunityPostId(postId: Long): List<CommentEntity>

    fun findAllByLostPostId(postId: Long): List<CommentEntity>

    fun countCommunity(postId: Long): Int

    fun countLost(postId: Long): Int
}