package backend.team.ahachul_backend.common.client.impl

import backend.team.ahachul_backend.api.member.domain.model.ProviderType
import backend.team.ahachul_backend.common.client.KakaoMemberClient
import backend.team.ahachul_backend.common.dto.KakaoAccessTokenDto
import backend.team.ahachul_backend.common.dto.KakaoMemberInfoDto
import backend.team.ahachul_backend.common.exception.CommonException
import backend.team.ahachul_backend.common.properties.OAuthProperties
import backend.team.ahachul_backend.common.response.ResponseCode
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import java.util.*

@Component
class KakaoMemberClientImpl(
        private val restTemplate: RestTemplate,
        private val objectMapper: ObjectMapper,
        oAuthProperties: OAuthProperties
): KakaoMemberClient {
    companion object {
        val PROVIDER = ProviderType.KAKAO.toString().lowercase(Locale.getDefault())
    }

    private val client : OAuthProperties.Client = oAuthProperties.client[PROVIDER]!!
    private val provider : OAuthProperties.Provider = oAuthProperties.provider[PROVIDER]!!

    override fun getAccessTokenByCodeAndOrigin(code: String, origin: String?): String {
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_FORM_URLENCODED
        }

        val params = LinkedMultiValueMap<String, String>()
        params.add("grant_type", "authorization_code")
        params.add("client_id", client.clientId)
        params.add("redirect_uri", client.getRedirectUri(origin))
        params.add("code", code)

        val request = HttpEntity(params, headers)
        val url = provider.tokenUri

        val response = restTemplate.exchange(url, HttpMethod.POST, request, String::class.java)
        return objectMapper.readValue(response.body, KakaoAccessTokenDto::class.java).accessToken
    }

    override fun getMemberInfoByAccessToken(accessToken: String): KakaoMemberInfoDto {
        val headers = HttpHeaders()
        headers.set("Authorization", "Bearer $accessToken")

        val params = LinkedMultiValueMap<String, String>()

        val request = HttpEntity<MultiValueMap<String, String>>(params, headers)

        val response = restTemplate.exchange(
                provider.userInfoUri!!,
                HttpMethod.GET,
                request,
                String::class.java
        )

        try {
            return objectMapper.readValue(response.body, KakaoMemberInfoDto::class.java)
        } catch (e: JsonProcessingException) {
            throw CommonException(ResponseCode.BAD_REQUEST)
        }
    }
}