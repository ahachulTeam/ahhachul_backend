package backend.team.ahachul_backend.api.member.domain.entity

import backend.team.ahachul_backend.api.common.domain.entity.StationEntity
import backend.team.ahachul_backend.api.member.domain.model.MemberStationWalkingSourceType
import backend.team.ahachul_backend.common.domain.entity.BaseEntity
import java.time.LocalDateTime
import jakarta.persistence.*

@Entity
class MemberStationEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_station_id")
    val id: Long = 0,

    var label: String?,

    var locationName: String? = null,

    var roadAddress: String? = null,

    var jibunAddress: String? = null,

    var latitude: Double? = null,

    var longitude: Double? = null,

    var walkingMinutes: Int? = null,

    @Enumerated(EnumType.STRING)
    var walkingSource: MemberStationWalkingSourceType? = null,

    var walkingUpdatedAt: LocalDateTime? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: MemberEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id")
    val station: StationEntity

): BaseEntity() {

    fun isEquals(stationName: String, label: String?): Boolean {
        return this.station.name == stationName && this.label == label
    }

    fun changeLocationMeta(
        locationName: String?,
        roadAddress: String?,
        jibunAddress: String?,
        latitude: Double?,
        longitude: Double?,
        walkingMinutes: Int?,
        walkingSource: MemberStationWalkingSourceType?,
    ) {
        this.locationName = locationName
        this.roadAddress = roadAddress
        this.jibunAddress = jibunAddress
        this.latitude = latitude
        this.longitude = longitude
        this.walkingMinutes = walkingMinutes
        this.walkingSource = walkingSource
        this.walkingUpdatedAt = if (walkingMinutes == null) null else LocalDateTime.now()
    }
}
