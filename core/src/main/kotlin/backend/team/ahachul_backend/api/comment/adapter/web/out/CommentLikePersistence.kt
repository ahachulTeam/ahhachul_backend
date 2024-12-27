package backend.team.ahachul_backend.api.comment.adapter.web.out

import backend.team.ahachul_backend.api.comment.application.port.out.CommentLikeReader
import backend.team.ahachul_backend.api.comment.application.port.out.CommentLikeWriter
import backend.team.ahachul_backend.api.comment.domain.entity.CommentLikeEntity
import org.springframework.stereotype.Component

@Component
class CommentLikePersistence(
    private val repository: CommentLikeRepository,
): CommentLikeReader, CommentLikeWriter {

    override fun save(entity: CommentLikeEntity): CommentLikeEntity {
        return repository.save(entity)
    }

    override fun delete(commentId: Long, memberId: Long) {
        repository.deleteByCommentIdAndMemberId(commentId, memberId)
    }

    override fun find(commentId: Long, memberId: Long): CommentLikeEntity? {
        return repository.findByCommentIdAndMemberId(commentId, memberId)
    }
}
