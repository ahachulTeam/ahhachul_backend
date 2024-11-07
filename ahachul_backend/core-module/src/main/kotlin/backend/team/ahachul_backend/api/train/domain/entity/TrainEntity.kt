package backend.team.ahachul_backend.api.train.domain.entity

import backend.team.ahachul_backend.common.domain.entity.SubwayLineEntity
import backend.team.ahachul_backend.common.domain.entity.BaseEntity
import jakarta.persistence.*

@Entity
class TrainEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "train_id")
    var id: Long = 0,

    val prefixTrainNo: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subway_line_id")
    var subwayLine: SubwayLineEntity,
): BaseEntity() {
}
