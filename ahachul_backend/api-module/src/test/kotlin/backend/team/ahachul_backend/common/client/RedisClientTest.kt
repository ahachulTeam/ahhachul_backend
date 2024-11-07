package backend.team.ahachul_backend.common.client

import backend.team.ahachul_backend.config.controller.CommonServiceTestConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class RedisClientTest(
    @Autowired val redisClient: RedisClient
): CommonServiceTestConfig() {

    private val test = "TEST"

    @Test
    @DisplayName("레디스 기본동작 테스트")
    fun 레디스_기본동작_테스트() {
        // when, then
        redisClient.set(test, test)
        assertThat(redisClient.get(test)).isEqualTo(test)
        redisClient.delete(test)
        assertThat(redisClient.get(test)).isNull()
    }

    @Test
    @DisplayName("레디스 객체_기본동작 테스트")
    fun 레디스_객체_기본동작_테스트() {
        val testMock = TestMock(
            a = "a",
            b = true,
            c = 1234
        )

        // when
        redisClient.set(test, testMock)
        val result = redisClient.get(test, TestMock::class.java)!!

        // then
        assertThat(result.a).isEqualTo("a")
        assertThat(result.b).isEqualTo(true)
        assertThat(result.c).isEqualTo(1234)
    }

    data class TestMock(
        val a: String,
        val b: Boolean,
        val c: Int
    )
}
