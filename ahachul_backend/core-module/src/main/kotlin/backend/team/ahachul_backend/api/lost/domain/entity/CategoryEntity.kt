package backend.team.ahachul_backend.api.lost.domain.entity

import backend.team.ahachul_backend.common.domain.entity.BaseEntity
import jakarta.persistence.*

@Entity
class CategoryEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    var id: Long = 0,

    var name: String
): BaseEntity() {
}
