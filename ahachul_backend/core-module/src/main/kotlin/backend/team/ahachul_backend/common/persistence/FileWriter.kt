package backend.team.ahachul_backend.common.persistence

import backend.team.ahachul_backend.common.domain.entity.FileEntity

interface FileWriter {

    fun save(entity: FileEntity): FileEntity

    fun delete(id: Long)
}
