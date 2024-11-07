package backend.team.ahachul_backend.api.community.application.port.`in`

import backend.team.ahachul_backend.api.community.adapter.web.`in`.dto.comment.*

interface CommunityCommentUseCase {

    fun getCommunityComments(command: GetCommunityCommentsCommand): GetCommunityCommentsDto.Response

    fun createCommunityComment(command: CreateCommunityCommentCommand): CreateCommunityCommentDto.Response

    fun updateCommunityComment(command: UpdateCommunityCommentCommand): UpdateCommunityCommentDto.Response

    fun deleteCommunityComment(command: DeleteCommunityCommentCommand): DeleteCommunityCommentDto.Response
}