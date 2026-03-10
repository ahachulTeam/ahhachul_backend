package backend.team.ahachul_backend.common.config

import org.apache.hc.client5.http.config.RequestConfig
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager
import org.apache.hc.core5.util.TimeValue
import org.apache.hc.core5.util.Timeout
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestClient

@Configuration
class RestClientConfig {

    @Bean
    fun restClient(): RestClient {

        val connectionManager = PoolingHttpClientConnectionManager()
        connectionManager.maxTotal = 200
        connectionManager.defaultMaxPerRoute = 100

        val requestConfig = RequestConfig.custom()
            .setConnectTimeout(Timeout.ofSeconds(3))
            .setResponseTimeout(Timeout.ofSeconds(3))
            .build()

        val httpClient = HttpClients.custom()
            .setConnectionManager(connectionManager)
            .setDefaultRequestConfig(requestConfig)
            .evictIdleConnections(TimeValue.ofSeconds(30))
            .build()

        val requestFactory = HttpComponentsClientHttpRequestFactory(httpClient)

        return RestClient.builder()
            .requestFactory(requestFactory)
            .build()
    }
}
