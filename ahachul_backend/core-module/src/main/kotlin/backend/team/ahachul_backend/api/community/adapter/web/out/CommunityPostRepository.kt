package backend.team.ahachul_backend.api.community.adapter.web.out

import backend.team.ahachul_backend.api.community.domain.entity.CommunityPostEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CommunityPostRepository: JpaRepository<CommunityPostEntity, Long> {
}