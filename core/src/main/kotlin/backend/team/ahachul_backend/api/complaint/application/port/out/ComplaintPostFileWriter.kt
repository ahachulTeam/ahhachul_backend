package backend.team.ahachul_backend.api.complaint.application.port.out

import backend.team.ahachul_backend.api.complaint.domain.entity.ComplaintPostFileEntity

interface ComplaintPostFileWriter {

    fun save(entity: ComplaintPostFileEntity): ComplaintPostFileEntity

    fun deleteByFileId(fileId: Long)
}