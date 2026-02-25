package backend.team.ahachul_backend.api.health

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ReadinessCheckController(
    private val jdbcTemplate: JdbcTemplate,
    private val redisTemplate: StringRedisTemplate,
) {

    @GetMapping("/health-check/readiness")
    fun readiness(): ResponseEntity<Map<String, Any>> {
        val checks = linkedMapOf<String, String>()
        var up = true

        try {
            jdbcTemplate.queryForObject("SELECT 1", Int::class.java)
            checks["database"] = "UP"
        } catch (exception: Exception) {
            checks["database"] = "DOWN"
            up = false
        }

        try {
            val connectionFactory = redisTemplate.connectionFactory
                ?: throw IllegalStateException("Redis connection factory is not configured")
            connectionFactory.connection.use { connection ->
                val ping = connection.ping()
                if (ping.equals("PONG", ignoreCase = true)) {
                    checks["redis"] = "UP"
                } else {
                    checks["redis"] = "DOWN"
                    up = false
                }
            }
        } catch (exception: Exception) {
            checks["redis"] = "DOWN"
            up = false
        }

        val status = if (up) "UP" else "DOWN"
        val response = mapOf(
            "status" to status,
            "checks" to checks,
        )

        return ResponseEntity.status(
            if (up) HttpStatus.OK else HttpStatus.SERVICE_UNAVAILABLE
        ).body(response)
    }
}
