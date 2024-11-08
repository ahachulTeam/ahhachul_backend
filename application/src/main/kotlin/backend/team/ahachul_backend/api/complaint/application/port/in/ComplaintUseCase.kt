package backend.team.ahachul_backend.api.complaint.application.port.`in`

import backend.team.ahachul_backend.api.complaint.adapter.`in`.dto.SearchComplaintMessagesDto
import backend.team.ahachul_backend.api.complaint.application.command.SendComplaintMessageCommand
import org.springframework.data.domain.Pageable

interface ComplaintUseCase {

    fun searchComplaintMessages(pageable: Pageable): SearchComplaintMessagesDto.Response

    fun sendComplaintMessage(command: SendComplaintMessageCommand)
}
