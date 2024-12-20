package backend.team.ahachul_backend.api.comment.application.port.out

import backend.team.ahachul_backend.api.comment.domain.entity.CommentLikeEntity

interface CommentLikeWriter {

    fun save(entity: CommentLikeEntity): CommentLikeEntity

    fun delete(commentId: Long, memberId: Long)
}