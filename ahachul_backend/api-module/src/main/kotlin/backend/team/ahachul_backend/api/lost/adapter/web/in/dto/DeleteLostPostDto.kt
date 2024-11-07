package backend.team.ahachul_backend.api.lost.adapter.web.`in`.dto

import backend.team.ahachul_backend.api.lost.domain.entity.LostPostEntity
import backend.team.ahachul_backend.api.lost.domain.model.LostPostType

class DeleteLostPostDto {

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