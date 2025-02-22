package backend.team.ahachul_backend.api.member.application.command

import backend.team.ahachul_backend.common.exception.BusinessException
import backend.team.ahachul_backend.common.response.ResponseCode

data class BookmarkStationCommands(
    val stations: List<BookmarkStationCommand>
) {
    init {
        if (stations.size > MAX_STATION_COUNT) {
            throw BusinessException(ResponseCode.EXCEED_MAXIMUM_STATION_COUNT)
        }
    }

    companion object {
        const val MAX_STATION_COUNT = 4
    }
}
