package backend.team.ahachul_backend.common.client.impl

import backend.team.ahachul_backend.common.exception.CommonException
import backend.team.ahachul_backend.common.properties.OAuthProperties
import backend.team.ahachul_backend.common.response.ResponseCode
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate

class KakaoMemberClientImplTest {

    private val restTemplate: RestTemplate = Mockito.mock(RestTemplate::class.java)
    private val objectMapper = ObjectMapper()
    private val oAuthProperties = OAuthProperties(
        client = mapOf(
            "kakao" to OAuthProperties.Client(
                clientId = "client-id",
                clientSecret = null,
                redirectUriPath = "/oauth/kakao/callback",
                scope = null,
                responseType = "code",
                accessType = null,
            ),
        ),
        provider = mapOf(
            "kakao" to OAuthProperties.Provider(
                loginUri = "https://kauth.kakao.com/oauth/authorize",
                tokenUri = "https://kauth.kakao.com/oauth/token",
                userInfoUri = "https://kapi.kakao.com/v2/user/me",
            ),
        ),
        properties = emptyMap(),
    )
    private val kakaoMemberClient = KakaoMemberClientImpl(restTemplate, objectMapper, oAuthProperties)

    @Test
    @DisplayName("카카오 인증 코드가 유효하지 않으면 INVALID_OAUTH_AUTHORIZATION_CODE를 반환한다.")
    fun getAccessTokenInvalidCode() {
        // given
        given(restTemplate.exchange(anyString(), Mockito.eq(HttpMethod.POST), any(), Mockito.eq(String::class.java)))
            .willThrow(HttpClientErrorException(HttpStatus.BAD_REQUEST))

        // when & then
        assertThatThrownBy {
            kakaoMemberClient.getAccessTokenByCodeAndOrigin("invalid-code", "http://localhost:3000")
        }
            .isInstanceOfSatisfying(CommonException::class.java) { commonException ->
                assertThat(commonException.code).isEqualTo(ResponseCode.INVALID_OAUTH_AUTHORIZATION_CODE)
            }
    }

    @Test
    @DisplayName("카카오 액세스 토큰이 유효하지 않으면 INVALID_OAUTH_ACCESS_TOKEN을 반환한다.")
    fun getMemberInfoInvalidAccessToken() {
        // given
        given(restTemplate.exchange(anyString(), Mockito.eq(HttpMethod.GET), any(), Mockito.eq(String::class.java)))
            .willThrow(HttpClientErrorException(HttpStatus.UNAUTHORIZED))

        // when & then
        assertThatThrownBy {
            kakaoMemberClient.getMemberInfoByAccessToken("invalid-token")
        }
            .isInstanceOfSatisfying(CommonException::class.java) { commonException ->
                assertThat(commonException.code).isEqualTo(ResponseCode.INVALID_OAUTH_ACCESS_TOKEN)
            }
    }

    private fun <T> any(): T {
        Mockito.any<T>()
        return null as T
    }

    private fun anyString(): String {
        return Mockito.anyString()
    }

}
