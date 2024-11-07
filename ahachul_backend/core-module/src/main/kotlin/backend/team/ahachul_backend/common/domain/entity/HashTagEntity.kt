package backend.team.ahachul_backend.common.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class HashTagEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hash_tag_id")
    val id: Long = 0,

    val name: String
): BaseEntity() {
}
