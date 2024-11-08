package backend.team.ahachul_backend.api.member.adapter.web.`in`.dto

import backend.team.ahachul_backend.api.member.application.port.`in`.command.BookmarkStationCommand

class BookmarkStationDto {

    data class Request(
        val stationNames: List<String>
    ) {
        fun toCommand(): BookmarkStationCommand {
            return BookmarkStationCommand(
                stationNames = stationNames.toMutableList()
            )
        }
    }

    data class Response(
        val memberStationIds: List<Long>
    )
}
