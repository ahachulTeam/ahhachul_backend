package backend.team.ahachul_backend.api.member.application.port.`in`

import backend.team.ahachul_backend.api.member.adapter.web.`in`.dto.GetRedirectUrlDto
import backend.team.ahachul_backend.api.member.adapter.web.`in`.dto.GetTokenDto
import backend.team.ahachul_backend.api.member.adapter.web.`in`.dto.LoginMemberDto
import backend.team.ahachul_backend.api.member.application.port.`in`.command.GetRedirectUrlCommand
import backend.team.ahachul_backend.api.member.application.port.`in`.command.GetTokenCommand
import backend.team.ahachul_backend.api.member.application.port.`in`.command.LoginMemberCommand

interface AuthUseCase {

    fun login(command: LoginMemberCommand): LoginMemberDto.Response

    fun getToken(command: GetTokenCommand): GetTokenDto.Response

    fun getRedirectUrl(command: GetRedirectUrlCommand): GetRedirectUrlDto.Response
}