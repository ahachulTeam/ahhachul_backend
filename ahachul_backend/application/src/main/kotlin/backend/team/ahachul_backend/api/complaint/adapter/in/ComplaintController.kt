package backend.team.ahachul_backend.api.complaint.adapter.`in`

import backend.team.ahachul_backend.api.complaint.adapter.`in`.dto.SearchComplaintMessagesDto
import backend.team.ahachul_backend.api.complaint.adapter.`in`.dto.SendComplaintMessageDto
import backend.team.ahachul_backend.api.complaint.application.port.`in`.ComplaintUseCase
import backend.team.ahachul_backend.common.annotation.Authentication
import backend.team.ahachul_backend.common.response.CommonResponse
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ComplaintController(
    private val complaintUseCase: ComplaintUseCase,
) {
    @GetMapping("/v1/complaints/messages")
    fun searchComplaintMessages(pageable: Pageable): CommonResponse<SearchComplaintMessagesDto.Response> {
        return CommonResponse.success(complaintUseCase.searchComplaintMessages(pageable))
    }

    @Authentication
    @PostMapping("/v1/complaints/messages")
    fun sendComplaintMessage(request: SendComplaintMessageDto.Request): CommonResponse<Unit> {
        complaintUseCase.sendComplaintMessage(request.toCommand())
        return CommonResponse.success()
    }
}