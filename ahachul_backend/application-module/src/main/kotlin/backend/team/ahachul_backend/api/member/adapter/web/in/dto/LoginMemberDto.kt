package backend.team.ahachul_backend.api.member.adapter.web.`in`.dto

import backend.team.ahachul_backend.api.member.application.port.`in`.command.LoginMemberCommand
import backend.team.ahachul_backend.api.member.domain.model.ProviderType
import org.jetbrains.annotations.NotNull

class LoginMemberDto {
    data class Request(
            @NotNull
            val providerType: ProviderType,
            @NotNull
            val providerCode: String
    ) {
        fun toCommand(originHost: String?): LoginMemberCommand {
            return LoginMemberCommand(
                    originHost = originHost,
                    providerType = providerType,
                    providerCode = providerCode
            )
        }
    }

    data class Response(
            val memberId: String,
            val isNeedAdditionalUserInfo: Boolean,
            val accessToken: String,
            val accessTokenExpiresIn: Long,
            val refreshToken: String,
            val refreshTokenExpiresIn: Long,
    )
}