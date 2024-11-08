package backend.team.ahachul_backend.api.lost.adapter.web.out

import backend.team.ahachul_backend.api.lost.domain.entity.LostPostFileEntity
import org.springframework.data.jpa.repository.JpaRepository

interface LostPostFileRepository: JpaRepository<LostPostFileEntity, Long> {

    fun findTopByLostPostIdOrderById(lostPostId: Long): LostPostFileEntity?

    fun findAllByLostPostIdOrderById(lostPostId: Long): List<LostPostFileEntity>
}
