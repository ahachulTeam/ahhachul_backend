package backend.team.ahachul_backend.api.foreigner.domain.entity

import backend.team.ahachul_backend.api.common.domain.entity.StationEntity
import backend.team.ahachul_backend.api.foreigner.domain.model.StationSocialMeetupStatusType
import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity
import backend.team.ahachul_backend.common.domain.entity.BaseEntity
import backend.team.ahachul_backend.common.domain.entity.SubwayLineEntity
import backend.team.ahachul_backend.common.domain.model.YNType
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "tb_station_social_meetup",
    indexes = [
        Index(name = "idx_station_social_meetup_station_status_at", columnList = "station_id,status,meetup_at"),
        Index(name = "idx_station_social_meetup_host", columnList = "host_member_id"),
    ],
)
class StationSocialMeetupEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "station_social_meetup_id")
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id")
    val station: StationEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subway_line_id")
    val subwayLine: SubwayLineEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_member_id")
    val hostMember: MemberEntity,

    var title: String,

    @Column(name = "description", length = 2000)
    var description: String,

    @Column(name = "meetup_at")
    var meetupAt: LocalDateTime,

    @Column(name = "max_participants")
    var maxParticipants: Int,

    @Column(name = "nationality_code")
    var nationalityCode: String?,

    @Enumerated(EnumType.STRING)
    @Column(name = "same_nationality_only_yn")
    var sameNationalityOnlyYn: YNType = YNType.N,

    @Enumerated(EnumType.STRING)
    var status: StationSocialMeetupStatusType = StationSocialMeetupStatusType.OPEN,
) : BaseEntity() {

    companion object {
        fun of(
            station: StationEntity,
            subwayLine: SubwayLineEntity,
            hostMember: MemberEntity,
            title: String,
            description: String,
            meetupAt: LocalDateTime,
            maxParticipants: Int,
            nationalityCode: String?,
            sameNationalityOnlyYn: YNType,
        ): StationSocialMeetupEntity {
            return StationSocialMeetupEntity(
                station = station,
                subwayLine = subwayLine,
                hostMember = hostMember,
                title = title,
                description = description,
                meetupAt = meetupAt,
                maxParticipants = maxParticipants,
                nationalityCode = nationalityCode,
                sameNationalityOnlyYn = sameNationalityOnlyYn,
            )
        }
    }

    fun isOpen(): Boolean = status == StationSocialMeetupStatusType.OPEN

    fun close() {
        status = StationSocialMeetupStatusType.CLOSED
    }

    fun cancel() {
        status = StationSocialMeetupStatusType.CANCELED
    }

    fun isHost(memberId: Long): Boolean = hostMember.id == memberId
}
