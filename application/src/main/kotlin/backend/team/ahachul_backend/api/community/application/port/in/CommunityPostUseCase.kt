package backend.team.ahachul_backend.api.community.application.port.`in`

import backend.team.ahachul_backend.api.community.adapter.web.`in`.dto.*
import backend.team.ahachul_backend.api.community.application.command.`in`.*
import backend.team.ahachul_backend.common.dto.PageInfoDto

interface CommunityPostUseCase {

    fun searchCommunityPosts(command: SearchCommunityPostCommand): PageInfoDto<SearchCommunityPostDto.Response>

    fun searchCommunityHotPosts(command: SearchCommunityHotPostCommand): PageInfoDto<SearchCommunityPostDto.Response>

    fun getCommunityPost(command: GetCommunityPostCommand): GetCommunityPostDto.Response

    fun createCommunityPost(command: CreateCommunityPostCommand): CreateCommunityPostDto.Response

    fun updateCommunityPost(command: UpdateCommunityPostCommand): UpdateCommunityPostDto.Response

    fun deleteCommunityPost(command: DeleteCommunityPostCommand): DeleteCommunityPostDto.Response
}