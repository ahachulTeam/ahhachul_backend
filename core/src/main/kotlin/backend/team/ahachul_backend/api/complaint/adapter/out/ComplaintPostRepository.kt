package backend.team.ahachul_backend.api.complaint.adapter.out

import backend.team.ahachul_backend.api.complaint.domain.entity.ComplaintPostEntity
import backend.team.ahachul_backend.api.complaint.domain.model.ComplaintPostType
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface ComplaintPostRepository: JpaRepository<ComplaintPostEntity, Long> {

    fun findByMemberIdAndStatusInOrderByCreatedAtDesc(
        memberId: Long,
        statuses: Collection<ComplaintPostType>,
        pageable: Pageable,
    ): List<ComplaintPostEntity>
}
