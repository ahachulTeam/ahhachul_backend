package backend.team.ahachul_backend.common.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "resilience4j.circuit-breaker")
class CircuitBreakerProperty {
    var failureRateThreshold: Float = Float.MIN_VALUE
    var slowCallDurationThreshold: Long = Long.MIN_VALUE
    var slowCallRateThreshold: Float = Float.MIN_VALUE
    var waitDurationInOpenState: Long = Long.MIN_VALUE
    var slidingWindowSize: Int = Int.MIN_VALUE
    var permittedNumberOfCallsInHalfOpenState: Int = Int.MIN_VALUE
}
