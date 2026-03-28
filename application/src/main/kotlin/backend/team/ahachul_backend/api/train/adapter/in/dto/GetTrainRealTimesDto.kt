package backend.team.ahachul_backend.api.train.adapter.`in`.dto

import backend.team.ahachul_backend.api.train.domain.model.TrainArrivalCode
import backend.team.ahachul_backend.api.train.domain.model.UpDownType
import backend.team.ahachul_backend.common.dto.RealtimeArrivalListDTO
import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class GetTrainRealTimesDto {

    data class Request(
        val stationId: Long,
        val subwayLineId: Long,
        val upDownType: UpDownType?
    )

    data class Response(
        val trainRealTimes: List<TrainRealTime>,
    )

    data class TrainRealTime(
        @JsonIgnore
        val subwayId: String?,
        @JsonIgnore
        val arrivalSeconds: Long,
        val upDownType: UpDownType,
        val nextStationDirection: String,
        val destinationStationDirection: String,
        val trainNum: String,
        val currentArrivalTime: Long,
        val currentTrainArrivalCode: TrainArrivalCode,
    ) {

        companion object {
            fun of(dto: RealtimeArrivalListDTO): TrainRealTime {
                val barvlDt = dto.barvlDt?.toLongOrNull() ?: 0L
                val arrivalSeconds = computeArrivalSeconds(barvlDt, dto.arvlMsg2, dto.recptnDt)
                val trainDirection = dto.trainLineNm.split("-")
                return TrainRealTime(
                    subwayId = dto.subwayId,
                    arrivalSeconds = arrivalSeconds,
                    upDownType = UpDownType.from(dto.updnLine),
                    nextStationDirection = trainDirection[1].trim(),
                    destinationStationDirection = trainDirection[0].trim(),
                    trainNum = dto.btrainNo,
                    currentArrivalTime = System.currentTimeMillis() / 1000 + arrivalSeconds,
                    currentTrainArrivalCode = TrainArrivalCode.from(dto.arvlCd)
                )
            }

            private fun computeArrivalSeconds(barvlDt: Long, arvlMsg2: String, recptnDt: String?): Long {
                val elapsed = recptnDt
                    ?.runCatching {
                        LocalDateTime.parse(this, RECPTN_DT_FORMATTER).toEpochSecond(ZoneOffset.of("+09:00"))
                    }
                    ?.getOrNull()
                    ?.let { maxOf(System.currentTimeMillis() / 1000 - it, 0L) }
                    ?: 0L

                if (barvlDt > 0) return maxOf(barvlDt - elapsed, 0L)
                // barvlDt = 0이지만 [N]번째 전역인 경우 → 역당 150초로 추정
                val match = DISTANT_STATION_PATTERN.find(arvlMsg2)
                if (match != null) {
                    return maxOf(match.groupValues[1].toLong() * SECONDS_PER_STATION - elapsed, 0L)
                }
                return 0L
            }

            private val RECPTN_DT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            private val DISTANT_STATION_PATTERN = "\\[(\\d+)\\]번째".toRegex()
            private const val SECONDS_PER_STATION = 150L  // 역당 2분 30초 추정
        }
    }
}
