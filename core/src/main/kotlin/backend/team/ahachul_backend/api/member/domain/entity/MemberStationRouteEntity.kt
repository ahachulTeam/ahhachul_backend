package backend.team.ahachul_backend.api.member.domain.entity

import backend.team.ahachul_backend.api.common.domain.entity.StationEntity
import backend.team.ahachul_backend.common.domain.entity.BaseEntity
import jakarta.persistence.*

@Entity
@Table(
    name = "tb_member_station_route",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_member_station_route_pair",
            columnNames = ["member_id", "source_station_id", "destination_station_id"],
        ),
    ],
)
class MemberStationRouteEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_station_route_id")
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: MemberEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_station_id")
    val sourceStation: StationEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_station_id")
    val destinationStation: StationEntity,

    @Column(name = "title")
    var title: String? = null,
) : BaseEntity()
