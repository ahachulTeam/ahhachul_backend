package backend.team.ahachul_backend.api.complaint.application.port.out

import backend.team.ahachul_backend.api.complaint.domain.entity.ComplaintMessageHistoryEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ComplaintMessageReader {

    fun findAll(pageable: Pageable): Page<ComplaintMessageHistoryEntity>
}