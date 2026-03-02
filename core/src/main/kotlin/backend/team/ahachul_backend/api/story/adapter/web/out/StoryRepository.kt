package backend.team.ahachul_backend.api.story.adapter.web.out

import backend.team.ahachul_backend.api.story.domain.entity.StoryEntity
import backend.team.ahachul_backend.api.story.domain.model.StoryStatusType
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface StoryRepository : JpaRepository<StoryEntity, Long> {
    @EntityGraph(attributePaths = ["member", "station", "subwayLine"])
    fun findByMemberIdAndStatusOrderByCreatedAtDesc(
        memberId: Long,
        status: StoryStatusType,
        pageable: Pageable,
    ): List<StoryEntity>

    @EntityGraph(attributePaths = ["member", "station", "subwayLine"])
    @Query(
        """
        SELECT s
        FROM StoryEntity s
        WHERE s.status = :status
        AND (:stationId IS NULL OR s.station.id = :stationId)
        AND (:subwayLineId IS NULL OR s.subwayLine.id = :subwayLineId)
        ORDER BY s.createdAt DESC
        """,
    )
    fun findByStatusAndOptionalFiltersOrderByCreatedAtDesc(
        @Param("status") status: StoryStatusType,
        @Param("stationId") stationId: Long?,
        @Param("subwayLineId") subwayLineId: Long?,
        pageable: Pageable,
    ): List<StoryEntity>

    @EntityGraph(attributePaths = ["member", "station", "subwayLine"])
    override fun findById(id: Long): java.util.Optional<StoryEntity>
}
