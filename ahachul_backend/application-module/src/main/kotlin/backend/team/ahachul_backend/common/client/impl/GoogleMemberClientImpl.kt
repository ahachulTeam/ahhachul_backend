package backend.team.ahachul_backend.common.client.impl

import backend.team.ahachul_backend.api.member.domain.model.ProviderType
import backend.team.ahachul_backend.common.client.GoogleMemberClient
import backend.team.ahachul_backend.common.dto.GoogleAccessTokenDto
import backend.team.ahachul_backend.common.dto.GoogleUserInfoDto
import backend.team.ahachul_backend.common.exception.CommonException
import backend.team.ahachul_backend.common.properties.OAuthProperties
import backend.team.ahachul_backend.common.response.ResponseCode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.*
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestTemplate
import java.util.*

@Component
class GoogleMemberClientImpl(
    private val restTemplate: RestTemplate,
    private val oAuthProperties: OAuthProperties
): GoogleMemberClient {
    companion object {
        val PROVIDER = ProviderType.GOOGLE.toString().lowercase(Locale.getDefault())
    }

    private val client :OAuthProperties.Client =  oAuthProperties.client[PROVIDER]!!
    private val provider :OAuthProperties.Provider =  oAuthProperties.provider[PROVIDER]!!
    val objectMapper: ObjectMapper = ObjectMapper()

    override fun getAccessTokenByCodeAndOrigin(code: String, origin: String?): String {
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_FORM_URLENCODED
        }
        val httpEntity = HttpEntity(getHttpBodyParams(code, origin), headers)
        val response = restTemplate.exchange(provider.tokenUri, HttpMethod.POST, httpEntity, String::class.java)

        if (response.statusCode == HttpStatus.OK) {
            return objectMapper.readValue(response.body, GoogleAccessTokenDto::class.java).accessToken
        }
        throw CommonException(ResponseCode.INVALID_OAUTH_AUTHORIZATION_CODE)
    }

    private fun getHttpBodyParams(code: String, origin: String?): LinkedMultiValueMap<String, String?>{
        val params = LinkedMultiValueMap<String, String?>()
        params["code"] = code
        params["client_id"] = client.clientId
        params["client_secret"] = client.clientSecret
        params["redirect_uri"] = client.getRedirectUri(origin)
        params["grant_type"] = "authorization_code"
        return params
    }

    override fun getMemberInfoByAccessToken(accessToken: String): GoogleUserInfoDto {
        val headers = HttpHeaders().apply {
            setBearerAuth(accessToken)
        }
        val httpEntity = HttpEntity<Any>(headers)
        val response = restTemplate.exchange(provider.userInfoUri!!, HttpMethod.GET, httpEntity, String::class.java)

        if (response.statusCode == HttpStatus.OK) {
            return objectMapper.readValue(response.body, GoogleUserInfoDto::class.java)
        }
        throw CommonException(ResponseCode.INVALID_OAUTH_ACCESS_TOKEN)
    }
}