package backend.team.ahachul_backend.common.domain.entity

import backend.team.ahachul_backend.common.exception.CommonException
import backend.team.ahachul_backend.common.response.ResponseCode
import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity {
    @CreatedDate
    @Column(updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()

    @CreatedBy
    @Column(updatable = false)
    var createdBy: String = ""

    @LastModifiedDate
    var updatedAt: LocalDateTime = LocalDateTime.now()

    @LastModifiedBy
    var updatedBy: String = ""

    fun checkMe(createdBy: String) {
        if (this.createdBy != createdBy) throw CommonException(ResponseCode.INVALID_AUTH)
    }
}
