package backend.team.ahachul_backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@EnableConfigurationProperties
@EnableCaching
@EnableAsync
class AhachulBackendApplication

fun main(args: Array<String>) {
    runApplication<AhachulBackendApplication>(*args)
}
