package backend.team.ahachul_backend.api.common.application.port.out

import backend.team.ahachul_backend.api.common.domain.entity.StationEntity
import backend.team.ahachul_backend.common.domain.entity.SubwayLineEntity

interface StationReader {

    fun getById(id: Long): StationEntity

    fun getByName(name: String): StationEntity
}
