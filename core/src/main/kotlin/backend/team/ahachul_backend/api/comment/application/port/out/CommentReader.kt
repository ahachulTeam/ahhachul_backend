package backend.team.ahachul_backend.api.comment.application.port.out

import backend.team.ahachul_backend.api.comment.domain.entity.CommentEntity

interface CommentReader {

    fun getById(id: Long): CommentEntity

    fun findById(id: Long): CommentEntity?

    fun findAllByPostId(postId: Long): List<CommentEntity>

    fun count(postId: Long): Int
}