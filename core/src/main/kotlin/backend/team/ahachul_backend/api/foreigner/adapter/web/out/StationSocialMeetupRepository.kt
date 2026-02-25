package backend.team.ahachul_backend.api.foreigner.adapter.web.out

import backend.team.ahachul_backend.api.foreigner.domain.entity.StationSocialMeetupEntity
import backend.team.ahachul_backend.api.foreigner.domain.model.StationSocialMeetupStatusType
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime
import java.util.Optional

interface StationSocialMeetupRepository : JpaRepository<StationSocialMeetupEntity, Long> {

    @EntityGraph(attributePaths = ["station", "subwayLine", "hostMember"])
    override fun findById(id: Long): Optional<StationSocialMeetupEntity>

    @EntityGraph(attributePaths = ["station", "subwayLine", "hostMember"])
    fun findByStationIdAndMeetupAtBetweenOrderByMeetupAtAsc(
        stationId: Long,
        from: LocalDateTime,
        to: LocalDateTime,
    ): List<StationSocialMeetupEntity>

    @EntityGraph(attributePaths = ["station", "subwayLine", "hostMember"])
    fun findByStationIdAndMeetupAtBetweenAndStatusOrderByMeetupAtAsc(
        stationId: Long,
        from: LocalDateTime,
        to: LocalDateTime,
        status: StationSocialMeetupStatusType,
    ): List<StationSocialMeetupEntity>

    @EntityGraph(attributePaths = ["station", "subwayLine", "hostMember"])
    fun findByStationIdAndMeetupAtGreaterThanEqualAndStatusOrderByMeetupAtAsc(
        stationId: Long,
        from: LocalDateTime,
        status: StationSocialMeetupStatusType,
        pageable: PageRequest,
    ): List<StationSocialMeetupEntity>

    fun countByStationIdAndStatusAndMeetupAtGreaterThanEqual(
        stationId: Long,
        status: StationSocialMeetupStatusType,
        from: LocalDateTime,
    ): Long
}
