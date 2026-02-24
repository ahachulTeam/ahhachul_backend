package backend.team.ahachul_backend.common.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "delay-proof")
class DelayProofProperties(
    var proofTtlSeconds: Long = 60L * 60L * 24L,
    var communityWindowMinutes: Long = 30L,
    var signalPageSize: Int = 100,
    var shareBaseUrl: String = "https://ahhachul.com",
    var incidentFeedUrl: String = "",
    var incidentApiKey: String = "",
    var incidentTimeoutSeconds: Long = 2L,
)
