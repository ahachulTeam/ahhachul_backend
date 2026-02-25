package backend.team.ahachul_backend.api.dailyvote.domain.entity

import backend.team.ahachul_backend.api.common.domain.entity.StationEntity
import backend.team.ahachul_backend.api.dailyvote.domain.model.DailyVoteContextType
import backend.team.ahachul_backend.api.dailyvote.domain.model.DailyVoteKindType
import backend.team.ahachul_backend.api.dailyvote.domain.model.DailyVotePollStatusType
import backend.team.ahachul_backend.api.dailyvote.domain.model.DailyVoteSlotType
import backend.team.ahachul_backend.common.domain.entity.BaseEntity
import backend.team.ahachul_backend.common.domain.entity.SubwayLineEntity
import backend.team.ahachul_backend.common.domain.model.YNType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "tb_daily_vote_poll")
class DailyVotePollEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "daily_vote_poll_id")
    val id: Long = 0,

    var pollDate: LocalDate,

    @Enumerated(EnumType.STRING)
    var pollSlot: DailyVoteSlotType,

    @Enumerated(EnumType.STRING)
    var pollContext: DailyVoteContextType,

    @Enumerated(EnumType.STRING)
    var pollKind: DailyVoteKindType,

    @Enumerated(EnumType.STRING)
    var status: DailyVotePollStatusType,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id")
    var station: StationEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subway_line_id")
    var subwayLine: SubwayLineEntity,

    var question: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "primary_yn")
    var primaryYn: YNType,
) : BaseEntity() {

    companion object {
        fun of(
            pollDate: LocalDate,
            pollSlot: DailyVoteSlotType,
            pollContext: DailyVoteContextType,
            pollKind: DailyVoteKindType,
            station: StationEntity,
            subwayLine: SubwayLineEntity,
            question: String,
            isPrimary: Boolean,
        ): DailyVotePollEntity {
            return DailyVotePollEntity(
                pollDate = pollDate,
                pollSlot = pollSlot,
                pollContext = pollContext,
                pollKind = pollKind,
                status = DailyVotePollStatusType.OPEN,
                station = station,
                subwayLine = subwayLine,
                question = question,
                primaryYn = if (isPrimary) YNType.Y else YNType.N,
            )
        }
    }
}
