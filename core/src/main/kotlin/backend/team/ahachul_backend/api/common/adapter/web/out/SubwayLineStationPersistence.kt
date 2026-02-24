package backend.team.ahachul_backend.api.common.adapter.web.out

import backend.team.ahachul_backend.api.common.application.port.out.SubwayLineStationReader
import backend.team.ahachul_backend.api.common.domain.entity.StationEntity
import backend.team.ahachul_backend.api.common.domain.entity.SubwayLineStationEntity
import backend.team.ahachul_backend.common.exception.AdapterException
import backend.team.ahachul_backend.common.response.ResponseCode
import org.springframework.stereotype.Component

@Component
class SubwayLineStationPersistence(
    private val subwayLineStationRepository: SubwayLineStationRepository
): SubwayLineStationReader {

    override fun findByStation(station: StationEntity): List<SubwayLineStationEntity> {
        return subwayLineStationRepository.findByStation(station)
    }

    override fun findAll(): List<SubwayLineStationEntity> {
        return subwayLineStationRepository.findAll()
    }

    override fun findAllOrderedForGraph(): List<SubwayLineStationEntity> {
        return subwayLineStationRepository.findAllByOrderBySubwayLineIdAscIdAsc()
    }

    override fun findBySubwayLineIdAndStationId(subwayLineId: Long, stationId: Long): SubwayLineStationEntity {
        return subwayLineStationRepository.findBySubwayLineIdAndStationId(subwayLineId, stationId)
            ?: throw AdapterException(ResponseCode.INVALID_DOMAIN)
    }

}
