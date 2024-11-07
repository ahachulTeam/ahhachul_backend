package backend.team.ahachul_backend.api.lost.application.port.`in`

import backend.team.ahachul_backend.api.lost.adapter.web.`in`.dto.*
import backend.team.ahachul_backend.api.lost.application.service.command.`in`.CreateLostPostCommand
import backend.team.ahachul_backend.api.lost.application.service.command.`in`.SearchLostPostCommand
import backend.team.ahachul_backend.api.lost.application.service.command.`in`.UpdateLostPostCommand
import backend.team.ahachul_backend.common.dto.PageInfoDto

interface LostPostUseCase {

    fun getLostPost(id: Long): GetLostPostDto.Response

    fun searchLostPosts(command: SearchLostPostCommand): PageInfoDto<SearchLostPostsDto.Response>

    fun createLostPost(command: CreateLostPostCommand): CreateLostPostDto.Response

    fun updateLostPost(command: UpdateLostPostCommand): UpdateLostPostDto.Response

    fun deleteLostPost(id: Long): DeleteLostPostDto.Response
}
