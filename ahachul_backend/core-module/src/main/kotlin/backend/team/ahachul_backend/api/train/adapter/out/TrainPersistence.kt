package backend.team.ahachul_backend.api.train.adapter.out

import backend.team.ahachul_backend.api.train.application.port.out.TrainReader
import backend.team.ahachul_backend.api.train.domain.entity.TrainEntity
import backend.team.ahachul_backend.common.exception.AdapterException
import backend.team.ahachul_backend.common.response.ResponseCode
import org.springframework.stereotype.Component

@Component
class TrainPersistence(
    private val trainRepository: TrainRepository,
): TrainReader {

    override fun getTrain(prefixTrainNo: String): TrainEntity {
        return trainRepository.findByPrefixTrainNo(prefixTrainNo) ?: throw AdapterException(ResponseCode.INVALID_DOMAIN)
    }
}