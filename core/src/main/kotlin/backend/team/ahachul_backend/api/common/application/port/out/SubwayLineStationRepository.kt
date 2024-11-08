package backend.team.ahachul_backend.api.common.application.port.out

import backend.team.ahachul_backend.api.common.domain.entity.StationEntity
import backend.team.ahachul_backend.api.common.domain.entity.SubwayLineStationEntity
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface SubwayLineStationRepository: JpaRepository<SubwayLineStationEntity, Long> {

    @EntityGraph(attributePaths = ["station", "subwayLine"])
    override fun findAll(): List<SubwayLineStationEntity>

    @EntityGraph(attributePaths = ["subwayLine"])
    fun findByStation(station: StationEntity): List<SubwayLineStationEntity>
}
