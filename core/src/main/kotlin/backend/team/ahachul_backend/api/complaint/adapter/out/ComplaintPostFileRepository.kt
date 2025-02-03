package backend.team.ahachul_backend.api.complaint.adapter.out

import backend.team.ahachul_backend.api.complaint.domain.entity.ComplaintPostFileEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ComplaintPostFileRepository: JpaRepository<ComplaintPostFileEntity, Long> {

    fun findTopByComplaintPostIdOrderById(complaintPostId: Long): ComplaintPostFileEntity?

    fun findAllByComplaintPostIdOrderById(complaintPostId: Long): List<ComplaintPostFileEntity>

    fun deleteByFileId(fileId: Long)
}