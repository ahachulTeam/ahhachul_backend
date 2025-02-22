package backend.team.ahachul_backend.api.member.adapter.web.`in`

import backend.team.ahachul_backend.api.member.adapter.web.`in`.support.LogoutSupportTestController
import backend.team.ahachul_backend.common.exception.CommonException
import backend.team.ahachul_backend.common.interceptor.AuthenticationInterceptor
import backend.team.ahachul_backend.common.response.ResponseCode
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(LogoutSupportTestController::class)
class AuthLogoutControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var authenticationInterceptor: AuthenticationInterceptor

    @BeforeEach
    fun setup() {
        given(authenticationInterceptor.preHandle(any(), any(), any()))
            .willThrow(CommonException(ResponseCode.ALREADY_LOGOUT_TOKEN))
    }

    @Test
    fun 로그아웃_토큰_요청시_예외발생() {
        //when & then
        mockMvc.perform(
            get("/test/support/authenticated")
                .header("Authorization", "Bearer logout-token")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isUnauthorized)
    }

    fun <T> any(): T {
        Mockito.any<T>()
        return null as T
    }
}