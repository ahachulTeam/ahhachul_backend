package backend.team.ahachul_backend.common.client

import backend.team.ahachul_backend.common.client.dto.TrainCongestionDto
import backend.team.ahachul_backend.common.exception.BusinessException
import backend.team.ahachul_backend.common.properties.PublicDataProperties
import backend.team.ahachul_backend.common.response.ResponseCode
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate

@Component
class TrainCongestionClient(
    private val restTemplate: RestTemplate,
    private val publicDataProperties: PublicDataProperties
) {

    fun getCongestions(subwayLine: Long, trainNo: Int): TrainCongestionDto {
        val url = "${publicDataProperties.realTimeCongestionUrl}/$subwayLine/$trainNo"

        try {
            val response = restTemplate.exchange(url, HttpMethod.GET, getHttpEntity(), TrainCongestionDto::class.java)
            return response.body!!
        } catch (e: HttpClientErrorException) {
            throw BusinessException(ResponseCode.INVALID_TRAIN_NO)
        }
    }

    private fun getHttpEntity(): HttpEntity<Any> {
        val headers = HttpHeaders()
        headers.set(PARAM_KEY, publicDataProperties.realTimeCongestionAppKey)
        return HttpEntity(headers)
    }

    companion object {
        const val PARAM_KEY = "appkey"
    }
}
