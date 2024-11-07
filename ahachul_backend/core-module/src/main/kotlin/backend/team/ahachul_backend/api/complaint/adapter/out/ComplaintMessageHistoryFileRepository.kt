package backend.team.ahachul_backend.api.complaint.adapter.out

import backend.team.ahachul_backend.api.complaint.domain.entity.ComplaintMessageHistoryFileEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ComplaintMessageHistoryFileRepository: JpaRepository<ComplaintMessageHistoryFileEntity, Long> {

    fun findAllByComplaintMessageHistoryIdOrderById(complaintMessageHistoryId: Long): List<ComplaintMessageHistoryFileEntity>
}