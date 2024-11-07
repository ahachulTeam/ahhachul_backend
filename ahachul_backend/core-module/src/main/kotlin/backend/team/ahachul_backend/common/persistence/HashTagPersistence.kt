package backend.team.ahachul_backend.common.persistence

import backend.team.ahachul_backend.common.domain.entity.HashTagEntity
import org.springframework.stereotype.Component

@Component
class HashTagPersistence(
    private val repository: HashTagRepository,
): HashTagReader, HashTagWriter {

    override fun save(entity: HashTagEntity): HashTagEntity {
        return repository.save(entity)
    }

    override fun find(name: String): HashTagEntity? {
        return repository.findByName(name)
    }
}