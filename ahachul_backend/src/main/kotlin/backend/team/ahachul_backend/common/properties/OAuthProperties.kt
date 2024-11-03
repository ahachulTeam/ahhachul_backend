package backend.team.ahachul_backend.common.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "oauth")
class OAuthProperties(
    val client: Map<String, Client> = HashMap(),
    val provider: Map<String, Provider> = HashMap(),
    val properties: Map<String, Properties> = HashMap(),
) {

    data class Client(
        val clientId: String,
        val clientSecret: String?,
        var redirectUriPath: String,
        val scope: String?,
        val responseType: String,
        val accessType: String?,
    )

    data class Provider(
        val loginUri: String,
        val tokenUri: String,
        val userInfoUri: String?
    )

    data class Properties(
        val kid: String?,
        val iss: String?,
        val sub: String?,
        val aud: String?
    )
}