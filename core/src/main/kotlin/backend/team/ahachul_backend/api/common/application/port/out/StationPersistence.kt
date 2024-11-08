package backend.team.ahachul_backend.api.common.application.port.out

import backend.team.ahachul_backend.api.common.domain.entity.StationEntity
import backend.team.ahachul_backend.common.domain.entity.SubwayLineEntity
import backend.team.ahachul_backend.common.exception.AdapterException
import backend.team.ahachul_backend.common.exception.DomainException
import backend.team.ahachul_backend.common.response.ResponseCode
import org.springframework.stereotype.Component

@Component
class StationPersistence(
    private val stationRepository: StationRepository
): StationReader {
    override fun getById(id: Long): StationEntity {
        return stationRepository.findById(id).orElseThrow {
            throw AdapterException(ResponseCode.INVALID_DOMAIN)
        }
    }

    override fun getByName(name: String): StationEntity {
        return stationRepository.findByName(name).orElseThrow {
            throw AdapterException(ResponseCode.INVALID_DOMAIN)
        }
    }
}
