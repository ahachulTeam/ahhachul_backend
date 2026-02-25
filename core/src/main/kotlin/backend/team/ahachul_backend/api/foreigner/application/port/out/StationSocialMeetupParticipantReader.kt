package backend.team.ahachul_backend.api.foreigner.application.port.out

import backend.team.ahachul_backend.api.foreigner.domain.entity.StationSocialMeetupParticipantEntity
import backend.team.ahachul_backend.api.foreigner.domain.model.StationSocialParticipantStatusType

interface StationSocialMeetupParticipantReader {

    fun findByMeetupId(meetupId: Long): List<StationSocialMeetupParticipantEntity>

    fun findByMeetupIdAndMemberId(meetupId: Long, memberId: Long): StationSocialMeetupParticipantEntity?

    fun getParticipantById(participantId: Long): StationSocialMeetupParticipantEntity

    fun countByMeetupIdAndStatus(meetupId: Long, status: StationSocialParticipantStatusType): Long
}
