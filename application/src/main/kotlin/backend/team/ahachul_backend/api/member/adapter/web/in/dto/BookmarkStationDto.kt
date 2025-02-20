package backend.team.ahachul_backend.api.member.adapter.web.`in`.dto

import backend.team.ahachul_backend.api.member.application.command.BookmarkStationCommand
import backend.team.ahachul_backend.api.member.application.command.BookmarkStationCommands

class BookmarkStationDto {

    data class Request(
        val stations: List<BookmarkStation>
    ) {
        fun toCommand(): BookmarkStationCommands {
            return BookmarkStationCommands(
                stations.map { BookmarkStationCommand(it.stationName, it.label) }
            )
        }
    }

    data class Response(
        val memberStationIds: List<Long>
    )

    data class BookmarkStation(
        val stationName: String,
        val label: String?,
    )
}
