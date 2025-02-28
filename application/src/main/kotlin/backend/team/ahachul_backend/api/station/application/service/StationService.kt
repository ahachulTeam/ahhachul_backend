package backend.team.ahachul_backend.api.station.application.service

import backend.team.ahachul_backend.api.common.application.port.out.SubwayLineStationReader
import backend.team.ahachul_backend.api.station.adapter.`in`.dto.GetStationTimesDto
import backend.team.ahachul_backend.api.station.application.port.`in`.StationUseCase
import backend.team.ahachul_backend.api.station.application.port.`in`.dto.GetStationTimesCommand
import backend.team.ahachul_backend.common.client.SeoulTrainClient
import backend.team.ahachul_backend.common.exception.BusinessException
import backend.team.ahachul_backend.common.response.ResponseCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class StationService(
    private val subwayLineStationReader: SubwayLineStationReader,
    private val stationTimesCacheUtils: StationTimesCacheUtils,
    private val seoulTrainClient: SeoulTrainClient,
): StationUseCase {

    override fun getStationTimes(command: GetStationTimesCommand): GetStationTimesDto.Response {
        val subwayLineStation = subwayLineStationReader.findBySubwayLineIdAndStationId(command.subwayLineId, command.stationId)
        val stationCode = subwayLineStation.stationCode ?: throw BusinessException(ResponseCode.NOT_EXIST_PUBLIC_STATION_CODE)

        val cacheCommand = command.toCacheCommand(stationCode)
        stationTimesCacheUtils.getStationTimesByCache(cacheCommand)?.let {
            return GetStationTimesDto.Response(it)
        }

        val response = seoulTrainClient.getStationTimesByApi(command.toRequest(stationCode))
        if (response.isFail()) {
            throw BusinessException(ResponseCode.INVALID_STATION_TIMES_API_RESPONSE)
        }

        val stationTimes = response.toStationTimes()
        stationTimesCacheUtils.setStationTimesCache(cacheCommand, stationTimes)

        return GetStationTimesDto.Response(stationTimes)
    }
}