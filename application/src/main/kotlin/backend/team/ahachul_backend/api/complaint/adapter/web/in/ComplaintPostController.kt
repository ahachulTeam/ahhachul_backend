package backend.team.ahachul_backend.api.complaint.adapter.web.`in`

import backend.team.ahachul_backend.api.complaint.adapter.web.`in`.dto.*
import backend.team.ahachul_backend.api.complaint.application.port.`in`.ComplaintPostUseCase
import backend.team.ahachul_backend.common.annotation.Authentication
import backend.team.ahachul_backend.common.dto.PageInfoDto
import backend.team.ahachul_backend.common.response.CommonResponse
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
class ComplaintPostController(
    private val complaintPostUseCase: ComplaintPostUseCase
) {

    @GetMapping("/v1/complaint-posts")
    fun searchComplaintPosts(
        @RequestParam(required = false) pageToken: String?,
        @RequestParam pageSize: Int,
        request: SearchComplaintPostDto.Request
    ): CommonResponse<PageInfoDto<SearchComplaintPostDto.Response>> {
        return CommonResponse.success(complaintPostUseCase.searchComplaintPosts(request.toCommand(pageToken, pageSize)))
    }

    @GetMapping("/v1/complaint-posts/{postId}")
    fun getComplaintPost(@PathVariable("postId") postId: Long): CommonResponse<GetComplaintPostDto.Response> {
        return CommonResponse.success(complaintPostUseCase.getComplaintPost(postId))
    }

    @Authentication
    @PostMapping("/v1/complaint-posts")
    fun createComplaintPost(
        @RequestPart(value = "content") request: CreateComplaintPostDto.Request,
        @RequestPart(value = "files", required = false) imageFiles: List<MultipartFile>?
    ): CommonResponse<CreateComplaintPostDto.Response> {
        return CommonResponse.success(complaintPostUseCase.createComplaintPost(request.toCommand(imageFiles)))
    }

    @Authentication
    @PostMapping("/v1/complaint-posts/{postId}")
    fun updateComplaintPost(
        @PathVariable("postId") postId: Long,
        @RequestPart(value = "content") request: UpdateComplaintPostDto.Request,
        @RequestPart(value = "files", required = false) imageFiles: List<MultipartFile>?
    ): CommonResponse<UpdateComplaintPostDto.Response> {
        return CommonResponse.success(complaintPostUseCase.updateComplaintPost(request.toCommand(postId, imageFiles)))
    }

    @Authentication
    @PatchMapping("/v1/complaint-posts/{postId}/status")
    fun updateComplaintPostStatus(
        @PathVariable("postId") postId: Long,
        @RequestBody request: UpdateComplaintPostStatusDto.Request
    ): CommonResponse<UpdateComplaintPostStatusDto.Response> {
        return CommonResponse.success(complaintPostUseCase.updateComplaintPostStatus(request.toCommand(postId)))
    }

    @Authentication
    @DeleteMapping("/v1/complaint-posts/{postId}")
    fun deleteComplaintPost(@PathVariable("postId") postId: Long): CommonResponse<DeleteComplaintPostDto.Response> {
        return CommonResponse.success(complaintPostUseCase.deleteComplaintPost(postId))
    }

}