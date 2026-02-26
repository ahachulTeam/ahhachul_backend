package backend.team.ahachul_backend.api.member.adapter.web.`in`.dto

import backend.team.ahachul_backend.api.member.application.command.BookmarkStationCommand
import backend.team.ahachul_backend.api.member.application.command.BookmarkStationLocationMetaCommand
import backend.team.ahachul_backend.api.member.application.command.BookmarkStationCommands
import backend.team.ahachul_backend.api.member.domain.model.MemberStationWalkingSourceType

class BookmarkStationDto {

    data class Request(
        val stations: List<BookmarkStation>
    ) {
        fun toCommand(): BookmarkStationCommands {
            return BookmarkStationCommands(
                stations.map {
                    BookmarkStationCommand(
                        stationName = it.stationName,
                        label = it.label,
                        stationId = it.stationId,
                        locationMeta = it.locationMeta?.toCommand(),
                    )
                }
            )
        }
    }

    data class Response(
        val memberStationIds: List<Long>
    )

    data class BookmarkStation(
        val stationName: String,
        val label: String?,
        val stationId: Long? = null,
        val locationMeta: LocationMeta? = null,
    )

    data class LocationMeta(
        val locationName: String? = null,
        val roadAddress: String? = null,
        val jibunAddress: String? = null,
        val latitude: Double? = null,
        val longitude: Double? = null,
        val walkingMinutes: Int? = null,
        val walkingSource: MemberStationWalkingSourceType? = null,
    ) {
        fun toCommand(): BookmarkStationLocationMetaCommand {
            return BookmarkStationLocationMetaCommand(
                locationName = locationName,
                roadAddress = roadAddress,
                jibunAddress = jibunAddress,
                latitude = latitude,
                longitude = longitude,
                walkingMinutes = walkingMinutes,
                walkingSource = walkingSource,
            )
        }
    }
}
