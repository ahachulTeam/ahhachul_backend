package backend.team.ahachul_backend.common.client

import backend.team.ahachul_backend.common.client.dto.TrainCongestionDto
import backend.team.ahachul_backend.common.exception.BusinessException
import backend.team.ahachul_backend.common.properties.PublicDataProperties
import backend.team.ahachul_backend.common.response.ResponseCode
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Component
class TrainCongestionClient(
    private val restTemplate: RestTemplate,
    private val webClient: WebClient,
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

    fun getCongestionsByMono(subwayLine: Long, trainNo: Int): Mono<TrainCongestionDto> {
        val url = "${publicDataProperties.realTimeCongestionUrl}/$subwayLine/$trainNo"

        return webClient.get()
            .uri(url)
            .header(PARAM_KEY, publicDataProperties.realTimeCongestionAppKey)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError) {
                Mono.error(BusinessException(ResponseCode.INVALID_TRAIN_NO))
            }
            .onStatus(HttpStatusCode::is5xxServerError) {
                Mono.error(BusinessException(ResponseCode.FAILED_TO_GET_TRAIN_INFO))
            }
            .bodyToMono(TrainCongestionDto::class.java)
    }

    companion object {
        const val PARAM_KEY = "appkey"
    }
}
