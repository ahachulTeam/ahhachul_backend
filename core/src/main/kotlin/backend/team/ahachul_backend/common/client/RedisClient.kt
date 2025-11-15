package backend.team.ahachul_backend.common.client

import backend.team.ahachul_backend.common.logging.Logger
import com.fasterxml.jackson.databind.ObjectMapper
import io.lettuce.core.api.async.RedisAsyncCommands
import io.lettuce.core.codec.StringCodec
import io.lettuce.core.output.StatusOutput
import io.lettuce.core.protocol.CommandArgs
import io.lettuce.core.protocol.CommandKeyword
import io.lettuce.core.protocol.CommandType
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
        val streamExists = redisTemplate.hasKey(streamKey)

        if (!streamExists) {
            val native = redisTemplate.connectionFactory!!
                .connection
                .nativeConnection as RedisAsyncCommands<String, String>

            val args = CommandArgs<String, String>(StringCodec.UTF8)
                .add(CommandKeyword.CREATE)
                .add(streamKey)
                .add(consumerGroupName)
                .add("0")
                .add("MKSTREAM")

            native.dispatch(
                CommandType.XGROUP,
                StatusOutput(StringCodec.UTF8),
                args
            )

        } else {
            if (!isStreamConsumerGroupExist(streamKey, consumerGroupName)) {
                redisTemplate.opsForStream<String, String>()
                    .createGroup(streamKey, ReadOffset.from("0"), consumerGroupName)
            }
        }
    }

    private fun isStreamConsumerGroupExist(streamKey: String, consumerGroupName: String): Boolean {
        val groups = redisTemplate.opsForStream<String, String>().groups(streamKey)
        return groups.any { it.groupName() == consumerGroupName }
    }

    fun createStreamMessageListenerContainer(): StreamMessageListenerContainer<String, MapRecord<String, String, String>> {
        val options = StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder()
            .hashKeySerializer<String, String>(StringRedisSerializer())
            .hashValueSerializer<String, String>(StringRedisSerializer())
            .pollTimeout(Duration.ofSeconds(1))
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
