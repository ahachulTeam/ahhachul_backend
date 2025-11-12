package backend.team.ahachul_backend.stream

import backend.team.ahachul_backend.api.lost.application.port.out.LostPostWriter
import backend.team.ahachul_backend.api.lost.domain.entity.CategoryEntity
import backend.team.ahachul_backend.api.lost.domain.entity.LostPostEntity
import backend.team.ahachul_backend.api.lost.domain.model.Lost112Data
import backend.team.ahachul_backend.common.client.RedisClient
import backend.team.ahachul_backend.common.domain.entity.SubwayLineEntity
import backend.team.ahachul_backend.common.logging.Logger
import backend.team.ahachul_backend.common.storage.CategoryStorage
import backend.team.ahachul_backend.common.storage.SubwayLineStorage
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.InitializingBean
import org.springframework.data.redis.connection.stream.Consumer
import org.springframework.data.redis.connection.stream.ObjectRecord
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
    private val objectMapper: ObjectMapper,
    private val lostPostWriter: LostPostWriter,
    private val subwayLineStorage: SubwayLineStorage,
    private val categoryStorage: CategoryStorage
): StreamListener<String, ObjectRecord<String, String>>, InitializingBean {  // stream key, stream value type

    private val logger: Logger = Logger(javaClass)

    private lateinit var listenerContainer: StreamMessageListenerContainer<String, ObjectRecord<String, String>>
    private lateinit var subscription: Subscription

    private lateinit var streamKey: String
    private lateinit var consumerGroupName: String
    private lateinit var consumerName: String

    override fun onMessage(message: ObjectRecord<String, String>?) {
        val recordId = message?.id?.value

        runCatching {
            message?.value?.let {
                val lost112Data = objectMapper.readValue(it, Lost112Data::class.java)
                val subwayLine = getSubwayLineEntity(lost112Data.receiptPlace)
                val category = getCategory(lost112Data.categoryName)
                val lostPost = LostPostEntity.ofLost112(lost112Data, subwayLine, category, lost112Data.imageUrl)
                lostPostWriter.save(lostPost)
            }
        }.onSuccess {
            redisClient.ackStream(streamKey, consumerGroupName, recordId)
        }.onFailure {
            logger.error("Process failed. Keep pending. id=${message?.id?.value}", it)
            throw it
        }
    }

    private fun getSubwayLineEntity(receivedPlace: String): SubwayLineEntity? {
        val subwayLineName = subwayLineStorage.extractSubWayLine(receivedPlace)
        return subwayLineStorage.getSubwayLineEntityByName(subwayLineName)
    }

    private fun getCategory(categoryName: String): CategoryEntity? {
        val primaryCategoryName = categoryStorage.extractPrimaryCategory(categoryName)
        return categoryStorage.getCategoryByName(primaryCategoryName)
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
