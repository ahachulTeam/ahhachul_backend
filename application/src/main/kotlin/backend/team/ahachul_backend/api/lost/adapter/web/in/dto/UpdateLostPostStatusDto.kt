package backend.team.ahachul_backend.api.lost.adapter.web.`in`.dto

import backend.team.ahachul_backend.api.lost.application.service.command.`in`.UpdateLostPostStatusCommand
import backend.team.ahachul_backend.api.lost.domain.entity.LostPostEntity
import backend.team.ahachul_backend.api.lost.domain.model.LostStatus

class UpdateLostPostStatusDto {

    data class Request(
        val status: LostStatus
    ) {
        fun toCommand(id: Long): UpdateLostPostStatusCommand {
            return UpdateLostPostStatusCommand(
                id = id,
                status = status
            )
        }
    }

    data class Response(
        val id: Long
    ) {
        companion object {
            fun from(entity: LostPostEntity): Response {
                return Response(
                    id = entity.id
                )
            }
        }
    }
}
