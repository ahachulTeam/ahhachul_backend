package backend.team.ahachul_backend.api.train.application.service

import backend.team.ahachul_backend.api.train.adapter.`in`.dto.GetCongestionDto
import backend.team.ahachul_backend.common.client.RedisClient
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class CongestionCacheUtils(
    private val redisClient: RedisClient,
    private val objectMapper: ObjectMapper
) {

    fun setCache(
        subwayLineId: Long,
        trainNo: String,
        value: GetCongestionDto.Response
    ) {
        val key = createKey(subwayLineId, trainNo)
        redisClient.set(
                key, value,
                TRAIN_CONGESTION_REDIS_EXPIRE_SEC,
                TimeUnit.SECONDS
        )
    }

    fun getCache(
        subwayLineId: Long,
        trainNo: String
    ): GetCongestionDto.Response? {
        val key = createKey(subwayLineId, trainNo)
        val cachedData = redisClient.get(key)

        return cachedData?.let {
            val typeRef = object : TypeReference<GetCongestionDto.Response>() {}
            objectMapper.readValue(it, typeRef)
        }
    }

    private fun createKey(subwayLineId: Long, trainNo: String): String {
        return "${TRAIN_CONGESTION_REDIS_PREFIX}${subwayLineId}-$trainNo"
    }

    companion object {
        const val TRAIN_CONGESTION_REDIS_PREFIX = "TRAIN_CONGESTION:"
        const val TRAIN_CONGESTION_REDIS_EXPIRE_SEC = 30L
    }
}
