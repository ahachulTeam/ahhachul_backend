package backend.team.ahachul_backend.api.lost.adapter.web.out

import backend.team.ahachul_backend.api.lost.domain.entity.LostPostEntity
import org.springframework.data.jpa.repository.JpaRepository

interface LostPostRepository: JpaRepository<LostPostEntity, Long> {
}
