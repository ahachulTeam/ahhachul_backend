package backend.team.ahachul_backend.api.complaint.adapter.out

import backend.team.ahachul_backend.api.complaint.application.port.out.ComplaintPostFileReader
import backend.team.ahachul_backend.api.complaint.application.port.out.ComplaintPostFileWriter
import backend.team.ahachul_backend.api.complaint.domain.entity.ComplaintPostFileEntity
import org.springframework.stereotype.Component

@Component
class ComplaintPostFilePersistence(
    private val repository: ComplaintPostFileRepository
): ComplaintPostFileReader, ComplaintPostFileWriter {

    override fun findByPostId(postId: Long): ComplaintPostFileEntity? {
        return repository.findTopByComplaintPostIdOrderById(postId)
    }

    override fun findAllByPostId(postId: Long): List<ComplaintPostFileEntity> {
        return repository.findAllByComplaintPostIdOrderById(postId)
    }

    override fun save(entity: ComplaintPostFileEntity): ComplaintPostFileEntity {
        return repository.save(entity)
    }

    override fun deleteByFileId(fileId: Long) {
        return repository.deleteByFileId(fileId)
    }
}