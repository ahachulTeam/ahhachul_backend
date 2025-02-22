package backend.team.ahachul_backend.api.comment.adapter.web.out

import backend.team.ahachul_backend.api.comment.domain.entity.CommentEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CommentRepository: JpaRepository<CommentEntity, Long> {

    fun countByCommunityPostId(postId: Long): Int

    fun countByLostPostId(postId: Long): Int

    fun countByComplaintPostId(postId: Long): Int
}