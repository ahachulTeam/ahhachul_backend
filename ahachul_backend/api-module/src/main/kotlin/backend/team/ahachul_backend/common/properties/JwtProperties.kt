package backend.team.ahachul_backend.common.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "jwt")
class JwtProperties (
    var secretKey: String = "",
    var accessTokenExpireTime: Long = 0,
    var refreshTokenExpireTime: Long = 0
)