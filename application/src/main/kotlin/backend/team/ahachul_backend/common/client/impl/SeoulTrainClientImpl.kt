package backend.team.ahachul_backend.common.client.impl

import backend.team.ahachul_backend.common.client.SeoulTrainClient
import backend.team.ahachul_backend.common.client.dto.StationTimesDto
import backend.team.ahachul_backend.common.dto.TrainRealTimeDto
import backend.team.ahachul_backend.common.exception.BusinessException
import backend.team.ahachul_backend.common.properties.PublicDataProperties
import backend.team.ahachul_backend.common.response.ResponseCode
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientException

@Component
class SeoulTrainClientImpl(
    private val restClient: RestClient,
    private val publicDataProperties: PublicDataProperties,
): SeoulTrainClient {

    override fun getTrainRealTimes(
        stationName: String,
        startIndex: Int,
        endIndex: Int
    ): TrainRealTimeDto {
        val url =
            "${publicDataProperties.realTimeStationArrivalPrefixUri}/" +
                    "${publicDataProperties.realTimeStationArrivalToken}/" +
                    "${publicDataProperties.realTimeStationArrivalSuffixUri}/" +
                    "$startIndex/$endIndex/$stationName"

        try {
            return restClient.get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatusCode::isError) { _, _ ->
                    throw BusinessException(ResponseCode.FAILED_TO_GET_TRAIN_INFO)
                }
                .body(TrainRealTimeDto::class.java)!!
        } catch (e: RestClientException) {
            throw BusinessException(ResponseCode.FAILED_TO_GET_TRAIN_INFO)
        }
    }

    override fun getStationTimesByApi(
        request: StationTimesDto.Request
    ): StationTimesDto.Response {
        val url =
            "${publicDataProperties.stationTimesPrefixUri}/" +
                    "${publicDataProperties.realTimeStationArrivalToken}" +
                    "${publicDataProperties.stationTimesSuffixUri}/" +
                    "${request.startIndex}/${request.endIndex}/" +
                    "${request.stationCd}/${request.weekTag}/${request.inoutTag}"

        try {
            return restClient.get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatusCode::isError) { _, _ ->
                    throw BusinessException(ResponseCode.FAILED_STATION_TIMES_API)
                }
                .body(StationTimesDto.Response::class.java)!!
        } catch (e: RestClientException) {
            throw BusinessException(ResponseCode.FAILED_STATION_TIMES_API)
        }
    }
}
