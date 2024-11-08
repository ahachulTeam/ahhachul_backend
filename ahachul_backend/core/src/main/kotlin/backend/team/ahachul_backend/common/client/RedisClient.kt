package backend.team.ahachul_backend.common.client

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class RedisClient(
    private val redisTemplate: StringRedisTemplate,
    private val objectMapper: ObjectMapper,
) {

    fun set(key: String, value: String) {
        redisTemplate.opsForValue().set(key, value)
    }

    fun set(key: String, value: String, timeout: Long, timeUnit: TimeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit)
    }

    fun set(key: String, value: Any) {
        redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(value))
    }

    fun set(key: String, value: Any, timeout: Long, timeUnit: TimeUnit) {
        redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(value), timeout, timeUnit)
    }

    fun get(key: String): String? {
        return redisTemplate.opsForValue().get(key)
    }

    fun <T> get(key: String, clazz: Class<T>): T? {
        return redisTemplate.opsForValue().get(key)?.let {
            objectMapper.readValue(it, clazz)
        }
    }

    fun delete(key: String) {
        redisTemplate.delete(key)
    }

    fun hasKey(key: String): Boolean {
        return redisTemplate.hasKey(key)
    }
}
