package backend.team.ahachul_backend.config

import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName

class ContainerTest: BeforeAllCallback {
    companion object {
        private const val REDIS_IMAGE = "redis:7.2-rc1-alpine"
        private const val REDIS_PORT = 6379
    }

    private lateinit var redis: GenericContainer<Nothing>

    override fun beforeAll(context: ExtensionContext?) {
        redis = GenericContainer<Nothing>(DockerImageName.parse(REDIS_IMAGE))
            .withExposedPorts(REDIS_PORT)
        redis.start()
        System.setProperty("spring.data.redis.host", redis.host)
        System.setProperty("spring.data.redis.port", redis.getMappedPort(REDIS_PORT).toString())
    }
}
