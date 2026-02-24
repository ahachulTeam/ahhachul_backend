package backend.team.ahachul_backend.api.community.adapter.web.out

import backend.team.ahachul_backend.api.community.domain.entity.CommunityPostEntity
import backend.team.ahachul_backend.api.community.domain.model.CommunityPostType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.domain.Pageable

interface CommunityPostRepository: JpaRepository<CommunityPostEntity, Long> {

    fun findByMemberIdAndStatusOrderByCreatedAtDesc(
        memberId: Long,
        status: CommunityPostType,
        pageable: Pageable,
    ): List<CommunityPostEntity>
}
