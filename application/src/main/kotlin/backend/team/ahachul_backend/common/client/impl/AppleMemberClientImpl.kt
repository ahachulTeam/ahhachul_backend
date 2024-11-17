package backend.team.ahachul_backend.common.client.impl

import backend.team.ahachul_backend.api.member.domain.model.ProviderType
import backend.team.ahachul_backend.common.client.AppleMemberClient
import backend.team.ahachul_backend.common.dto.AppleAccessTokenDto
import backend.team.ahachul_backend.common.dto.AppleUserInfoDto
import backend.team.ahachul_backend.common.exception.CommonException
import backend.team.ahachul_backend.common.properties.OAuthProperties
import backend.team.ahachul_backend.common.response.ResponseCode
import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm.ES256
import org.springframework.core.io.ClassPathResource
import org.springframework.http.*
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestTemplate
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.spec.KeySpec
import java.security.spec.PKCS8EncodedKeySpec
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

@Component
class AppleMemberClientImpl(
        private val restTemplate: RestTemplate,
        private val oAuthProperties: OAuthProperties
): AppleMemberClient {
    companion object {
        val PROVIDER = ProviderType.APPLE.toString().lowercase(Locale.getDefault())
    }

    private val client : OAuthProperties.Client = oAuthProperties.client[PROVIDER]!!
    private val provider : OAuthProperties.Provider = oAuthProperties.provider[PROVIDER]!!
    private val properties : OAuthProperties.Properties = oAuthProperties.properties[PROVIDER]!!

    val objectMapper: ObjectMapper = ObjectMapper()

    override fun getIdTokenByCodeAndOrigin(code: String, origin: String?): String {
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_FORM_URLENCODED
        }
        val httpEntity = HttpEntity(getHttpBodyParams(code, origin), headers)
        val response = restTemplate.exchange(provider.tokenUri, HttpMethod.POST, httpEntity, String::class.java)

        if (response.statusCode == HttpStatus.OK) {
            return objectMapper.readValue(response.body, AppleAccessTokenDto::class.java).idToken
        }
        throw CommonException(ResponseCode.INVALID_OAUTH_AUTHORIZATION_CODE)
    }

    override fun getMemberInfoByIdToken(idToken: String): AppleUserInfoDto {
        val tokenParts: List<String> = idToken.split(".")

        val payload = String(Base64.getDecoder().decode(tokenParts[1]))

        return objectMapper.readValue(payload, AppleUserInfoDto::class.java)
    }

    private fun getHttpBodyParams(code: String, origin: String?): LinkedMultiValueMap<String, String?> {
        val params = LinkedMultiValueMap<String, String?>()

        params["code"] = code
        params["client_id"] = client.clientId
        params["client_secret"] = getClientSecret()
        params["redirect_uri"] = client.getRedirectUri(origin)
        params["grant_type"] = "authorization_code"

        return params
    }

    private fun getClientSecret(): String {
        val now = LocalDateTime.now()

        return Jwts.builder()
            .setHeaderParam("kid", properties.kid)
            .setIssuer(properties.iss)
            .setIssuedAt(localDateTimeToDate(now))
            .setExpiration(localDateTimeToDate(now.plusMonths(6)))
            .setSubject(properties.sub)
            .setAudience(properties.aud)
            .signWith(getKey(), ES256)
            .compact()
    }

    private fun localDateTimeToDate(now: LocalDateTime): Date {
        return Date.from(now.atZone(ZoneId.systemDefault()).toInstant())
    }

    private fun getKey(): PrivateKey {
        return KeyFactory.getInstance("EC").generatePrivate(getKeySpec())
    }

    private fun getKeySpec(): KeySpec {
        return PKCS8EncodedKeySpec(Base64.getDecoder().decode(getKeyBytes()))
    }

    private fun getKeyBytes(): ByteArray {
        return ClassPathResource(properties.kid + ".p8").inputStream.readAllBytes()
    }
}