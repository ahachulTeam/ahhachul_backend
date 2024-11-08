package backend.team.ahachul_backend.config.controller

import backend.team.ahachul_backend.config.ContainerTest
import jakarta.transaction.Transactional
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@Transactional
@ExtendWith(ContainerTest::class)
class CommonServiceTestConfig {
}
