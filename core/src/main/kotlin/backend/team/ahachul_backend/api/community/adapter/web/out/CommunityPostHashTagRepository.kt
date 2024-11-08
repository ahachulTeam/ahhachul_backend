package backend.team.ahachul_backend.api.community.adapter.web.out

import backend.team.ahachul_backend.api.community.domain.entity.CommunityPostHashTagEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query


interface CommunityPostHashTagRepository: JpaRepository<CommunityPostHashTagEntity, Long> {

    @Query("SELECT cpht FROM CommunityPostHashTagEntity cpht JOIN FETCH cpht.hashTag WHERE cpht.communityPost.id = :postId")
    fun findAllByCommunityPostId(postId: Long): List<CommunityPostHashTagEntity>
}