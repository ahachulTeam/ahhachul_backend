package backend.team.ahachul_backend.api.report.adpater.out

import backend.team.ahachul_backend.api.report.application.port.out.ReportWriter
import backend.team.ahachul_backend.api.report.domain.ReportEntity
import org.springframework.stereotype.Component

@Component
class ReportPersistence(
    private val repository: ReportRepository
): ReportWriter {

    override fun save(reportEntity: ReportEntity): ReportEntity {
        return repository.save(reportEntity)
    }
}