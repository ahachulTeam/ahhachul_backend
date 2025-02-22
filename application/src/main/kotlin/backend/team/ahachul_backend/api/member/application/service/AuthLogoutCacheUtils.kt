package backend.team.ahachul_backend.api.member.application.service

import backend.team.ahachul_backend.common.client.RedisClient
import backend.team.ahachul_backend.common.exception.CommonException
import backend.team.ahachul_backend.common.properties.JwtProperties
import backend.team.ahachul_backend.common.response.ResponseCode
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

@Component
class AuthLogoutCacheUtils(
    private val redisClient: RedisClient,
    private val jwtProperties: JwtProperties,
) {

    fun logout(accessToken: String) {
        alreadyLogout(accessToken)

        val key = createKey(accessToken)
        val value = now()

        redisClient.set(key, value, jwtProperties.accessTokenExpireTime, TimeUnit.SECONDS)
    }

    fun alreadyLogout(accessToken: String) {
        if (isLogout(accessToken)) {
            throw CommonException(ResponseCode.ALREADY_LOGOUT_TOKEN)
        }
    }

    fun isNotLogout(accessToken: String): Boolean {
        val key = createKey(accessToken)
        return redisClient.get(key).isNullOrEmpty()
    }

    private fun isLogout(accessToken: String) = !isNotLogout(accessToken)

    private fun createKey(accessToken: String): String {
        return "${LOGOUT_BLACKLIST_REDIS_PREFIX}${accessToken}"
    }

    private fun now(): String {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"))
    }

    companion object {
        const val LOGOUT_BLACKLIST_REDIS_PREFIX = "LOGOUT_BLACKLIST:"
    }
}
