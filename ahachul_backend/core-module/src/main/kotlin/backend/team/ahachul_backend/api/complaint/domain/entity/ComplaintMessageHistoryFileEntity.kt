package backend.team.ahachul_backend.api.complaint.domain.entity

import backend.team.ahachul_backend.common.domain.entity.FileEntity
import backend.team.ahachul_backend.common.domain.entity.BaseEntity
import jakarta.persistence.*

@Entity
class ComplaintMessageHistoryFileEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "complaint_message_history_file_id")
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "complaint_message_history_id")
    var complaintMessageHistory: ComplaintMessageHistoryEntity,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "file_id")
    var file: FileEntity
): BaseEntity() {

    companion object {
        fun of(complaintMessage: ComplaintMessageHistoryEntity, file: FileEntity): ComplaintMessageHistoryFileEntity {
            return ComplaintMessageHistoryFileEntity(
                complaintMessageHistory = complaintMessage,
                file = file
            )
        }
    }
}
