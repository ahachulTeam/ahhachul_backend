package backend.team.ahachul_backend.common.client

import backend.team.ahachul_backend.common.dto.TrainRealTimeDto

interface SeoulTrainClient {

    fun getTrainRealTimes(stationName: String, startIndex: Int, endIndex: Int): TrainRealTimeDto
}