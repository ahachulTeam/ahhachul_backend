package backend.team.ahachul_backend.api.member.adapter.web.`in`

import backend.team.ahachul_backend.api.member.adapter.web.`in`.dto.GetRedirectUrlDto
import backend.team.ahachul_backend.api.member.adapter.web.`in`.dto.GetTokenDto
import backend.team.ahachul_backend.api.member.adapter.web.`in`.dto.LoginMemberDto
import backend.team.ahachul_backend.api.member.adapter.web.`in`.dto.LogoutMemberDto
import backend.team.ahachul_backend.api.member.application.port.`in`.AuthUseCase
import backend.team.ahachul_backend.api.member.application.port.`in`.command.GetRedirectUrlCommand
import backend.team.ahachul_backend.api.member.domain.model.ProviderType
import backend.team.ahachul_backend.common.exception.CommonException
import backend.team.ahachul_backend.common.response.CommonResponse
import backend.team.ahachul_backend.common.response.ResponseCode
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.security.SignatureException
import org.springframework.web.bind.annotation.*

@RestController
class AuthController(
    private val authUseCase: AuthUseCase,
) {

    @GetMapping("/v1/auth/redirect-url")
    fun getRedirectUrl(@RequestHeader(value = "Origin") origin: String?, @RequestParam providerType: ProviderType): CommonResponse<GetRedirectUrlDto.Response> {
        return CommonResponse.success(authUseCase.getRedirectUrl(GetRedirectUrlCommand(origin, providerType)))
    }

    @PostMapping("/v1/auth/login")
    fun login(@RequestHeader(value = "Origin") origin: String?, @RequestBody request: LoginMemberDto.Request): CommonResponse<LoginMemberDto.Response> {
        return CommonResponse.success(authUseCase.login(request.toCommand(origin)))
    }

    @PostMapping("/v1/auth/logout")
    fun logout(@RequestBody request: LogoutMemberDto.Request): CommonResponse<*> {
        authUseCase.logout(request.accessToken)
        return CommonResponse.success()
    }

    @PostMapping("/v1/auth/token/refresh")
    fun getToken(@RequestBody request: GetTokenDto.Request): CommonResponse<GetTokenDto.Response> {
        try {
            return CommonResponse.success(authUseCase.getToken(request.toCommand()))
        } catch (e: Exception) {
            throw when (e) {
                is SignatureException, is UnsupportedJwtException, is IllegalArgumentException, is MalformedJwtException -> CommonException(ResponseCode.INVALID_REFRESH_TOKEN)
                is ExpiredJwtException -> CommonException(ResponseCode.EXPIRED_REFRESH_TOKEN)
                else -> CommonException(ResponseCode.INTERNAL_SERVER_ERROR)
            }
        }
    }
}