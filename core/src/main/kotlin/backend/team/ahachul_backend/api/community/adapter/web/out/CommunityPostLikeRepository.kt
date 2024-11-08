package backend.team.ahachul_backend.api.community.adapter.web.out

import backend.team.ahachul_backend.api.community.domain.entity.CommunityPostLikeEntity
import backend.team.ahachul_backend.common.domain.model.YNType
import org.springframework.data.jpa.repository.JpaRepository

interface CommunityPostLikeRepository: JpaRepository<CommunityPostLikeEntity, Long> {

    fun findByCommunityPostIdAndMemberId(postId: Long, memberId: Long): CommunityPostLikeEntity?

    fun deleteByCommunityPostIdAndMemberId(postId: Long, memberId: Long)

    fun existsByCommunityPostIdAndMemberId(postId: Long, memberId: Long): Boolean

    fun countByCommunityPostIdAndLikeYn(postId: Long, likeYn: YNType): Int
}
