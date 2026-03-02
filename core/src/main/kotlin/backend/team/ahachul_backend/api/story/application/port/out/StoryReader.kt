package backend.team.ahachul_backend.api.story.application.port.out

import backend.team.ahachul_backend.api.story.domain.entity.StoryEntity
import backend.team.ahachul_backend.api.story.domain.model.StoryStatusType
import backend.team.ahachul_backend.common.exception.AdapterException
import backend.team.ahachul_backend.common.response.ResponseCode
import org.springframework.data.domain.Pageable

interface StoryReader {
    fun findById(id: Long): StoryEntity?

    fun findByMemberIdAndStatus(memberId: Long, status: StoryStatusType, pageable: Pageable): List<StoryEntity>

    fun findPublicStories(
        status: StoryStatusType,
        pageable: Pageable,
        stationId: Long? = null,
        subwayLineId: Long? = null,
    ): List<StoryEntity>

    fun getById(id: Long): StoryEntity {
        return findById(id) ?: throw AdapterException(ResponseCode.STORY_NOT_FOUND)
    }
}
