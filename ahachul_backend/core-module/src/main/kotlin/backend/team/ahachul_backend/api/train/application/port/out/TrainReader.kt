package backend.team.ahachul_backend.api.train.application.port.out

import backend.team.ahachul_backend.api.train.domain.entity.TrainEntity

interface TrainReader {

    fun getTrain(prefixTrainNo: String): TrainEntity
}
