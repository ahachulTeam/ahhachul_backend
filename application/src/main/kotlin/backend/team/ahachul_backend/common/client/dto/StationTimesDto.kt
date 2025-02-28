package backend.team.ahachul_backend.common.client.dto

import backend.team.ahachul_backend.api.station.adapter.`in`.dto.GetStationTimesDto
import backend.team.ahachul_backend.api.train.domain.model.TrainType
import com.fasterxml.jackson.annotation.JsonProperty

class StationTimesDto {

    companion object {
        const val DEFAULT_START_INDEX = 1
        const val DEFAULT_END_INDEX = 300
        const val SUCCESS_CODE = "INFO-000"
    }

    data class Request(
        val stationCd: String,
        val weekTag: Int,
        val inoutTag: Int,
        val startIndex: Int = DEFAULT_START_INDEX,
        val endIndex: Int = DEFAULT_END_INDEX,
    )

    data class Response(
        @JsonProperty("SearchSTNTimeTableByIDService") val stationTimesTable: StationTimesTable,
    ) {
        fun isFail(): Boolean {
            return stationTimesTable.isFail()
        }

        fun toStationTimes(): List<GetStationTimesDto.StationTimes> {
            return stationTimesTable.rows.map {
                GetStationTimesDto.StationTimes(
                    arrivalTime = it.arrivetime,
                    departureTime = it.lefttime,
                    arrivalStationName = it.subwayename,
                    departureStationName = it.subwaysname,
                    trainType = TrainType.from(it.expressYn),
                )
            }
        }
    }

    data class StationTimesTable(
        @JsonProperty("list_total_count") val totalCount: Int,
        @JsonProperty("RESULT") val result: StationTimesResult,
        @JsonProperty("row") val rows: List<StationTimeRow>,
    ) {
        fun isFail(): Boolean {
            return totalCount == 0 || SUCCESS_CODE != result.code || rows.isEmpty()
        }
    }
    
    data class StationTimesResult(
        @JsonProperty("CODE") val code: String,
        @JsonProperty("MESSAGE") val message: String,
    )

    data class StationTimeRow(
        @JsonProperty("LINE_NUM") val lineNum: String,
        @JsonProperty("FR_CODE") val frCode: String,
        @JsonProperty("STATION_CD") val stationCd: String,
        @JsonProperty("STATION_NM") val stationNm: String,
        @JsonProperty("TRAIN_NO") val trainNo: String,
        @JsonProperty("ARRIVETIME") val arrivetime: String,
        @JsonProperty("LEFTTIME") val lefttime: String,
        @JsonProperty("ORIGINSTATION") val originstation: String,
        @JsonProperty("DESTSTATION") val deststation: String,
        @JsonProperty("SUBWAYSNAME") val subwaysname: String,
        @JsonProperty("SUBWAYENAME") val subwayename: String,
        @JsonProperty("WEEK_TAG") val weekTag: String,
        @JsonProperty("INOUT_TAG") val inoutTag: String,
        @JsonProperty("FL_FLAG") val flFlag: String,
        @JsonProperty("DESTSTATION2") val deststation2: String,
        @JsonProperty("EXPRESS_YN") val expressYn: String,
        @JsonProperty("BRANCH_LINE") val branchLine: String,
    )
}