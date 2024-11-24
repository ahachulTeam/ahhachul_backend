package backend.team.ahachul_backend.api.comment.adapter.web.out

import backend.team.ahachul_backend.api.comment.domain.entity.CommentEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface CommentRepository: JpaRepository<CommentEntity, Long> {

    @Query("SELECT cc " +
            "FROM CommentEntity cc " +
            "JOIN FETCH cc.member m " +
            "WHERE cc.communityPost.id = :postId " +
            "ORDER BY cc.createdAt ASC")
    fun findAllByCommunityPostId(postId: Long): List<CommentEntity>

    fun countByCommunityPostId(postId: Long): Int
}