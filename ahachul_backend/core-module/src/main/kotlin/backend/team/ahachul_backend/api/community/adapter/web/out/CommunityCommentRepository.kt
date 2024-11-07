package backend.team.ahachul_backend.api.community.adapter.web.out

import backend.team.ahachul_backend.api.community.domain.entity.CommunityCommentEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface CommunityCommentRepository: JpaRepository<CommunityCommentEntity, Long> {

    @Query("SELECT cc " +
            "FROM CommunityCommentEntity cc " +
            "JOIN FETCH cc.member m " +
            "WHERE cc.communityPost.id = :postId " +
            "ORDER BY cc.createdAt ASC")
    fun findAllByCommunityPostId(postId: Long): List<CommunityCommentEntity>

    fun countByCommunityPostId(postId: Long): Int
}