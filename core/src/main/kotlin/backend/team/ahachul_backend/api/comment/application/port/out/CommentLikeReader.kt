package backend.team.ahachul_backend.api.comment.application.port.out

import backend.team.ahachul_backend.api.comment.domain.entity.CommentLikeEntity
import backend.team.ahachul_backend.common.domain.model.YNType

interface CommentLikeReader {

    fun find(commentId: Long, memberId: Long): CommentLikeEntity?

    fun exist(commentId: Long, memberId: Long): Boolean

    fun count(commentId: Long, ynType: YNType): Int
}
