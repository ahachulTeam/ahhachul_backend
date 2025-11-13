package backend.team.ahachul_backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContext

@SpringBootApplication
@EnableConfigurationProperties
class ConsumerModuleApplication

fun main(args: Array<String>) {
    val context: ApplicationContext = runApplication<ConsumerModuleApplication>(*args)
}
