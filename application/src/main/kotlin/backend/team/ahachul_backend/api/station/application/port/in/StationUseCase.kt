package backend.team.ahachul_backend.api.station.application.port.`in`

import backend.team.ahachul_backend.api.station.adapter.`in`.dto.GetStationTimesDto
import backend.team.ahachul_backend.api.station.adapter.`in`.dto.SearchSubwayRouteDto
import backend.team.ahachul_backend.api.station.application.port.`in`.dto.GetStationTimesFullCommand
import backend.team.ahachul_backend.api.station.application.port.`in`.dto.GetStationLastTrainRiskCommand
import backend.team.ahachul_backend.api.station.application.port.`in`.dto.GetStationQuickExitCommand
import backend.team.ahachul_backend.api.station.application.port.`in`.dto.GetStationTimesCommand
import backend.team.ahachul_backend.api.station.application.port.`in`.dto.GetStationTimesSummaryCommand
import backend.team.ahachul_backend.api.station.application.port.`in`.dto.SearchSubwayRouteCommand

interface StationUseCase {

    fun getStationTimes(command: GetStationTimesCommand): GetStationTimesDto.Response

    fun getStationTimesFull(command: GetStationTimesFullCommand): GetStationTimesDto.FullResponse

    fun getStationTimesSummary(command: GetStationTimesSummaryCommand): GetStationTimesDto.SummaryResponse

    fun getLastTrainRisk(command: GetStationLastTrainRiskCommand): GetStationTimesDto.LastTrainRiskResponse

    fun getQuickExits(command: GetStationQuickExitCommand): GetStationTimesDto.QuickExitResponse

    fun searchSubwayRoutes(command: SearchSubwayRouteCommand): SearchSubwayRouteDto.Response
}
