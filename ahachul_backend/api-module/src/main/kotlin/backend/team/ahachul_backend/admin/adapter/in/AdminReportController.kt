package backend.team.ahachul_backend.admin.adapter.`in`

import backend.team.ahachul_backend.admin.adapter.`in`.dto.ActionReportDto
import backend.team.ahachul_backend.admin.application.port.`in`.AdminUseCase
import backend.team.ahachul_backend.common.response.CommonResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AdminReportController(
    private val adminService: AdminUseCase
) {

    @Deprecated("Admin function is not supported")
    @PostMapping("admin/v1/reports/block")
    fun actionForReport(@RequestBody request: ActionReportDto.Request): CommonResponse<ActionReportDto.Response> {
        return CommonResponse.success(adminService.actionOnReport(request.toCommand()))
    }
}