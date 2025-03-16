package backend.team.ahachul_backend.api.member.domain.entity

import backend.team.ahachul_backend.common.domain.entity.BaseEntity
import jakarta.persistence.*

@Entity
class FcmTokenEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    val id: Long = 0,

    @OneToOne
    @JoinColumn(name = "member_id")
    val member: MemberEntity,

    var token: String
) : BaseEntity() {

}
