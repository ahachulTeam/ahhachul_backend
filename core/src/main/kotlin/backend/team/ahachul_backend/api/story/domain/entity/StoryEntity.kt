package backend.team.ahachul_backend.api.story.domain.entity

import backend.team.ahachul_backend.api.common.domain.entity.StationEntity
import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity
import backend.team.ahachul_backend.api.story.domain.model.StoryStatusType
import backend.team.ahachul_backend.common.domain.entity.BaseEntity
import backend.team.ahachul_backend.common.domain.entity.SubwayLineEntity
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

@Entity
@Table(name = "tb_member_story")
class StoryEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_story_id")
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: MemberEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id")
    var station: StationEntity? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subway_line_id")
    var subwayLine: SubwayLineEntity? = null,

    @Column(name = "image_url")
    var imageUrl: String,

    @Column(name = "caption")
    var caption: String?,

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    var status: StoryStatusType = StoryStatusType.CREATED,
) : BaseEntity() {

    companion object {
        fun of(
            member: MemberEntity,
            imageUrl: String,
            caption: String?,
            station: StationEntity? = null,
            subwayLine: SubwayLineEntity? = null,
        ): StoryEntity {
            return StoryEntity(
                member = member,
                station = station,
                subwayLine = subwayLine,
                imageUrl = imageUrl,
                caption = caption,
                status = StoryStatusType.CREATED,
            )
        }
    }

    fun delete() {
        status = StoryStatusType.DELETED
    }
}
