package backend.team.ahachul_backend.api.station.application.port.`in`

import backend.team.ahachul_backend.api.station.adapter.`in`.dto.GetStationTimesDto
import backend.team.ahachul_backend.api.station.application.port.`in`.dto.GetStationTimesCommand

interface StationUseCase {

    fun getStationTimes(command: GetStationTimesCommand): GetStationTimesDto.Response
}