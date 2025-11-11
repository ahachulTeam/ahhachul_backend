package backend.team.ahachul_backend.common.config

import org.apache.hc.client5.http.config.RequestConfig
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager
import org.apache.hc.core5.util.Timeout
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestTemplate
import java.util.concurrent.TimeUnit

@Configuration
class RestTemplateConfig {

    @Bean
    fun poolingHttpClientConnectionManager(): PoolingHttpClientConnectionManager {
        // 커넥션 풀 설정
        val connectionManager = PoolingHttpClientConnectionManager().apply {
            maxTotal = 200                      // 전체 커넥션 최대 수
            defaultMaxPerRoute = 50             // 라우트(target host)당 최대 커넥션 수
        }
        return connectionManager
    }

    @Bean
    fun restTemplate(
        connectionManager: PoolingHttpClientConnectionManager
    ): RestTemplate {
        // 타임아웃 설정
        val requestConfig = RequestConfig.custom()
            .setConnectionRequestTimeout(Timeout.of(3000, TimeUnit.MILLISECONDS)) // 커넥션 풀에서 커넥션을 가져올 때 타임아웃
            .setResponseTimeout(Timeout.of(3000, TimeUnit.MILLISECONDS))     // 응답 대기 시간 (소켓 읽기)
            .build()

        val httpClient = HttpClients.custom()
            .setConnectionManager(connectionManager)
            .setDefaultRequestConfig(requestConfig)
            .build()

        val requestFactory = HttpComponentsClientHttpRequestFactory(httpClient)
        return RestTemplate(requestFactory)
    }
}
