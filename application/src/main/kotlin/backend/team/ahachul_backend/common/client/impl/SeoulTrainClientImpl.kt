package backend.team.ahachul_backend.common.client.impl

import backend.team.ahachul_backend.common.client.SeoulTrainClient
import backend.team.ahachul_backend.common.client.dto.StationTimesDto
import backend.team.ahachul_backend.common.dto.TrainRealTimeDto
import backend.team.ahachul_backend.common.exception.BusinessException
import backend.team.ahachul_backend.common.properties.PublicDataProperties
import backend.team.ahachul_backend.common.response.ResponseCode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate

@Component
class SeoulTrainClientImpl(
    private val restTemplate: RestTemplate,
    private val publicDataProperties: PublicDataProperties,
    private val objectMapper: ObjectMapper,
): SeoulTrainClient {

    override fun getTrainRealTimes(stationName: String, startIndex: Int, endIndex: Int): TrainRealTimeDto {
        val url = "${publicDataProperties.realTimeStationArrivalPrefixUri}/${publicDataProperties.realTimeStationArrivalToken}/${publicDataProperties.realTimeStationArrivalSuffixUri}/$startIndex/$endIndex/$stationName"
        val response = restTemplate.exchange(url, HttpMethod.GET, null, TrainRealTimeDto::class.java).body!!
        return response.takeIf { it.status == null } ?: TrainRealTimeDto(500, null, emptyList())
    }

    override fun getStationTimesByApi(request: StationTimesDto.Request): StationTimesDto.Response {
        val url = "${publicDataProperties.stationTimesPrefixUri}/${publicDataProperties.realTimeStationArrivalToken}${publicDataProperties.stationTimesSuffixUri}/" +
                "${request.startIndex}/${request.endIndex}/${request.stationCd}/${request.weekTag}/${request.inoutTag}"
        try {
            val response = restTemplate.exchange(url, HttpMethod.GET, null, String::class.java)
            val body = response.body ?: throw BusinessException(ResponseCode.FAILED_STATION_TIMES_API)
            return parseStationTimesResponse(body)
        } catch (e: RestClientException) {
            throw BusinessException(ResponseCode.FAILED_STATION_TIMES_API)
        }
    }

    private fun parseStationTimesResponse(rawBody: String): StationTimesDto.Response {
        try {
            val rootNode = objectMapper.readTree(rawBody)

            if (rootNode.has("SearchSTNTimeTableByIDService")) {
                return objectMapper.treeToValue(rootNode, StationTimesDto.Response::class.java)
            }

            if (rootNode.has("RESULT")) {
                val result = objectMapper.treeToValue(
                    rootNode["RESULT"],
                    StationTimesDto.StationTimesResult::class.java
                )
                return StationTimesDto.Response(
                    stationTimesTable = StationTimesDto.StationTimesTable(
                        totalCount = 0,
                        result = result,
                        rows = emptyList(),
                    )
                )
            }
        } catch (e: BusinessException) {
            throw e
        } catch (e: Exception) {
            throw BusinessException(ResponseCode.FAILED_STATION_TIMES_API)
        }

        throw BusinessException(ResponseCode.FAILED_STATION_TIMES_API)
    }
}
