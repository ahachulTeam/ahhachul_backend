package backend.team.ahachul_backend.common.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource

@Configuration
@ConfigurationProperties(prefix = "socket-server")
data class SocketServerProperties(
    var port: String = "",
    var configPath: String = ""
)