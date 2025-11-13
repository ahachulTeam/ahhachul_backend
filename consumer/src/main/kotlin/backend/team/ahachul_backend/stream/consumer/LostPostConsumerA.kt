package backend.team.ahachul_backend.stream.consumer

import backend.team.ahachul_backend.common.client.RedisClient
import backend.team.ahachul_backend.stream.service.Lost112Service
import org.springframework.stereotype.Component


@Component
class LostPostConsumerA(
    redisClient: RedisClient,
    private val lostPostUtil: Lost112Service
) : AbstractRedisStreamConsumer(redisClient) {

    override val streamKey = "lostpost-stream"
    override val consumerGroupName = "ahachul"
    override val consumerName = "ahachul-consumer-1"

    override fun handleMessage(data: Map<String, String>) {
        lostPostUtil.convertAndSaveLostPost(data)
    }
}
