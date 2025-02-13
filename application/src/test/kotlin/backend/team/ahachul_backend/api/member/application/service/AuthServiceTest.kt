package backend.team.ahachul_backend.api.member.application.service

import backend.team.ahachul_backend.api.member.application.port.`in`.AuthUseCase
import backend.team.ahachul_backend.common.exception.CommonException
import backend.team.ahachul_backend.common.response.ResponseCode
import backend.team.ahachul_backend.common.utils.JwtUtils
import backend.team.ahachul_backend.config.controller.CommonServiceTestConfig
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class AuthServiceTest(
    @Autowired val authUseCase: AuthUseCase,
    @Autowired val authLogoutCacheUtils: AuthLogoutCacheUtils,
    @Autowired val jwtUtils: JwtUtils,
) : CommonServiceTestConfig() {

    @Test
    fun 로그아웃_블랙리스트_저장() {
        //given
        val createToken = jwtUtils.createToken("test", 100L)

        //when
        authUseCase.logout(createToken);

        //then
        val notLogout = authLogoutCacheUtils.isNotLogout(createToken)
        assertThat(notLogout).isFalse()
    }

    @Test
    fun 로그아웃_유효하지_않는_JWT_예외() {
        //given
        val createToken = "test"

        //when & then
        assertThatThrownBy {
            authUseCase.logout(createToken)
        }
            .isExactlyInstanceOf(CommonException::class.java)
            .hasMessage(ResponseCode.INVALID_ACCESS_TOKEN.message)
    }

    @Test
    fun 로그아웃_만료된_JWT_예외() {
        //given
        val createToken = jwtUtils.createToken("test", -86400L)

        //when & then
        assertThatThrownBy {
            authUseCase.logout(createToken)
        }
            .isExactlyInstanceOf(CommonException::class.java)
            .hasMessage(ResponseCode.EXPIRED_ACCESS_TOKEN.message)
    }

    @Test
    fun 이미_로그아웃_처리된_토큰_예외() {
        //given
        val createToken = jwtUtils.createToken("test", 10L)
        authUseCase.logout(createToken); // 로그아웃 처리

        //when & then
        assertThatThrownBy {
            authUseCase.logout(createToken)
        }
            .isExactlyInstanceOf(CommonException::class.java)
            .hasMessage(ResponseCode.ALREADY_LOGOUT_TOKEN.message)
    }
}