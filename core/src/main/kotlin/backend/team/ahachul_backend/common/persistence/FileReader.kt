package backend.team.ahachul_backend.common.persistence

import backend.team.ahachul_backend.common.domain.entity.FileEntity

interface FileReader {

    fun findAllIdIn(fileIds: List<Long>): List<FileEntity>
}