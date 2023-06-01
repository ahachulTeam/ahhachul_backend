package backend.team.ahachul_backend.api.report.adpater.`in`

import backend.team.ahachul_backend.api.report.adpater.`in`.dto.CreateReportDto
import backend.team.ahachul_backend.api.report.application.port.`in`.ReportUseCase
import backend.team.ahachul_backend.common.response.CommonResponse
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
class CommunityPostReportController(
    private val communityPostReportService: ReportUseCase
) {

    @PostMapping("/v1/report/community/{targetId}")
    fun saveReport(@PathVariable("targetId") targetId: Long,
                   @RequestParam("type") type: String): CommonResponse<CreateReportDto.Response> {
        return CommonResponse.success(communityPostReportService.saveReport(targetId, type))
    }
}