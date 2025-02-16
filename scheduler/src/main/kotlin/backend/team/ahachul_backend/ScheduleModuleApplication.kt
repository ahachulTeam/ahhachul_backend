package backend.team.ahachul_backend

import backend.team.ahachul_backend.common.properties.SocketServerProperties
import ch.qos.logback.classic.net.SimpleSocketServer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContext

@SpringBootApplication
@EnableConfigurationProperties
class ScheduleModuleApplication

fun main(args: Array<String>) {
    val context: ApplicationContext = runApplication<ScheduleModuleApplication>(*args)
    val properties = context.getBean(SocketServerProperties::class.java)
    SimpleSocketServer.main(arrayOf(properties.port, properties.configPath))
}
