package backend.team.ahachul_backend.api.station.adapter.`in`.dto

import backend.team.ahachul_backend.api.station.application.port.`in`.dto.GetStationTimesCommand
import backend.team.ahachul_backend.api.train.domain.model.TrainType
import backend.team.ahachul_backend.api.train.domain.model.UpDownType

class GetStationTimesDto {

    data class Request(
        val stationId: Long,
        val subwayLineId: Long,
        val upDownType: UpDownType,
        val stationTimeWeekType: StationTimeWeekType,
    ) {
        fun toCommand(): GetStationTimesCommand {
            return GetStationTimesCommand(
                stationId = stationId,
                subwayLineId = subwayLineId,
                upDownType = upDownType,
                stationTimeWeekType = stationTimeWeekType,
            )
        }
    }

    data class Response(
        val stationTimes: List<StationTimes>
    )

    data class StationTimes(
        val arrivalTime: String,
        val departureTime: String,
        val arrivalStationName: String,
        val departureStationName: String,
        val trainType: TrainType,
    )
}