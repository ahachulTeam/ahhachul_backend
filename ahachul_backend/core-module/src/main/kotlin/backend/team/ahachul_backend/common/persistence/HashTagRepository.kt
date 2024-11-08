package backend.team.ahachul_backend.common.persistence

import backend.team.ahachul_backend.common.domain.entity.HashTagEntity
import org.springframework.data.jpa.repository.JpaRepository

interface HashTagRepository: JpaRepository<HashTagEntity, Long> {

    fun findByName(name: String): HashTagEntity?
}