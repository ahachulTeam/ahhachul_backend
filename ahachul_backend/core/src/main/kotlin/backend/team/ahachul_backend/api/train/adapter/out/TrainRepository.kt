package backend.team.ahachul_backend.api.train.adapter.out

import backend.team.ahachul_backend.api.train.domain.entity.TrainEntity
import org.springframework.data.jpa.repository.JpaRepository

interface TrainRepository: JpaRepository<TrainEntity, Long> {

    fun findByPrefixTrainNo(prefixTrainNo: String): TrainEntity?
}