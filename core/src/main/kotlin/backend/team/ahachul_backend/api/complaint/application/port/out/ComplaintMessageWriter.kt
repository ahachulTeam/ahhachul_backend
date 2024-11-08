package backend.team.ahachul_backend.api.complaint.application.port.out

import backend.team.ahachul_backend.api.complaint.domain.entity.ComplaintMessageHistoryEntity

interface ComplaintMessageWriter {

    fun save(complaintMessageHistory: ComplaintMessageHistoryEntity): ComplaintMessageHistoryEntity
}