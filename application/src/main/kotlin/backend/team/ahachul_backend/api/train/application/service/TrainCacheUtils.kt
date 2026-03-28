package backend.team.ahachul_backend.api.train.application.service

import backend.team.ahachul_backend.api.train.adapter.`in`.dto.GetTrainRealTimesDto
import backend.team.ahachul_backend.common.client.RedisClient
import backend.team.ahachul_backend.common.config.CircuitBreakerConfig.Companion.CUSTOM_CIRCUIT_BREAKER
import backend.team.ahachul_backend.common.logging.Logger
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class TrainCacheUtils(
    private val redisClient: RedisClient,
    private val objectMapper: ObjectMapper
) {

    private val logger = Logger(javaClass)

    private val localCache: Cache<String, List<GetTrainRealTimesDto.TrainRealTime>> = Caffeine.newBuilder()
        .expireAfterWrite(TRAIN_REAL_TIME_LOCAL_EXPIRE_SEC, TimeUnit.SECONDS)
        .build()

    fun setCache(
        subwayLineIdentity: Long, stationId: Long, value: List<GetTrainRealTimesDto.TrainRealTime>
    ) {
        val key = createKey(subwayLineIdentity, stationId)
        localCache.put(key, value)
        try {
            redisClient.set(key, value, TRAIN_REAL_TIME_EXPIRE_SEC, TimeUnit.SECONDS)
        } catch (e: Exception) {
            logger.warn("Redis 캐시 저장 실패, 로컬 캐시로 유지: key=$key")
        }
    }

    @CircuitBreaker(name = CUSTOM_CIRCUIT_BREAKER, fallbackMethod = "getCacheFromLocal")
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

    private fun getCacheFromLocal(
        subwayLineIdentity: Long, stationId: Long, e: Exception
    ): List<GetTrainRealTimesDto.TrainRealTime>? {
        logger.warn("Redis 장애로 로컬 캐시 조회: key=${createKey(subwayLineIdentity, stationId)}, cause=${e::class.simpleName}")
        return localCache.getIfPresent(createKey(subwayLineIdentity, stationId))
    }

    private fun createKey(subwayLineIdentity: Long, stationId: Long): String {
        return "${TRAIN_REAL_TIME_REDIS_PREFIX}${subwayLineIdentity}-$stationId"
    }

    companion object {
        const val TRAIN_REAL_TIME_REDIS_PREFIX = "TRAIN_TIME:"
        const val TRAIN_REAL_TIME_EXPIRE_SEC = 15L
        const val TRAIN_REAL_TIME_LOCAL_EXPIRE_SEC = 60L
    }
}
