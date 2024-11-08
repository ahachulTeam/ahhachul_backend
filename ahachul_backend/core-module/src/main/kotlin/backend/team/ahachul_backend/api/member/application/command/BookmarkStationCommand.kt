package backend.team.ahachul_backend.api.member.application.port.`in`.command

import backend.team.ahachul_backend.common.exception.BusinessException
import backend.team.ahachul_backend.common.response.ResponseCode

data class BookmarkStationCommand(
    val stationNames: MutableList<String>
) {
    init {
        if (stationNames.size > MAX_STATION_COUNT) {
            throw BusinessException(ResponseCode.EXCEED_MAXIMUM_STATION_COUNT)
        }
    }

    companion object {
        const val MAX_STATION_COUNT = 3
    }
}
