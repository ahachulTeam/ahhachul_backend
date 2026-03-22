package backend.team.ahachul_backend.api.train.application.service

import backend.team.ahachul_backend.api.train.adapter.`in`.dto.GetCongestionDto
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
class CongestionCacheUtils(
    private val redisClient: RedisClient,
    private val objectMapper: ObjectMapper
) {

    private val logger = Logger(javaClass)

    private val localCache: Cache<String, GetCongestionDto.Response> = Caffeine.newBuilder()
        .expireAfterWrite(TRAIN_CONGESTION_EXPIRE_SEC, TimeUnit.SECONDS)
        .build()

    fun setCache(
        subwayLineId: Long,
        trainNo: String,
        value: GetCongestionDto.Response
    ) {
        val key = createKey(subwayLineId, trainNo)
        localCache.put(key, value)
        try {
            redisClient.set(key, value, TRAIN_CONGESTION_EXPIRE_SEC, TimeUnit.SECONDS)
        } catch (e: Exception) {
            logger.warn("Redis 캐시 저장 실패, 로컬 캐시로 유지: key=$key")
        }
    }

    @CircuitBreaker(name = CUSTOM_CIRCUIT_BREAKER, fallbackMethod = "getCacheFromLocal")
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

    private fun getCacheFromLocal(
        subwayLineId: Long, trainNo: String, e: Exception
    ): GetCongestionDto.Response? {
        logger.warn("Redis 장애로 로컬 캐시 조회: key=${createKey(subwayLineId, trainNo)}, cause=${e::class.simpleName}")
        return localCache.getIfPresent(createKey(subwayLineId, trainNo))
    }

    fun getLocalCache(
        subwayLineId: Long,
        trainNo: String
    ): GetCongestionDto.Response? {
        return localCache.getIfPresent(createKey(subwayLineId, trainNo))
    }

    private fun createKey(subwayLineId: Long, trainNo: String): String {
        return "${TRAIN_CONGESTION_REDIS_PREFIX}${subwayLineId}-$trainNo"
    }

    companion object {
        const val TRAIN_CONGESTION_REDIS_PREFIX = "TRAIN_CONGESTION:"
        const val TRAIN_CONGESTION_EXPIRE_SEC = 20L
    }
}
