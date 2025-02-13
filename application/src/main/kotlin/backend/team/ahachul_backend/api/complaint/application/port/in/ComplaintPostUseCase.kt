package backend.team.ahachul_backend.api.complaint.application.port.`in`

import backend.team.ahachul_backend.api.complaint.adapter.web.`in`.dto.*
import backend.team.ahachul_backend.api.complaint.application.command.`in`.CreateComplaintPostCommand
import backend.team.ahachul_backend.api.complaint.application.command.`in`.SearchComplaintPostCommand
import backend.team.ahachul_backend.api.complaint.application.command.`in`.UpdateComplaintPostCommand
import backend.team.ahachul_backend.api.complaint.application.command.`in`.UpdateComplaintPostStatusCommand
import backend.team.ahachul_backend.common.dto.PageInfoDto

interface ComplaintPostUseCase {

    fun searchComplaintPosts(command: SearchComplaintPostCommand): PageInfoDto<SearchComplaintPostDto.Response>

    fun getComplaintPost(postId: Long): GetComplaintPostDto.Response

    fun createComplaintPost(command: CreateComplaintPostCommand): CreateComplaintPostDto.Response

    fun updateComplaintPost(command: UpdateComplaintPostCommand): UpdateComplaintPostDto.Response

    fun updateComplaintPostStatus(command: UpdateComplaintPostStatusCommand): UpdateComplaintPostStatusDto.Response

    fun deleteComplaintPost(postId: Long): DeleteComplaintPostDto.Response
}