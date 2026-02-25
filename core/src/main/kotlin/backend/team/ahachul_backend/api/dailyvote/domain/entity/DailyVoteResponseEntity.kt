package backend.team.ahachul_backend.api.dailyvote.domain.entity

import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity
import backend.team.ahachul_backend.common.domain.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "tb_daily_vote_response")
class DailyVoteResponseEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "daily_vote_response_id")
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_vote_poll_id")
    val poll: DailyVotePollEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: MemberEntity,

    var optionCode: String,
) : BaseEntity() {

    companion object {
        fun of(
            poll: DailyVotePollEntity,
            member: MemberEntity,
            optionCode: String,
        ): DailyVoteResponseEntity {
            return DailyVoteResponseEntity(
                poll = poll,
                member = member,
                optionCode = optionCode,
            )
        }
    }

    fun changeOption(optionCode: String) {
        this.optionCode = optionCode
    }
}
