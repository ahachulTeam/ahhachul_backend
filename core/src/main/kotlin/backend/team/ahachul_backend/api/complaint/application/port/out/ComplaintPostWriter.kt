package backend.team.ahachul_backend.api.complaint.application.port.out

import backend.team.ahachul_backend.api.complaint.domain.entity.ComplaintPostEntity

interface ComplaintPostWriter {

    fun save(entity: ComplaintPostEntity): ComplaintPostEntity
}