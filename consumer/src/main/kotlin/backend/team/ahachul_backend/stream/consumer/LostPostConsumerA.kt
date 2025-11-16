package backend.team.ahachul_backend.stream.consumer

import backend.team.ahachul_backend.common.client.RedisClient
import backend.team.ahachul_backend.stream.service.Lost112Service
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component


@Component
class LostPostConsumerA(
    redisClient: RedisClient,
    private val lostPostUtil: Lost112Service
) : AbstractRedisStreamConsumer(redisClient) {

    @Value("\${stream.key}")
    override lateinit var streamKey: String

    @Value("\${stream.consumer-group-name}")
    override lateinit var consumerName: String

    @Value("\${stream.consumer-name}")
    override lateinit var consumerGroupName: String

    override fun handleMessage(data: Map<String, String>) {
        lostPostUtil.convertAndSaveLostPost(data)
    }
}
