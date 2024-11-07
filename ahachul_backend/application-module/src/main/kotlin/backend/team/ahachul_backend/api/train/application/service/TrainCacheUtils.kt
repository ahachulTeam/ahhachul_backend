package backend.team.ahachul_backend.api.train.application.service

import backend.team.ahachul_backend.api.train.adapter.`in`.dto.GetTrainRealTimesDto
import backend.team.ahachul_backend.common.client.RedisClient
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class TrainCacheUtils(
    private val redisClient: RedisClient,
    private val objectMapper: ObjectMapper
) {

    fun setCache(
        subwayLineIdentity: Long, stationId: Long, value: List<GetTrainRealTimesDto.TrainRealTime>
    ) {
        val key = createKey(subwayLineIdentity, stationId)
        redisClient.set(
            key, value,
            TRAIN_REAL_TIME_REDIS_EXPIRE_SEC,
            TimeUnit.SECONDS
        )
    }

    fun getCache(
        subwayLineIdentity: Long, stationId: Long
    ): List<GetTrainRealTimesDto.TrainRealTime>? {
        val key = createKey(subwayLineIdentity, stationId)
        val cachedData = redisClient.get(key)

        return cachedData?.let {
            val typeRef = object : TypeReference<List<GetTrainRealTimesDto.TrainRealTime>>() {}
            objectMapper.readValue(it, typeRef)
        }
    }

    private fun createKey(subwayLineIdentity: Long, stationId: Long): String {
        return "${TRAIN_REAL_TIME_REDIS_PREFIX}${subwayLineIdentity}-$stationId"
    }

    companion object {
        const val TRAIN_REAL_TIME_REDIS_PREFIX = "TRAIN_TIME:"
        const val TRAIN_REAL_TIME_REDIS_EXPIRE_SEC = 30L
    }
}
