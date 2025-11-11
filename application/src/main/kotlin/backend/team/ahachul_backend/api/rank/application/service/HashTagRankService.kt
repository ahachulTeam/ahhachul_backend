package backend.team.ahachul_backend.api.rank.application.service

import backend.team.ahachul_backend.api.rank.adapter.web.`in`.dto.GetHashTagRankDto
import backend.team.ahachul_backend.common.client.RedisClient
import backend.team.ahachul_backend.common.constant.CommonConstant.Companion.HASHTAG_REDIS_KEY
import backend.team.ahachul_backend.common.logging.Logger
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service


@Service
class HashTagRankService(
    private val redisClient: RedisClient,
){

    val logger = Logger(javaClass)

    fun getRank(): GetHashTagRankDto.Response {
        val mapper = ObjectMapper()

        if (redisClient.hasKey(HASHTAG_REDIS_KEY)) {
            val typeRef = object : TypeReference<List<String>>() {}
            val cachedRank = redisClient.get(HASHTAG_REDIS_KEY)
            return GetHashTagRankDto.Response(mapper.readValue(cachedRank, typeRef))
        }

        return GetHashTagRankDto.Response(emptyList())
    }
}
