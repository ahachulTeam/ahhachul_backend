package backend.team.ahachul_backend.common.persistence

import backend.team.ahachul_backend.common.domain.entity.SubwayLineEntity

interface SubwayLineReader {

    fun getById(id: Long): SubwayLineEntity

    fun getByName(name: String): SubwayLineEntity

    fun getSubwayLines(): List<SubwayLineEntity>
}
