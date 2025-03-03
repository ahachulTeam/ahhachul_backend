package backend.team.ahachul_backend.api.station.application.port.`in`.dto

import backend.team.ahachul_backend.api.train.domain.model.UpDownType
import backend.team.ahachul_backend.api.station.adapter.`in`.dto.StationTimeWeekType
import backend.team.ahachul_backend.common.client.dto.StationTimesDto

class GetStationTimesCommand(
    val stationId: Long,
    val subwayLineId: Long,
    val upDownType: UpDownType,
    val stationTimeWeekType: StationTimeWeekType,
) {

    fun toCacheCommand(stationCode: String): GetStationTimesCacheCommand {
        return GetStationTimesCacheCommand(
            stationCode = stationCode,
            upDownType = this.upDownType,
            stationTimeWeekType = this.stationTimeWeekType
        )
    }

    fun toRequest(stationCode: String): StationTimesDto.Request {
        return StationTimesDto.Request(
            stationCd = stationCode,
            weekTag = stationTimeWeekType.publicCode,
            inoutTag = upDownType.publicCode,
        )
    }
}