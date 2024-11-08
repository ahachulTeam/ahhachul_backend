package backend.team.ahachul_backend.common.support

import backend.team.ahachul_backend.api.community.application.port.out.CommunityPostReader
import backend.team.ahachul_backend.common.client.RedisClient
import org.springframework.stereotype.Component

@Component
class ViewsSupport(
    private val redisClient: RedisClient,
    private val communityPostReader: CommunityPostReader
) {

    companion object {
        const val PREFIX = "views:"
    }

    fun increase(postId: Long): Int {
        val key = "$PREFIX$postId"

        val cachedViews = redisClient.get(key)?.toIntOrNull() ?: communityPostReader.getCommunityPost(postId).views

        val increasedViews = cachedViews + 1
        redisClient.set(key, increasedViews.toString())

        return increasedViews
    }

    fun get(postId: Long): Int {
        val key = "$PREFIX$postId"

        return redisClient.get(key)?.toInt() ?: run {
            val postViews = communityPostReader.getCommunityPost(postId).views
            redisClient.set(key, postViews.toString())
            postViews
        }
    }
}
