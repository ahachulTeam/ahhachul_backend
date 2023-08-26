package backend.team.ahachul_backend.common.domain.entity

import backend.team.ahachul_backend.common.entity.BaseEntity
import backend.team.ahachul_backend.common.model.RegionType
import jakarta.persistence.*

@Entity
class SubwayLineEntity (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subway_line_id")
    var id: Long = 0,

    var name: String,

    var phoneNumber: String = "",

    @Enumerated(EnumType.STRING)
    var regionType: RegionType,

    var subwayLineIdentity: Long = 0

): BaseEntity() {
}
