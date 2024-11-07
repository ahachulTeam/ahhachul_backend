package backend.team.ahachul_backend.api.complaint.adapter.out

import backend.team.ahachul_backend.api.complaint.domain.entity.ComplaintMessageHistoryEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ComplaintMessageHistoryRepository: JpaRepository<ComplaintMessageHistoryEntity, Long> {
}