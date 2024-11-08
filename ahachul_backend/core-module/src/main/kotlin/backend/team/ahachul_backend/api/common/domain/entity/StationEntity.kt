package backend.team.ahachul_backend.api.common.domain.entity

import backend.team.ahachul_backend.common.domain.entity.BaseEntity
import jakarta.persistence.*


@Entity
class StationEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "station_id")
    var id: Long = 0,

    var name: String

): BaseEntity() {

}
