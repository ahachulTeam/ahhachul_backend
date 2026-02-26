package backend.team.ahachul_backend.api.story.application.port.out

import backend.team.ahachul_backend.api.story.domain.entity.StoryEntity

interface StoryWriter {
    fun save(entity: StoryEntity): StoryEntity
}
