package backend.team.ahachul_backend.api.complaint.application.command.`in`

import backend.team.ahachul_backend.api.complaint.domain.model.ComplaintPostType

class UpdateComplaintPostStatusCommand(
    val id: Long,
    val status: ComplaintPostType
) {

}