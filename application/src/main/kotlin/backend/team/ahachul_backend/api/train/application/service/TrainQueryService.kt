package backend.team.ahachul_backend.api.train.application.service

import backend.team.ahachul_backend.api.common.application.port.out.StationReader
import backend.team.ahachul_backend.common.persistence.SubwayLineReader
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class TrainQueryService(
    private val stationLineReader: StationReader,
    private val subwayLineReader: SubwayLineReader,
) {

    data class StationSubwayInfo(
        val stationName: String,
        val subwayLineIdentity: Long,
        val lockKey: String,
    )

    fun getStationAndSubwayLine(stationId: Long, subwayLineId: Long): StationSubwayInfo {
        val station = stationLineReader.getById(stationId)
        val subwayLine = subwayLineReader.getById(subwayLineId)
        return StationSubwayInfo(
            stationName = station.name,
            subwayLineIdentity = subwayLine.identity,
            lockKey = "${subwayLine.identity}-${stationId}",
        )
    }

    fun getSubwayLineId(subwayLineId: Long): Long {
        return subwayLineReader.getById(subwayLineId).id
    }
}
