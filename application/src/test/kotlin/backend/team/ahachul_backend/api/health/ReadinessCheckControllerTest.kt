package backend.team.ahachul_backend.api.health

import backend.team.ahachul_backend.config.controller.CommonDocsTestConfig
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.redis.connection.RedisConnection
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(ReadinessCheckController::class)
class ReadinessCheckControllerTest: CommonDocsTestConfig() {

    @MockBean
    lateinit var jdbcTemplate: JdbcTemplate

    @MockBean
    lateinit var redisTemplate: StringRedisTemplate

    @MockBean
    lateinit var redisConnectionFactory: RedisConnectionFactory

    @MockBean
    lateinit var redisConnection: RedisConnection

    @Test
    fun readinessUp() {
        given(jdbcTemplate.queryForObject("SELECT 1", Int::class.java)).willReturn(1)
        given(redisTemplate.connectionFactory).willReturn(redisConnectionFactory)
        given(redisConnectionFactory.connection).willReturn(redisConnection)
        given(redisConnection.ping()).willReturn("PONG")

        mockMvc.perform(get("/health-check/readiness"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("UP"))
            .andExpect(jsonPath("$.checks.database").value("UP"))
            .andExpect(jsonPath("$.checks.redis").value("UP"))
    }

    @Test
    fun readinessDownWhenInfraUnavailable() {
        given(jdbcTemplate.queryForObject("SELECT 1", Int::class.java))
            .willThrow(RuntimeException("database-down"))
        given(redisTemplate.connectionFactory).willReturn(redisConnectionFactory)
        given(redisConnectionFactory.connection).willReturn(redisConnection)
        given(redisConnection.ping()).willThrow(RuntimeException("redis-down"))

        mockMvc.perform(get("/health-check/readiness"))
            .andExpect(status().isServiceUnavailable)
            .andExpect(jsonPath("$.status").value("DOWN"))
            .andExpect(jsonPath("$.checks.database").value("DOWN"))
            .andExpect(jsonPath("$.checks.redis").value("DOWN"))
    }
}
