package backend.team.ahachul_backend.api.station.application.port.`in`

import backend.team.ahachul_backend.api.station.adapter.`in`.dto.GetStationWeatherBriefDto
import backend.team.ahachul_backend.api.station.application.port.`in`.dto.GetStationWeatherBriefCommand

interface StationWeatherUseCase {

    fun getStationWeatherBrief(command: GetStationWeatherBriefCommand): GetStationWeatherBriefDto.Response
}
