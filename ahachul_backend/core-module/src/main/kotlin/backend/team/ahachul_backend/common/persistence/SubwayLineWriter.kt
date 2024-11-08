package backend.team.ahachul_backend.common.persistence

import backend.team.ahachul_backend.common.domain.entity.SubwayLineEntity

interface SubwayLineWriter {

    fun save(subwayLineEntity: SubwayLineEntity): SubwayLineEntity
}