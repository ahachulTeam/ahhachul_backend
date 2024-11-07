package backend.team.ahachul_backend.api.complaint.application.port.out

import backend.team.ahachul_backend.api.complaint.domain.entity.ComplaintMessageHistoryFileEntity

interface ComplaintMessageFileWriter {

    fun save(complaintMessageHistory: ComplaintMessageHistoryFileEntity): ComplaintMessageHistoryFileEntity
}