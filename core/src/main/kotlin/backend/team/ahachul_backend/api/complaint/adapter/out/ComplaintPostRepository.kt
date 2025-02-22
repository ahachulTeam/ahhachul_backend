package backend.team.ahachul_backend.api.complaint.adapter.out

import backend.team.ahachul_backend.api.complaint.domain.entity.ComplaintPostEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ComplaintPostRepository: JpaRepository<ComplaintPostEntity, Long> {
}