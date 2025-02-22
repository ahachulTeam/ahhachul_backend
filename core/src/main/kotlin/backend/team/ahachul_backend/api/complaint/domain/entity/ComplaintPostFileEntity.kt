package backend.team.ahachul_backend.api.complaint.domain.entity

import backend.team.ahachul_backend.common.domain.entity.FileEntity
import backend.team.ahachul_backend.common.domain.entity.BaseEntity
import jakarta.persistence.*

@Entity
class ComplaintPostFileEntity (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "complaint_post_file_id")
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "complaint_post_id")
    var complaintPost: ComplaintPostEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    var file: FileEntity

): BaseEntity() {

    companion object {
        fun of(post: ComplaintPostEntity, file: FileEntity): ComplaintPostFileEntity {
            return ComplaintPostFileEntity(
                complaintPost = post,
                file = file
            )
        }
    }
}
