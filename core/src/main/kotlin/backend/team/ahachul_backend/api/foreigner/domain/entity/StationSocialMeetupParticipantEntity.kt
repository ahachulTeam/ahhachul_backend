package backend.team.ahachul_backend.api.foreigner.domain.entity

import backend.team.ahachul_backend.api.foreigner.domain.model.StationSocialParticipantStatusType
import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity
import backend.team.ahachul_backend.common.domain.entity.BaseEntity
import backend.team.ahachul_backend.common.domain.model.YNType
import jakarta.persistence.*

@Entity
@Table(
    name = "tb_station_social_meetup_participant",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_station_social_meetup_member",
            columnNames = ["station_social_meetup_id", "member_id"],
        ),
    ],
    indexes = [
        Index(name = "idx_station_social_meetup_participant_meetup_status", columnList = "station_social_meetup_id,status"),
    ],
)
class StationSocialMeetupParticipantEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "station_social_meetup_participant_id")
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_social_meetup_id")
    val meetup: StationSocialMeetupEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: MemberEntity,

    @Enumerated(EnumType.STRING)
    var status: StationSocialParticipantStatusType = StationSocialParticipantStatusType.REQUESTED,

    @Column(name = "introduction_message", length = 500)
    var introductionMessage: String?,

    @Column(name = "nationality_code")
    var nationalityCode: String?,

    @Enumerated(EnumType.STRING)
    @Column(name = "match_open_yn")
    var matchOpenYn: YNType = YNType.Y,
) : BaseEntity() {

    companion object {
        fun of(
            meetup: StationSocialMeetupEntity,
            member: MemberEntity,
            status: StationSocialParticipantStatusType,
            introductionMessage: String?,
            nationalityCode: String?,
            matchOpenYn: YNType = YNType.Y,
        ): StationSocialMeetupParticipantEntity {
            return StationSocialMeetupParticipantEntity(
                meetup = meetup,
                member = member,
                status = status,
                introductionMessage = introductionMessage,
                nationalityCode = nationalityCode,
                matchOpenYn = matchOpenYn,
            )
        }
    }

    fun isAccepted(): Boolean = status == StationSocialParticipantStatusType.ACCEPTED

    fun request(introductionMessage: String?, nationalityCode: String?) {
        status = StationSocialParticipantStatusType.REQUESTED
        this.introductionMessage = introductionMessage
        this.nationalityCode = nationalityCode
    }

    fun approve() {
        status = StationSocialParticipantStatusType.ACCEPTED
    }

    fun reject() {
        status = StationSocialParticipantStatusType.REJECTED
    }

    fun cancel() {
        status = StationSocialParticipantStatusType.CANCELED
    }
}
