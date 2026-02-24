package backend.team.ahachul_backend.api.lost.adapter.web.out

import backend.team.ahachul_backend.api.lost.domain.entity.LostPostEntity
import backend.team.ahachul_backend.api.lost.domain.model.LostPostType
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface LostPostRepository: JpaRepository<LostPostEntity, Long> {

    fun findByMemberIdAndTypeOrderByCreatedAtDesc(
        memberId: Long,
        type: LostPostType,
        pageable: Pageable,
    ): List<LostPostEntity>
}
