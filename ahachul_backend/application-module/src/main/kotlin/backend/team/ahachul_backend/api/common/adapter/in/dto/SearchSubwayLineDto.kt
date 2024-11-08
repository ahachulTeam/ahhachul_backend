package backend.team.ahachul_backend.api.common.adapter.`in`.dto

import backend.team.ahachul_backend.api.common.domain.entity.StationEntity
import backend.team.ahachul_backend.api.common.domain.entity.SubwayLineStationEntity
import backend.team.ahachul_backend.common.domain.entity.SubwayLineEntity
import com.fasterxml.jackson.annotation.JsonProperty

class SearchSubwayLineDto {

    data class Response(
        @JsonProperty("subwayLines") val subwayLines: List<SubwayLine>
    )
}


data class SubwayLine(
    @JsonProperty("id") val id: Long,
    @JsonProperty("name") val name: String,
    @JsonProperty("phoneNumber") val phoneNumber: String,
    @JsonProperty("stations") val stations: List<Station>
) {

    companion object {

        fun of(subwayLine: SubwayLineEntity, stations: List<Station>): SubwayLine {
            return SubwayLine(
                id = subwayLine.id,
                name = subwayLine.name,
                phoneNumber = subwayLine.phoneNumber,
                stations = stations
            )
        }
    }
}

data class Station(
    @JsonProperty("id") val id: Long,
    @JsonProperty("name") val name: String
) {
    companion object {
        fun from(station: StationEntity): Station {
            return Station(
                id = station.id,
                name = station.name
            )
        }

        fun toList(stations: List<SubwayLineStationEntity>): List<Station> {
            return stations.map { from(it.station) }
        }
    }
}
