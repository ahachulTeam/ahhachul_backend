package backend.team.ahachul_backend.common.client

import backend.team.ahachul_backend.common.logging.Logger
import com.fasterxml.jackson.databind.ObjectMapper
import io.lettuce.core.RedisBusyException
import io.lettuce.core.XGroupCreateArgs
import io.lettuce.core.XReadArgs
import io.lettuce.core.api.sync.RedisCommands
import org.springframework.data.domain.Range
import org.springframework.data.redis.connection.stream.*
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer
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

    fun ackStream(key: String, consumerGroupName: String, recordId: RecordId?) {
        this.redisTemplate.opsForStream<String, String>().acknowledge(key, consumerGroupName, recordId)
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

    fun createStreamMessageListenerContainer(): StreamMessageListenerContainer<String, MapRecord<String, String, String>> {
        val options = StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder()
            .hashKeySerializer<String, String>(StringRedisSerializer())
            .hashValueSerializer<String, String>(StringRedisSerializer())
            .pollTimeout(Duration.ofMillis(20))
            .build()

        return StreamMessageListenerContainer.create(redisTemplate.connectionFactory, options)
    }

    fun findPendingMessages(streamKey: String, consumerGroupName: String, consumerName: String): PendingMessages {
        val range: Range<RecordId> = Range.unbounded()
        return redisTemplate.opsForStream<String, String>()
            .pending(streamKey, Consumer.from(consumerGroupName, consumerName), range, 100L)
    }

    fun pickIdleConsumers(n: Int, streamKey: String, consumerGroupName: String): List<String> {
        val consumers: StreamInfo.XInfoConsumers = redisTemplate.opsForStream<String, String>()
            .consumers(streamKey, consumerGroupName) // XINFO CONSUMERS

        if (consumers.isEmpty) {
            return listOf()
        }

        // pending 개수 오름차순 → idle 큰 순으로 정렬해 n개 뽑기
        val sortedList = consumers.sortedWith(
            compareBy<StreamInfo.XInfoConsumer> { it.pendingCount() }
                .thenByDescending { it.idleTimeMs() }
        )
        return sortedList.take(n).map { it.consumerName() }
    }

    fun claimStreamMessage(
        streamKey: String, consumer: Consumer, minIdleTime: Duration, recordId: RecordId,
    ): List<MapRecord<String, String, String>> {
        return redisTemplate.opsForStream<String, String>().claim(
            streamKey, consumer.group, consumer.name, minIdleTime, recordId
        )
    }

    fun findStreamMessageById(streamKey: String, id: String): MapRecord<String, String, String>? {
        return redisTemplate
            .opsForStream<String, String>()
            .range(streamKey, Range.closed(id, id))
            ?.firstOrNull()
    }
}
