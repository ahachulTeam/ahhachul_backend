package backend.team.ahachul_backend.common.utils

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import kotlin.RuntimeException

class WebClientUtils {
    companion object {
        fun <T> get(url: String, responseClass: Class<T>, headers: Map<String, String> = emptyMap()): T {
            val client = WebClient.builder()
                    .defaultHeaders { headers.values }
                    .build()
            return client.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(responseClass)
                    .block()
                    ?: throw RuntimeException()
        }

        fun <T> post(url: String, payload: String, responseClass: Class<T>, headers: Map<String, String> = emptyMap()): T {
            val client = WebClient.builder()
                    .defaultHeaders { headers.values }
                    .build()
            return client.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(payload))
                    .retrieve()
                    .bodyToMono(responseClass)
                    .block()
                    ?: throw RuntimeException()
        }
    }
}