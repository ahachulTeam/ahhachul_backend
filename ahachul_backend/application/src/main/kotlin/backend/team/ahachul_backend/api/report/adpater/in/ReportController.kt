package backend.team.ahachul_backend.api.report.adpater.`in`

import backend.team.ahachul_backend.api.report.adpater.`in`.dto.CreateReportDto
import backend.team.ahachul_backend.api.report.application.port.`in`.ReportUseCase
import backend.team.ahachul_backend.common.annotation.Authentication
import backend.team.ahachul_backend.common.response.CommonResponse
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class ReportController(
    private val communityPostReportService: ReportUseCase,
    private val lostPostReportService: ReportUseCase

) {

    @Authentication
    @PostMapping("/v1/reports/community-post/{postId}")
    fun saveReportCommunityPost(@PathVariable("postId") postId: Long): CommonResponse<CreateReportDto.Response> {
        return CommonResponse.success(communityPostReportService.save(postId))
    }


    @Authentication
    @PostMapping("/v1/reports/lost-post/{postId}")
    fun saveReportLostPost(@PathVariable("postId") postId: Long): CommonResponse<CreateReportDto.Response> {
        return CommonResponse.success(lostPostReportService.save(postId))
    }
}