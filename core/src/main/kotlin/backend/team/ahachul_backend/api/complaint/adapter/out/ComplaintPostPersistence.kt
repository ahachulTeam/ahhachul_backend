package backend.team.ahachul_backend.api.complaint.adapter.out

import backend.team.ahachul_backend.api.complaint.application.command.out.GetSliceComplaintPostsCommand
import backend.team.ahachul_backend.api.complaint.application.port.out.ComplaintPostReader
import backend.team.ahachul_backend.api.complaint.application.port.out.ComplaintPostWriter
import backend.team.ahachul_backend.api.complaint.domain.entity.ComplaintPostEntity
import backend.team.ahachul_backend.common.exception.AdapterException
import backend.team.ahachul_backend.common.response.ResponseCode
import org.springframework.stereotype.Component

@Component
class ComplaintPostPersistence(
    private val repository: ComplaintPostRepository,
    private val customRepository: CustomComplaintPostRepository
): ComplaintPostReader, ComplaintPostWriter {

    override fun getComplaintPost(id: Long): ComplaintPostEntity {
        return repository.findById(id)
            .orElseThrow { throw AdapterException(ResponseCode.INVALID_DOMAIN) }
    }

    override fun getComplaintPosts(command: GetSliceComplaintPostsCommand): List<ComplaintPostEntity> {
        return customRepository.searchComplaintPosts(command)
    }

    override fun save(entity: ComplaintPostEntity): ComplaintPostEntity {
        return repository.save(entity)
    }
}