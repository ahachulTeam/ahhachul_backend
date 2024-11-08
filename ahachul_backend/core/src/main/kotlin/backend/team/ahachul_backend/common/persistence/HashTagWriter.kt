package backend.team.ahachul_backend.common.persistence

import backend.team.ahachul_backend.common.domain.entity.HashTagEntity

interface HashTagWriter {

    fun save(entity: HashTagEntity): HashTagEntity
}