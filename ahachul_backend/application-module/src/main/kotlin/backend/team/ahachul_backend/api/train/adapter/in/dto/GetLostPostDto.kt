package backend.team.ahachul_backend.api.train.adapter.`in`.dto

import backend.team.ahachul_backend.api.train.domain.entity.TrainEntity
import backend.team.ahachul_backend.common.domain.entity.SubwayLineEntity

class GetTrainDto {

    data class Response(
        val id: Long,
        val subwayLine: SubwayLine,
        val location: Int,
        val organizationTrainNo: String,
    ) {

        companion object {
            fun of(train: TrainEntity, location: Int, organizationTrainNo: String): Response {
                return Response(
                    id = train.id,
                    subwayLine = SubwayLine.of(train.subwayLine),
                    location = location,
                    organizationTrainNo = organizationTrainNo,
                )
            }
        }
    }

    data class SubwayLine(
        val id: Long,
        val name: String,
    ) {

        companion object {
            fun of(subwayLine: SubwayLineEntity): SubwayLine {
                return SubwayLine(
                    id = subwayLine.id,
                    name = subwayLine.name,
                )
            }
        }
    }
}
