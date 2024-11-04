package backend.team.ahachul_backend.api.member.adapter.web.`in`

import backend.team.ahachul_backend.api.member.adapter.web.`in`.dto.GetRedirectUrlDto
import backend.team.ahachul_backend.api.member.adapter.web.`in`.dto.GetTokenDto
import backend.team.ahachul_backend.api.member.adapter.web.`in`.dto.LoginMemberDto
import backend.team.ahachul_backend.api.member.application.port.`in`.AuthUseCase
import backend.team.ahachul_backend.api.member.domain.model.ProviderType
import backend.team.ahachul_backend.common.properties.OAuthProperties
import backend.team.ahachul_backend.config.controller.CommonDocsTestConfig
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.queryParameters
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(AuthController::class)
class AuthControllerDocsTest : CommonDocsTestConfig() {

    @MockBean
    lateinit var authUseCase: AuthUseCase

    @MockBean
    lateinit var oAuthProperties: OAuthProperties

    @Test
    fun getRedirectUrlTest() {
        // given
        val response = GetRedirectUrlDto.Response(
            redirectUrl = "redirectUrl"
        )

        given(authUseCase.getRedirectUrl(any()))
            .willReturn(response)

        // when
        val result = mockMvc.perform(
            get("/v1/auth/redirect-url")
                .param("providerType", ProviderType.KAKAO.toString())
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
            .andDo(
                document(
                    "get-redirect-url",
                    getDocsRequest(),
                    getDocsResponse(),
                    queryParameters(
                        parameterWithName("providerType").description("플랫폼 타입").attributes(getFormatAttribute("KAKAO, GOOGLE, APPLE"))
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.redirectUrl").type(JsonFieldType.STRING).description("리다이렉트 URL")
                    )
                )
            )
    }

    @Test
    fun loginTest() {
        // given
        val response = LoginMemberDto.Response(
            memberId = "1",
            isNeedAdditionalUserInfo = true,
            accessToken = "accessToken",
            accessTokenExpiresIn = 3600,
            refreshToken = "refreshToken",
            refreshTokenExpiresIn = 259200
        )

        given(authUseCase.login(any()))
            .willReturn(response)

        // TODO 개발용 코드. 추후 삭제
        given(oAuthProperties.client).willReturn(
            mapOf("kakao" to OAuthProperties.Client("", "", "", "", "", ""))
        )

        val request = LoginMemberDto.Request(
            providerType = ProviderType.KAKAO,
            providerCode = "providerCode"
        )

        // when
        val result = mockMvc.perform(
            post("/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
            .andDo(
                document(
                    "login",
                    getDocsRequest(),
                    getDocsResponse(),
                    requestFields(
                        fieldWithPath("providerType").type("ProviderType").description("플랫폼 타입").attributes(getFormatAttribute("KAKAO, GOOGLE, APPLE")),
                        fieldWithPath("providerCode").type(JsonFieldType.STRING).description("인가 코드")
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.memberId").type(JsonFieldType.STRING).description("사용자 Identification Key"),
                        fieldWithPath("result.isNeedAdditionalUserInfo").type(JsonFieldType.BOOLEAN).description("사용자 추가 정보 입력 여부"),
                        fieldWithPath("result.accessToken").type(JsonFieldType.STRING).description("엑세스 토큰. 만료 기간 : 1시간"),
                        fieldWithPath("result.accessTokenExpiresIn").type(JsonFieldType.NUMBER).description("엑세스 토큰 만료 기간"),
                        fieldWithPath("result.refreshToken").type(JsonFieldType.STRING).description("리프레쉬 토큰. 만료 기간 : 30일"),
                        fieldWithPath("result.refreshTokenExpiresIn").type(JsonFieldType.NUMBER).description("리프레쉬 토큰 만료 기간"),
                    )
                ),
            )
    }

    @Test
    fun getTokenTest() {
        // given
        val response = GetTokenDto.Response(
            accessToken = "accessToken",
            accessTokenExpiresIn = 3600,
            refreshToken = "refreshToken",
            refreshTokenExpiresIn = 259200
        )

        given(authUseCase.getToken(any()))
            .willReturn(response)

        val request = GetTokenDto.Request(
            refreshToken = "refreshToken"
        )

        // when
        val result = mockMvc.perform(
            post("/v1/auth/token/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
            .andDo(
                document(
                    "get-token",
                    getDocsRequest(),
                    getDocsResponse(),
                    requestFields(
                        fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("리프레쉬 토큰"),
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.accessToken").type(JsonFieldType.STRING).description("엑세스 토큰. 만료 기간 : 1시간"),
                        fieldWithPath("result.accessTokenExpiresIn").type(JsonFieldType.NUMBER).description("엑세스 토큰 만료 기간"),
                        fieldWithPath("result.refreshToken").type(JsonFieldType.STRING).description("리프레쉬 토큰. 만료 기간 : 30일").optional(),
                        fieldWithPath("result.refreshTokenExpiresIn").type(JsonFieldType.NUMBER).description("리프레쉬 토큰 만료 기간").optional(),
                    )
                ),
            )
    }
}