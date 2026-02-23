package backend.team.ahachul_backend.api.station.adapter.`in`.dto

import backend.team.ahachul_backend.api.station.application.port.`in`.dto.GetStationLastTrainRiskCommand
import backend.team.ahachul_backend.api.station.application.port.`in`.dto.GetStationTimesCommand
import backend.team.ahachul_backend.api.station.application.port.`in`.dto.GetStationTimesSummaryCommand
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

    data class SummaryRequest(
        val stationId: Long,
        val subwayLineId: Long,
        val stationTimeWeekType: StationTimeWeekType,
    ) {
        fun toCommand(): GetStationTimesSummaryCommand {
            return GetStationTimesSummaryCommand(
                stationId = stationId,
                subwayLineId = subwayLineId,
                stationTimeWeekType = stationTimeWeekType,
            )
        }
    }

    data class SummaryResponse(
        val stationTimeWeekType: StationTimeWeekType,
        val summaries: List<UpDownSummary>,
    )

    data class UpDownSummary(
        val upDownType: UpDownType,
        val firstDepartureTime: String?,
        val lastDepartureTime: String?,
        val firstDestinationStationName: String?,
        val lastDestinationStationName: String?,
    )

    data class LastTrainRiskRequest(
        val stationId: Long,
        val subwayLineId: Long,
        val upDownType: UpDownType,
        val stationTimeWeekType: StationTimeWeekType,
        val walkingMinutes: Int,
    ) {
        fun toCommand(): GetStationLastTrainRiskCommand {
            return GetStationLastTrainRiskCommand(
                stationId = stationId,
                subwayLineId = subwayLineId,
                upDownType = upDownType,
                stationTimeWeekType = stationTimeWeekType,
                walkingMinutes = walkingMinutes,
            )
        }
    }

    data class LastTrainRiskResponse(
        val stationTimeWeekType: StationTimeWeekType,
        val upDownType: UpDownType,
        val walkingMinutes: Int,
        val nowAt: String,
        val lastDepartureTime: String?,
        val minutesToLastTrain: Int,
        val isLastTrainRisk: Boolean,
        val riskLevel: LastTrainRiskLevel,
        val message: String,
    )

    enum class LastTrainRiskLevel {
        SAFE, WARN, RISK
    }
}
