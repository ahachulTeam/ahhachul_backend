package backend.team.ahachul_backend.api.story.adapter.web.out

import backend.team.ahachul_backend.api.story.application.port.out.StoryReader
import backend.team.ahachul_backend.api.story.application.port.out.StoryWriter
import backend.team.ahachul_backend.api.story.domain.entity.StoryEntity
import backend.team.ahachul_backend.api.story.domain.model.StoryStatusType
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class StoryPersistence(
    private val storyRepository: StoryRepository,
) : StoryReader, StoryWriter {

    override fun findById(id: Long): StoryEntity? {
        return storyRepository.findById(id).orElse(null)
    }

    override fun findByMemberIdAndStatus(
        memberId: Long,
        status: StoryStatusType,
        pageable: Pageable,
    ): List<StoryEntity> {
        return storyRepository.findByMemberIdAndStatusOrderByCreatedAtDesc(
            memberId = memberId,
            status = status,
            pageable = pageable,
        )
    }

    override fun save(entity: StoryEntity): StoryEntity {
        return storyRepository.save(entity)
    }
}
