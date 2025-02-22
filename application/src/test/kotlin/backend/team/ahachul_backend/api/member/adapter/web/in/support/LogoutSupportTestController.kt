package backend.team.ahachul_backend.api.member.adapter.web.`in`.support

import backend.team.ahachul_backend.common.annotation.Authentication
import backend.team.ahachul_backend.common.response.CommonResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class LogoutSupportTestController {

    @Authentication
    @GetMapping("/test/support/authenticated")
    fun authenticated(): CommonResponse<*> {
        return CommonResponse.success()
    }
}