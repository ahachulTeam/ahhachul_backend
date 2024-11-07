package backend.team.ahachul_backend.common.config

import backend.team.ahachul_backend.common.properties.RedisProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig(
    private val redisProperties: RedisProperties,
) {

    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory =
        LettuceConnectionFactory(RedisStandaloneConfiguration(
            redisProperties.host,
            redisProperties.port
        ))

    @Bean
    fun stringRedisTemplate(): StringRedisTemplate {
        val stringRedisTemplate = StringRedisTemplate()
        stringRedisTemplate.setConnectionFactory(redisConnectionFactory())
        return stringRedisTemplate
    }

    @Bean
    fun redisCacheManager(redisConnectionFactory: RedisConnectionFactory): RedisCacheManager {
        val configuration = RedisCacheConfiguration.defaultCacheConfig()
            .disableCachingNullValues()
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair
                    .fromSerializer(StringRedisSerializer()))
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair
                    .fromSerializer(GenericJackson2JsonRedisSerializer())
            )

        return RedisCacheManager.RedisCacheManagerBuilder
            .fromConnectionFactory(redisConnectionFactory)
            .cacheDefaults(configuration)
            .build()
    }
}
