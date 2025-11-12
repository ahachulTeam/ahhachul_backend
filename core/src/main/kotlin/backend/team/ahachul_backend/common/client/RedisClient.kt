package backend.team.ahachul_backend.common.client

import backend.team.ahachul_backend.common.logging.Logger
import com.fasterxml.jackson.databind.ObjectMapper
import io.lettuce.core.RedisBusyException
import io.lettuce.core.XGroupCreateArgs
import io.lettuce.core.XReadArgs
import io.lettuce.core.api.sync.RedisCommands
import org.springframework.data.redis.connection.stream.ObjectRecord
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.stream.StreamMessageListenerContainer
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.concurrent.TimeUnit


@Component
class RedisClient(
    private val redisTemplate: StringRedisTemplate,
    private val objectMapper: ObjectMapper,
) {

    private val logger = Logger(javaClass)

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

    fun ackStream(key: String, consumerGroupName: String, recordId: String?) {
        this.redisTemplate.opsForStream<Any, Any>().acknowledge(key, consumerGroupName, recordId)
    }

    @Suppress("UNCHECKED_CAST")
    fun createStreamConsumerGroup(streamKey: String, consumerGroupName: String) {
        // 스트림이 없다면 생성하고 Consumer group 추가
        redisTemplate.execute { conn ->
            val commands = conn.nativeConnection as RedisCommands<String, String>
            try {
                // 0-0부터 읽는 그룹 생성 + 스트림 없으면 자동 생성(MKSTREAM)
                commands.xgroupCreate(
                    XReadArgs.StreamOffset.from(streamKey, "0-0"),
                    consumerGroupName,
                    XGroupCreateArgs.Builder.mkstream()
                )
            } catch (e: RedisBusyException) {
                logger.info("Consumer Group $consumerGroupName already exists")
            }
        }
    }

    fun createStreamMessageListenerContainer(): StreamMessageListenerContainer<String, ObjectRecord<String, String>> {
        val options: StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, ObjectRecord<String, String>> =
            StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                .builder()
                .targetType(String::class.java)
                .pollTimeout(Duration.ofSeconds(2))
                .build()

        return StreamMessageListenerContainer.create(redisTemplate.connectionFactory, options)
    }
}
