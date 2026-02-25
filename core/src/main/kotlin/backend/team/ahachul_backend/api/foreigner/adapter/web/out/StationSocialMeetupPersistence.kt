package backend.team.ahachul_backend.api.foreigner.adapter.web.out

import backend.team.ahachul_backend.api.foreigner.application.port.out.StationSocialMeetupParticipantReader
import backend.team.ahachul_backend.api.foreigner.application.port.out.StationSocialMeetupParticipantWriter
import backend.team.ahachul_backend.api.foreigner.application.port.out.StationSocialMeetupReader
import backend.team.ahachul_backend.api.foreigner.application.port.out.StationSocialMeetupWriter
import backend.team.ahachul_backend.api.foreigner.domain.entity.StationSocialMeetupEntity
import backend.team.ahachul_backend.api.foreigner.domain.entity.StationSocialMeetupParticipantEntity
import backend.team.ahachul_backend.api.foreigner.domain.model.StationSocialMeetupStatusType
import backend.team.ahachul_backend.api.foreigner.domain.model.StationSocialParticipantStatusType
import backend.team.ahachul_backend.common.exception.AdapterException
import backend.team.ahachul_backend.common.response.ResponseCode
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class StationSocialMeetupPersistence(
    private val meetupRepository: StationSocialMeetupRepository,
    private val participantRepository: StationSocialMeetupParticipantRepository,
) : StationSocialMeetupReader,
    StationSocialMeetupWriter,
    StationSocialMeetupParticipantReader,
    StationSocialMeetupParticipantWriter {

    override fun getById(meetupId: Long): StationSocialMeetupEntity {
        return meetupRepository.findById(meetupId).orElseThrow {
            AdapterException(ResponseCode.STATION_SOCIAL_MEETUP_NOT_FOUND)
        }
    }

    override fun findByStationAndRange(
        stationId: Long,
        from: LocalDateTime,
        to: LocalDateTime,
        status: StationSocialMeetupStatusType?,
    ): List<StationSocialMeetupEntity> {
        return if (status == null) {
            meetupRepository.findByStationIdAndMeetupAtBetweenOrderByMeetupAtAsc(stationId, from, to)
        } else {
            meetupRepository.findByStationIdAndMeetupAtBetweenAndStatusOrderByMeetupAtAsc(stationId, from, to, status)
        }
    }

    override fun findUpcomingByStation(
        stationId: Long,
        from: LocalDateTime,
        limit: Int,
    ): List<StationSocialMeetupEntity> {
        return meetupRepository.findByStationIdAndMeetupAtGreaterThanEqualAndStatusOrderByMeetupAtAsc(
            stationId = stationId,
            from = from,
            status = StationSocialMeetupStatusType.OPEN,
            pageable = PageRequest.of(0, limit.coerceIn(1, 100)),
        )
    }

    override fun countByStationAndStatusAndMeetupAtAfter(
        stationId: Long,
        status: StationSocialMeetupStatusType,
        from: LocalDateTime,
    ): Long {
        return meetupRepository.countByStationIdAndStatusAndMeetupAtGreaterThanEqual(stationId, status, from)
    }

    override fun save(meetup: StationSocialMeetupEntity): StationSocialMeetupEntity {
        return meetupRepository.save(meetup)
    }

    override fun findByMeetupId(meetupId: Long): List<StationSocialMeetupParticipantEntity> {
        return participantRepository.findByMeetupIdOrderByCreatedAtAsc(meetupId)
    }

    override fun findByMeetupIdAndMemberId(
        meetupId: Long,
        memberId: Long,
    ): StationSocialMeetupParticipantEntity? {
        return participantRepository.findByMeetupIdAndMemberId(meetupId, memberId)
    }

    override fun getParticipantById(participantId: Long): StationSocialMeetupParticipantEntity {
        return participantRepository.findById(participantId).orElseThrow {
            AdapterException(ResponseCode.STATION_SOCIAL_PARTICIPANT_NOT_FOUND)
        }
    }

    override fun countByMeetupIdAndStatus(meetupId: Long, status: StationSocialParticipantStatusType): Long {
        return participantRepository.countByMeetupIdAndStatus(meetupId, status)
    }

    override fun save(participant: StationSocialMeetupParticipantEntity): StationSocialMeetupParticipantEntity {
        return participantRepository.save(participant)
    }
}
