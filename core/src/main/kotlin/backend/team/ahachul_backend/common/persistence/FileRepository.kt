package backend.team.ahachul_backend.common.persistence

import backend.team.ahachul_backend.common.domain.entity.FileEntity
import org.springframework.data.jpa.repository.JpaRepository

interface FileRepository: JpaRepository<FileEntity, Long> {

    fun findAllByIdIn(fileIds: List<Long>): List<FileEntity>
}