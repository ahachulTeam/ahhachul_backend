package backend.team.ahachul_backend.api.rank.application.service

import backend.team.ahachul_backend.api.rank.adapter.web.`in`.dto.GetHashTagRankDto
import backend.team.ahachul_backend.common.client.RedisClient
import backend.team.ahachul_backend.common.config.CircuitBreakerConfig.Companion.CUSTOM_CIRCUIT_BREAKER
import backend.team.ahachul_backend.common.constant.CommonConstant.Companion.HASHTAG_REDIS_KEY
import backend.team.ahachul_backend.common.exception.CommonException
import backend.team.ahachul_backend.common.logging.Logger
import backend.team.ahachul_backend.common.response.ResponseCode
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import org.springframework.data.redis.RedisConnectionFailureException
import org.springframework.stereotype.Service


@Service
class HashTagRankService(
    private val redisClient: RedisClient,
){

    val logger = Logger(javaClass)

    @CircuitBreaker(name = CUSTOM_CIRCUIT_BREAKER, fallbackMethod = "fallbackOnRedisCacheGet")
    fun getRank(): GetHashTagRankDto.Response {
        val mapper = ObjectMapper()

        if (redisClient.hasKey(HASHTAG_REDIS_KEY)) {
            val typeRef = object : TypeReference<List<String>>() {}
            val cachedRank = redisClient.get(HASHTAG_REDIS_KEY)
            return GetHashTagRankDto.Response(mapper.readValue(cachedRank, typeRef))
        }

        return GetHashTagRankDto.Response(emptyList())
    }

    fun fallbackOnRedisCacheGet(e: RedisConnectionFailureException): GetHashTagRankDto.Response {
        logger.error("circuit breaker opened for redis hashtag cache")
        throw CommonException(ResponseCode.FAILED_TO_CONNECT_TO_REDIS, e)
    }

//    fun fallbackOnRedisCacheGet(e : CallNotPermittedException): GetHashTagRankDto.Response {
//        logger.error("circuit breaker opened for redis hashtag cache")
//        val rankList = rankHashTagJob.readHashTagLogFile(CommonConstant.HASHTAG_FILE_URL)
//        return GetHashTagRankDto.Response(rankList)
//    }
}
