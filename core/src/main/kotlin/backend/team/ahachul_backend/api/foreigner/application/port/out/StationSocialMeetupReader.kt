package backend.team.ahachul_backend.api.foreigner.application.port.out

import backend.team.ahachul_backend.api.foreigner.domain.entity.StationSocialMeetupEntity
import backend.team.ahachul_backend.api.foreigner.domain.model.StationSocialMeetupStatusType
import java.time.LocalDateTime

interface StationSocialMeetupReader {

    fun getById(meetupId: Long): StationSocialMeetupEntity

    fun findByStationAndRange(
        stationId: Long,
        from: LocalDateTime,
        to: LocalDateTime,
        status: StationSocialMeetupStatusType? = null,
    ): List<StationSocialMeetupEntity>

    fun findUpcomingByStation(
        stationId: Long,
        from: LocalDateTime,
        limit: Int,
    ): List<StationSocialMeetupEntity>

    fun countByStationAndStatusAndMeetupAtAfter(
        stationId: Long,
        status: StationSocialMeetupStatusType,
        from: LocalDateTime,
    ): Long
}
