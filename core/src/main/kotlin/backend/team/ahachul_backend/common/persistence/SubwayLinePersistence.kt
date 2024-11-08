package backend.team.ahachul_backend.common.persistence

import backend.team.ahachul_backend.common.domain.entity.SubwayLineEntity
import backend.team.ahachul_backend.common.exception.AdapterException
import backend.team.ahachul_backend.common.response.ResponseCode
import org.springframework.stereotype.Component

@Component
class SubwayLinePersistence(
    private val subwayLineRepository: SubwayLineRepository
): SubwayLineReader, SubwayLineWriter {

    override fun getById(id: Long): SubwayLineEntity {
        return subwayLineRepository.findById(id).orElseThrow {
            throw AdapterException(ResponseCode.INVALID_DOMAIN)
        }
    }

    override fun getByName(name: String): SubwayLineEntity {
        return subwayLineRepository.findByName(name) ?: throw AdapterException(ResponseCode.INVALID_DOMAIN)
    }

    override fun getSubwayLines(): List<SubwayLineEntity> {
        return subwayLineRepository.findAll()
    }

    override fun save(subwayLineEntity: SubwayLineEntity): SubwayLineEntity {
        return subwayLineRepository.save(subwayLineEntity)
    }
}
