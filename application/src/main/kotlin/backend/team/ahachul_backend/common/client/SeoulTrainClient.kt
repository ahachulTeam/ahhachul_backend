package backend.team.ahachul_backend.common.client

import backend.team.ahachul_backend.common.client.dto.StationTimesDto
import backend.team.ahachul_backend.common.dto.TrainRealTimeDto
import reactor.core.publisher.Mono

interface SeoulTrainClient {

    fun getTrainRealTimes(stationName: String, startIndex: Int, endIndex: Int): TrainRealTimeDto

    fun getTrainRealTimesByMono(stationName: String, startIndex: Int, endIndex: Int): Mono<TrainRealTimeDto>

    fun getStationTimesByApi(request: StationTimesDto.Request): StationTimesDto.Response

    fun getStationTimesByApiByMono(request: StationTimesDto.Request): Mono<StationTimesDto.Response>
}
