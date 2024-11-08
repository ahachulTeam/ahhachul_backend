package backend.team.ahachul_backend.common.persistence

import backend.team.ahachul_backend.common.domain.entity.SubwayLineEntity
import org.springframework.data.jpa.repository.JpaRepository

interface SubwayLineRepository: JpaRepository<SubwayLineEntity, Long> {
    fun findByName(name: String): SubwayLineEntity?
}