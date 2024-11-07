package backend.team.ahachul_backend.api.complaint.adapter.out

import backend.team.ahachul_backend.api.complaint.application.port.out.ComplaintMessageFileReader
import backend.team.ahachul_backend.api.complaint.application.port.out.ComplaintMessageFileWriter
import backend.team.ahachul_backend.api.complaint.domain.entity.ComplaintMessageHistoryFileEntity
import org.springframework.stereotype.Component

@Component
class ComplaintMessageMessageFilePersistence(
    private val repository: ComplaintMessageHistoryFileRepository,
): ComplaintMessageFileWriter, ComplaintMessageFileReader {

    override fun save(entity: ComplaintMessageHistoryFileEntity): ComplaintMessageHistoryFileEntity {
        return repository.save(entity)
    }

    override fun findAllByComplaintId(complaintId: Long): List<ComplaintMessageHistoryFileEntity> {
        return repository.findAllByComplaintMessageHistoryIdOrderById(complaintId)
    }
}