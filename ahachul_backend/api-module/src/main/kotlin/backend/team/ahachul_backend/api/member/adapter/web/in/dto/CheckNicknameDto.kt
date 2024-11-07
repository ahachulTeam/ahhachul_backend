package backend.team.ahachul_backend.api.member.adapter.web.`in`.dto

import backend.team.ahachul_backend.api.member.application.port.`in`.command.CheckNicknameCommand
import org.jetbrains.annotations.NotNull

class CheckNicknameDto {
    data class Request(
            @NotNull
            val nickname: String,
    ) {

        fun toCommand(): CheckNicknameCommand {
            return CheckNicknameCommand(
                nickname = nickname
            )
        }
    }

    data class Response(
            val available: Boolean,
    ) {

        companion object {
            fun of(available: Boolean): Response {
                return Response(
                    available = available
                )
            }
        }
    }
}