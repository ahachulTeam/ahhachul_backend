package backend.team.ahachul_backend.common.persistence

import backend.team.ahachul_backend.common.domain.entity.HashTagEntity

interface HashTagReader {

    fun find(name: String): HashTagEntity?
}