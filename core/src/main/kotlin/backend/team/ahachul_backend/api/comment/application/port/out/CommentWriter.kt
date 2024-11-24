package backend.team.ahachul_backend.api.comment.application.port.out

import backend.team.ahachul_backend.api.comment.domain.entity.CommentEntity

interface CommentWriter {

    fun save(entity: CommentEntity): CommentEntity
}