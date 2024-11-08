package backend.team.ahachul_backend.api.community.adapter.web.out

import backend.team.ahachul_backend.api.community.domain.entity.CommunityPostFileEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CommunityPostFileRepository: JpaRepository<CommunityPostFileEntity, Long> {

    fun findTopByCommentPostIdOrderById(communityPostId: Long): CommunityPostFileEntity?

    fun findAllByCommentPostIdOrderById(communityPostId: Long): List<CommunityPostFileEntity>

    fun deleteByFileId(fileId: Long)
}