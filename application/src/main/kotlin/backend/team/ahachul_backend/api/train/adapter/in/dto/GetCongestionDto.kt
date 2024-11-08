package backend.team.ahachul_backend.api.train.adapter.`in`.dto

import backend.team.ahachul_backend.api.train.application.port.`in`.command.GetCongestionCommand
import backend.team.ahachul_backend.api.train.domain.model.Congestion

class GetCongestionDto {

    data class Request(
        val subwayLineId: Long,
        val trainNo: String
    ) {
        fun toCommand(): GetCongestionCommand {
            return GetCongestionCommand(
                subwayLineId = subwayLineId,
                trainNo = trainNo
            )
        }
    }

    data class Response(
        val trainNo: String,
        val congestions : List<Section>
    ) {
        companion object {
            fun from(trainNo: String, congestions: List<Section>): Response {
                return Response(
                    trainNo = trainNo,
                    congestions = congestions
                )
            }
        }
    }

    data class Section(
        val sectionNo: Int,
        val congestionColor: String
    ) {
        companion object {
            fun from(sectionNo: Int, congestionPercent: Int): Section {
                return Section(
                    sectionNo = sectionNo,
                    congestionColor = Congestion.from(congestionPercent)
                )
            }
        }
    }
}
