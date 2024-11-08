package backend.team.ahachul_backend.admin.application.port.`in`

import backend.team.ahachul_backend.admin.adapter.`in`.dto.ActionReportDto
import backend.team.ahachul_backend.api.report.application.port.`in`.command.ActionReportCommand

interface AdminUseCase {
    fun actionOnReport(command: ActionReportCommand): ActionReportDto.Response
}
