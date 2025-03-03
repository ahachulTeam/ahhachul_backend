package backend.team.ahachul_backend.api.common.adapter.web.out

import backend.team.ahachul_backend.api.common.domain.entity.StationEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface StationRepository: JpaRepository<StationEntity, Long> {

    fun findByName(name: String): Optional<StationEntity>

}
