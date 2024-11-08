package backend.team.ahachul_backend.common.client.impl

import backend.team.ahachul_backend.common.client.SeoulTrainClient
import backend.team.ahachul_backend.common.dto.TrainRealTimeDto
import backend.team.ahachul_backend.common.properties.PublicDataProperties
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class SeoulTrainClientImpl(
    private val restTemplate: RestTemplate,
    private val publicDataProperties: PublicDataProperties,
): SeoulTrainClient {

    override fun getTrainRealTimes(stationName: String, startIndex: Int, endIndex: Int): TrainRealTimeDto {
        val url = "${publicDataProperties.realTimeStationArrivalPrefixUri}/${publicDataProperties.realTimeStationArrivalToken}/${publicDataProperties.realTimeStationArrivalSuffixUri}/$startIndex/$endIndex/$stationName"
        val response = restTemplate.exchange(url, HttpMethod.GET, null, TrainRealTimeDto::class.java).body!!
        return response.takeIf { it.status == null } ?: TrainRealTimeDto(500, null, emptyList())
    }
}