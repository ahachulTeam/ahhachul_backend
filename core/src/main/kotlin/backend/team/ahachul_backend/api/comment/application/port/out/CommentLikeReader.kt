package backend.team.ahachul_backend.api.comment.application.port.out

import backend.team.ahachul_backend.api.comment.domain.entity.CommentLikeEntity

interface CommentLikeReader {

    fun find(commentId: Long, memberId: Long): CommentLikeEntity?
}
