package backend.team.ahachul_backend.api.story.adapter.web.out

import backend.team.ahachul_backend.api.story.domain.entity.StoryEntity
import backend.team.ahachul_backend.api.story.domain.model.StoryStatusType
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface StoryRepository : JpaRepository<StoryEntity, Long> {
    @EntityGraph(attributePaths = ["member", "station", "subwayLine"])
    fun findByMemberIdAndStatusOrderByCreatedAtDesc(
        memberId: Long,
        status: StoryStatusType,
        pageable: Pageable,
    ): List<StoryEntity>

    @EntityGraph(attributePaths = ["member", "station", "subwayLine"])
    override fun findById(id: Long): java.util.Optional<StoryEntity>
}
