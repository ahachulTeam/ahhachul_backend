package backend.team.ahachul_backend.api.report.application.port.`in`

import backend.team.ahachul_backend.api.report.adpater.`in`.dto.CreateReportDto

interface ReportUseCase {

    fun save(targetId: Long): CreateReportDto.Response
}