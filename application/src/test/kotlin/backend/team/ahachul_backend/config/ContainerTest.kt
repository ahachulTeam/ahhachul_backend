package backend.team.ahachul_backend.config

import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName

class ContainerTest: BeforeAllCallback {
    companion object {
        private const val REDIS_IMAGE = "redis:7.2-rc1-alpine"
        private const val REDIS_PORT = 6379
        @Volatile
        private var redis: GenericContainer<Nothing>? = null
        private val lock = Any()

        private fun getOrStartRedisContainer(): GenericContainer<Nothing> {
            redis?.let { return it }
            synchronized(lock) {
                redis?.let { return it }
                val container = GenericContainer<Nothing>(DockerImageName.parse(REDIS_IMAGE))
                container.withExposedPorts(REDIS_PORT)
                container.start()
                redis = container
                return container
            }
        }
    }

    override fun beforeAll(context: ExtensionContext?) {
        val redisContainer = getOrStartRedisContainer()
        System.setProperty("spring.data.redis.host", redisContainer.host)
        System.setProperty("spring.data.redis.port", redisContainer.getMappedPort(REDIS_PORT).toString())
    }
}
