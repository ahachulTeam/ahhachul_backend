package backend.team.ahachul_backend.api.member.adapter.web.`in`.dto

import backend.team.ahachul_backend.api.member.application.port.`in`.command.GetTokenCommand
import org.jetbrains.annotations.NotNull

class GetTokenDto {
    data class Request(
            @NotNull
            val refreshToken: String,
    ) {
        fun toCommand(): GetTokenCommand {
            return GetTokenCommand(
                    refreshToken = refreshToken
            )
        }
    }

    data class Response(
            val accessToken: String,
            val accessTokenExpiresIn: Long,
            val refreshToken: String? = null,
            val refreshTokenExpiresIn: Long? = null,
    )
}