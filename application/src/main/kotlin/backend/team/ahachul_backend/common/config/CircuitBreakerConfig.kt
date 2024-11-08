package backend.team.ahachul_backend.common.config

import backend.team.ahachul_backend.common.exception.BusinessException
import backend.team.ahachul_backend.common.properties.CircuitBreakerProperty
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
class CircuitBreakerConfig(
    private val circuitBreakerProperty: CircuitBreakerProperty
) {

    /**
     * DEFAULT
     */
    @Bean
    fun circuitBreakerRegistry(): CircuitBreakerRegistry {
        return CircuitBreakerRegistry.ofDefaults()
    }

    /**
     * CUSTOM : 비즈니스 예외 제외
     */
    @Bean
    fun circuitBreaker(circuitBreakerRegistry: CircuitBreakerRegistry): CircuitBreaker {
        val circuitBreakerConfig = CircuitBreakerConfig.custom()
            .failureRateThreshold(circuitBreakerProperty.failureRateThreshold)
            .slowCallDurationThreshold(Duration.ofMillis(circuitBreakerProperty.slowCallDurationThreshold))
            .slowCallRateThreshold(circuitBreakerProperty.slowCallRateThreshold)
            .waitDurationInOpenState(Duration.ofMillis(circuitBreakerProperty.waitDurationInOpenState))
            .slidingWindowSize(circuitBreakerProperty.slidingWindowSize)
            .ignoreExceptions(BusinessException::class.java)
            .permittedNumberOfCallsInHalfOpenState(circuitBreakerProperty.permittedNumberOfCallsInHalfOpenState)
            .build()

        return circuitBreakerRegistry.circuitBreaker(
            CUSTOM_CIRCUIT_BREAKER, circuitBreakerConfig
        )
    }

    companion object {
        const val CUSTOM_CIRCUIT_BREAKER = "custom-circuit-breaker"
    }
}
