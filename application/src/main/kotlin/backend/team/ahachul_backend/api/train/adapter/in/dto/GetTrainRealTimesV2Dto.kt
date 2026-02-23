package backend.team.ahachul_backend.api.train.adapter.`in`.dto

import backend.team.ahachul_backend.api.train.domain.model.UpDownType

class GetTrainRealTimesV2Dto {

    data class Request(
        val stationId: Long,
        val subwayLineId: Long,
        val upDownType: UpDownType?,
        val limit: Int? = 2,
    )

    data class Response(
        val generatedAt: String,
        val dataSource: String,
        val isStale: Boolean,
        val lastExternalRecptnAt: String,
        val freshnessSec: Int,
        val confidenceLevel: String,
        val trainRealTimes: List<TrainRealTimeV2>,
    )

    data class TrainRealTimeV2(
        val trainNo: String,
        val upDownType: UpDownType,
        val arrivalCode: String,
        val etaSec: Int,
        val etaMinDisplay: Int,
        val destinationStationDirection: String,
        val nextStationDirection: String,
    )
}
