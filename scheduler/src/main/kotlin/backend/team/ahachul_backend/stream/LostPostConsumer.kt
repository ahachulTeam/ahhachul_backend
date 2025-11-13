package backend.team.ahachul_backend.stream

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
import org.springframework.stereotype.Component
import java.time.Duration


@Component
class LostPostConsumer(
    private val redisClient: RedisClient,
    private val lostPostUtil: Lost112Service
): StreamListener<String, MapRecord<String, String, String>>, InitializingBean {

    private val logger: Logger = Logger(javaClass)

    private lateinit var listenerContainer: StreamMessageListenerContainer<String, MapRecord<String, String, String>>
    private lateinit var subscription: Subscription

    private lateinit var streamKey: String
    private lateinit var consumerGroupName: String
    private lateinit var consumerName: String

    override fun onMessage(message: MapRecord<String, String, String>?) {
        val recordId = message?.id

        runCatching {
            message?.value?.let {
                lostPostUtil.convertAndSaveLostPost(it)
            }
        }.onSuccess {
            redisClient.ackStream(streamKey, consumerGroupName, recordId)
        }.onFailure {
            logger.error("Process failed. Keep pending. id=${message?.id?.value}", it)
        }
    }

    @Throws(Exception::class)
    override fun afterPropertiesSet() {
        streamKey = "lostpost-stream"
        consumerGroupName = "ahachul"
        consumerName = "ahachul-server"

        // Consumer Group 설정
        this.redisClient.createStreamConsumerGroup(streamKey, consumerGroupName)

        // StreamMessageListenerContainer 설정
        this.listenerContainer = this.redisClient.createStreamMessageListenerContainer()

        // Subscription 설정
        this.subscription = this.listenerContainer.receive(
            Consumer.from(consumerGroupName, consumerName),
            StreamOffset.create(streamKey, ReadOffset.lastConsumed()),
            this
        )

        this.subscription.await(Duration.ofSeconds(2))
        this.listenerContainer.start()
    }
}
