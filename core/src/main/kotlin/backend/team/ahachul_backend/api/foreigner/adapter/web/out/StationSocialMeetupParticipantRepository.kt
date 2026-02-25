package backend.team.ahachul_backend.api.foreigner.adapter.web.out

import backend.team.ahachul_backend.api.foreigner.domain.entity.StationSocialMeetupParticipantEntity
import backend.team.ahachul_backend.api.foreigner.domain.model.StationSocialParticipantStatusType
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface StationSocialMeetupParticipantRepository : JpaRepository<StationSocialMeetupParticipantEntity, Long> {

    @EntityGraph(attributePaths = ["member", "meetup", "meetup.hostMember"])
    fun findByMeetupIdOrderByCreatedAtAsc(meetupId: Long): List<StationSocialMeetupParticipantEntity>

    @EntityGraph(attributePaths = ["member", "meetup", "meetup.hostMember"])
    fun findByMeetupIdAndMemberId(meetupId: Long, memberId: Long): StationSocialMeetupParticipantEntity?

    @EntityGraph(attributePaths = ["member", "meetup", "meetup.hostMember"])
    override fun findById(id: Long): Optional<StationSocialMeetupParticipantEntity>

    fun countByMeetupIdAndStatus(meetupId: Long, status: StationSocialParticipantStatusType): Long
}
