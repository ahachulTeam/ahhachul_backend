package backend.team.ahachul_backend.stream.consumer

import backend.team.ahachul_backend.common.client.RedisClient
import backend.team.ahachul_backend.common.logging.Logger
import org.springframework.beans.factory.InitializingBean
import org.springframework.data.redis.connection.stream.Consumer
import org.springframework.data.redis.connection.stream.MapRecord
import org.springframework.data.redis.connection.stream.ReadOffset
import org.springframework.data.redis.connection.stream.StreamOffset
import org.springframework.data.redis.stream.StreamListener
import org.springframework.data.redis.stream.StreamMessageListenerContainer
import org.springframework.data.redis.stream.Subscription
import java.time.Duration

abstract class AbstractRedisStreamConsumer(
    private val redisClient: RedisClient
) : StreamListener<String, MapRecord<String, String, String>>, InitializingBean {

    private lateinit var listenerContainer: StreamMessageListenerContainer<String, MapRecord<String, String, String>>
    private lateinit var subscription: Subscription

    protected abstract val streamKey: String
    protected abstract val consumerGroupName: String
    protected abstract val consumerName: String

    private val logger: Logger = Logger(javaClass)

    override fun onMessage(message: MapRecord<String, String, String>?) {
        val recordId = message?.id
        runCatching {
            message?.value?.let { handleMessage(it) }
        }.onSuccess {
            redisClient.ackStream(streamKey, consumerGroupName, recordId)
        }.onFailure {
            logger.error("Process failed. Keep pending. id=${message?.id?.value}", it)
        }
    }

    @Throws(Exception::class)
    override fun afterPropertiesSet() {
        redisClient.createStreamConsumerGroup(streamKey, consumerGroupName)

        listenerContainer = redisClient.createStreamMessageListenerContainer()

        subscription = listenerContainer.receive(
            Consumer.from(consumerGroupName, consumerName),
            StreamOffset.create(streamKey, ReadOffset.lastConsumed()),
            this
        )

        subscription.await(Duration.ofSeconds(2))
        listenerContainer.start()
    }

    /**
     * 실제 구체 클래스에서 데이터 처리 구현
     */
    protected abstract fun handleMessage(data: Map<String, String>)
}
