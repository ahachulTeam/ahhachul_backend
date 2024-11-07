package backend.team.ahachul_backend.api.train.adapter.`in`.dto

import backend.team.ahachul_backend.api.train.domain.model.TrainArrivalCode
import backend.team.ahachul_backend.api.train.domain.model.UpDownType
import backend.team.ahachul_backend.common.dto.RealtimeArrivalListDTO
import com.fasterxml.jackson.annotation.JsonIgnore

class GetTrainRealTimesDto {

    data class Request(
        val stationId: Long,
        val subwayLineId: Long,
        val upDownType: UpDownType?
    ) {
    }

    data class Response(
        val trainRealTimes: List<TrainRealTime>,
    ) {
    }

    data class TrainRealTime(
        @JsonIgnore
        val subwayId: String?,
        @JsonIgnore
        val stationOrder: Int?,
        val upDownType: UpDownType,
        val nextStationDirection: String,
        val destinationStationDirection: String,
        val trainNum: String,
        val currentArrivalTime: Int,
        val currentTrainArrivalCode: TrainArrivalCode,
    ) {

        companion object {
            fun of(it: RealtimeArrivalListDTO, stationOrder: Int): TrainRealTime {
                val trainDirection = it.trainLineNm.split("-")
                return TrainRealTime(
                    subwayId = it.subwayId,
                    stationOrder = stationOrder,
                    upDownType = UpDownType.from(it.updnLine),
                    nextStationDirection = trainDirection[1].trim(),
                    destinationStationDirection = trainDirection[0].trim(),
                    trainNum = it.btrainNo,
                    currentArrivalTime = if (stationOrder == Int.MAX_VALUE) { 0 } else stationOrder,
                    currentTrainArrivalCode = TrainArrivalCode.from(it.arvlCd)
                )
            }
        }
    }
}
