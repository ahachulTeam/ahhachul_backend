package backend.team.ahachul_backend.api.health

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthCheckController {

    @GetMapping("/health-check")
    fun healthCheck(): String {
        return "ok"
    }
}
