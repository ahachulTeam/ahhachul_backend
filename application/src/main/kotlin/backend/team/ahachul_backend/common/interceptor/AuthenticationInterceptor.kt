package backend.team.ahachul_backend.common.interceptor

import backend.team.ahachul_backend.api.member.application.service.AuthLogoutCacheUtils
import backend.team.ahachul_backend.common.annotation.Authentication
import backend.team.ahachul_backend.common.client.RedisClient
import backend.team.ahachul_backend.common.exception.CommonException
import backend.team.ahachul_backend.common.response.ResponseCode
import backend.team.ahachul_backend.common.utils.JwtUtils
import backend.team.ahachul_backend.common.utils.RequestUtils
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.security.SignatureException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor

@Component
class AuthenticationInterceptor(
        private val jwtUtils: JwtUtils,
        private val redisClient: RedisClient,
        private val authLogoutCacheUtils: AuthLogoutCacheUtils,
): HandlerInterceptor {

    companion object {
        const val AUTH_PREFIX = "Bearer "
    }

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (handler !is HandlerMethod) return true

        val authentication = handler.getMethodAnnotation(Authentication::class.java)
        authentication ?: return true

        try {
            val jwtTokenExcludePrefix = parseJwtToken(request)
            authLogoutCacheUtils.alreadyLogout(jwtTokenExcludePrefix)
            val verifiedJwtToken = jwtUtils.verify(jwtTokenExcludePrefix)
            val authenticatedMemberId = verifiedJwtToken.body.subject

            RequestUtils.setAttribute("memberId", authenticatedMemberId)
        } catch (e: Exception) {
            if (!authentication.required) {
                return true
            }
            when (e) {
                is SignatureException, is UnsupportedJwtException, is IllegalArgumentException, is MalformedJwtException -> {
                    throw CommonException(ResponseCode.INVALID_ACCESS_TOKEN, e)
                }

                is ExpiredJwtException -> {
                    throw CommonException(ResponseCode.EXPIRED_ACCESS_TOKEN, e)
                }

                else -> {
                    if (e.message == ResponseCode.BLOCKED_MEMBER.message) throw e
                    if (e.message == ResponseCode.ALREADY_LOGOUT_TOKEN.message) throw e
                    throw CommonException(ResponseCode.INTERNAL_SERVER_ERROR, e)
                }
            }
        }
        return true
    }

    private fun parseJwtToken(request: HttpServletRequest): String{
        val jwtToken = request.getHeader("Authorization")

        if (!jwtToken.startsWith(AUTH_PREFIX)) {
            throw UnsupportedJwtException("not supported jwt")
        }
        return jwtToken.substring(AUTH_PREFIX.length)
    }

    @Deprecated("Admin function is not supported")
    private fun isBlockedMember(memberId: String): Boolean {
        return !redisClient.get("blocked-member:${memberId}").isNullOrEmpty()
    }
}
