package backend.team.ahachul_backend.common.client.impl

import backend.team.ahachul_backend.common.client.SeoulTrainClient
import backend.team.ahachul_backend.common.client.dto.StationTimesDto
import backend.team.ahachul_backend.common.dto.TrainRealTimeDto
import backend.team.ahachul_backend.common.exception.BusinessException
import backend.team.ahachul_backend.common.properties.PublicDataProperties
import backend.team.ahachul_backend.common.response.ResponseCode
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Component
class SeoulTrainClientImpl(
    private val restTemplate: RestTemplate,
    private val webClient: WebClient,
    private val publicDataProperties: PublicDataProperties,
): SeoulTrainClient {

    override fun getTrainRealTimes(stationName: String, startIndex: Int, endIndex: Int): TrainRealTimeDto {
        val url = "${publicDataProperties.realTimeStationArrivalPrefixUri}/${publicDataProperties.realTimeStationArrivalToken}/${publicDataProperties.realTimeStationArrivalSuffixUri}/$startIndex/$endIndex/$stationName"
        val response = restTemplate.exchange(url, HttpMethod.GET, null, TrainRealTimeDto::class.java).body!!
        return response.takeIf { it.status == null } ?: TrainRealTimeDto(500, null, emptyList())
    }

    override fun getTrainRealTimesByMono(stationName: String, startIndex: Int, endIndex: Int): Mono<TrainRealTimeDto> {
        val url =
            "${publicDataProperties.realTimeStationArrivalPrefixUri}/" +
                    "${publicDataProperties.realTimeStationArrivalToken}/" +
                    "${publicDataProperties.realTimeStationArrivalSuffixUri}/" +
                    "$startIndex/$endIndex/$stationName"

        return webClient.get()
            .uri(url)
            .retrieve()
            .onStatus(HttpStatusCode::isError) {
                Mono.error(BusinessException(ResponseCode.FAILED_TO_GET_TRAIN_INFO))
            }
            .bodyToMono(TrainRealTimeDto::class.java)
    }

    override fun getStationTimesByApi(request: StationTimesDto.Request): StationTimesDto.Response {
        val url = "${publicDataProperties.stationTimesPrefixUri}/${publicDataProperties.realTimeStationArrivalToken}${publicDataProperties.stationTimesSuffixUri}/" +
                "${request.startIndex}/${request.endIndex}/${request.stationCd}/${request.weekTag}/${request.inoutTag}"
        try {
            val response = restTemplate.exchange(url, HttpMethod.GET, null, StationTimesDto.Response::class.java)
            return response.body!!
        } catch (e: RestClientException) {
            throw BusinessException(ResponseCode.FAILED_STATION_TIMES_API)
        }
    }

    override fun getStationTimesByApiByMono(request: StationTimesDto.Request): Mono<StationTimesDto.Response> {
        val url =
            "${publicDataProperties.stationTimesPrefixUri}/" +
                    "${publicDataProperties.realTimeStationArrivalToken}" +
                    "${publicDataProperties.stationTimesSuffixUri}/" +
                    "${request.startIndex}/${request.endIndex}/" +
                    "${request.stationCd}/${request.weekTag}/${request.inoutTag}"

        return webClient.get()
            .uri(url)
            .retrieve()
            .onStatus(HttpStatusCode::isError) {
                Mono.error(BusinessException(ResponseCode.FAILED_STATION_TIMES_API))
            }
            .bodyToMono(StationTimesDto.Response::class.java)
    }
}
