package backend.team.ahachul_backend.api.complaint.application.port.out

import backend.team.ahachul_backend.api.complaint.domain.entity.ComplaintPostFileEntity

interface ComplaintPostFileReader {

    fun findByPostId(postId: Long): ComplaintPostFileEntity?

    fun findAllByPostId(postId: Long): List<ComplaintPostFileEntity>
}