package backend.team.ahachul_backend.common.persistence

import backend.team.ahachul_backend.common.domain.entity.FileEntity
import org.springframework.stereotype.Component

@Component
class FilePersistence(
    private val repository: FileRepository
): FileReader, FileWriter {

    override fun save(entity: FileEntity): FileEntity {
        return repository.save(entity)
    }

    override fun delete(id: Long) {
        return repository.deleteById(id)
    }

    override fun findAllIdIn(fileIds: List<Long>): List<FileEntity> {
        return repository.findAllByIdIn(fileIds)
    }
}
