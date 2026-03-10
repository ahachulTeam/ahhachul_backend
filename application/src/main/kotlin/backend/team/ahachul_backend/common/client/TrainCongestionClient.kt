package backend.team.ahachul_backend.common.client

import backend.team.ahachul_backend.common.client.dto.TrainCongestionDto
import backend.team.ahachul_backend.common.exception.BusinessException
import backend.team.ahachul_backend.common.properties.PublicDataProperties
import backend.team.ahachul_backend.common.response.ResponseCode
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class TrainCongestionClient(
    private val restClient: RestClient,
    private val publicDataProperties: PublicDataProperties
) {

    fun getCongestions(subwayLine: Long, trainNo: Int): TrainCongestionDto {
        val url = "${publicDataProperties.realTimeCongestionUrl}/$subwayLine/$trainNo"

        return restClient.get()
            .uri(url)
            .header(PARAM_KEY, publicDataProperties.realTimeCongestionAppKey)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError) { _, _ ->
                throw BusinessException(ResponseCode.INVALID_TRAIN_NO)
            }
            .onStatus(HttpStatusCode::is5xxServerError) { _, _ ->
                throw BusinessException(ResponseCode.FAILED_TO_GET_TRAIN_INFO)
            }
            .body(TrainCongestionDto::class.java)!!
    }

    companion object {
        const val PARAM_KEY = "appkey"
    }
}
