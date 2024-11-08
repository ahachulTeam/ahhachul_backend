package backend.team.ahachul_backend.api.report.adpater.out

import backend.team.ahachul_backend.api.report.domain.ReportEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ReportRepository: JpaRepository<ReportEntity, Long> {
}