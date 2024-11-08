package backend.team.ahachul_backend.api.complaint.adapter.out

import backend.team.ahachul_backend.api.complaint.application.port.out.ComplaintMessageReader
import backend.team.ahachul_backend.api.complaint.application.port.out.ComplaintMessageWriter
import backend.team.ahachul_backend.api.complaint.domain.entity.ComplaintMessageHistoryEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class ComplaintMessageMessagePersistence(
    private val repository: ComplaintMessageHistoryRepository,
): ComplaintMessageWriter, ComplaintMessageReader {

    override fun save(entity: ComplaintMessageHistoryEntity): ComplaintMessageHistoryEntity {
        return repository.save(entity)
    }

    override fun findAll(pageable: Pageable): Page<ComplaintMessageHistoryEntity> {
        return repository.findAll(pageable)
    }
}