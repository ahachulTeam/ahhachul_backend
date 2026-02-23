package backend.team.ahachul_backend.api.station.application.service

import backend.team.ahachul_backend.api.station.adapter.`in`.dto.GetStationTimesDto
import java.time.Duration
import java.time.LocalTime
import java.time.OffsetDateTime

object StationLastTrainRiskCalculator {

    private const val DEFAULT_WAITING_MINUTES = 3

    data class Result(
        val nowAt: OffsetDateTime,
        val minutesToLastTrain: Int,
        val riskLevel: GetStationTimesDto.LastTrainRiskLevel,
        val message: String,
    ) {
        val isLastTrainRisk: Boolean
            get() = riskLevel != GetStationTimesDto.LastTrainRiskLevel.SAFE
    }

    fun calculate(nowAt: OffsetDateTime, lastDepartureTime: String?, walkingMinutes: Int): Result {
        val safeWalkingMinutes = walkingMinutes.coerceAtLeast(0)

        if (lastDepartureTime.isNullOrBlank()) {
            return Result(
                nowAt = nowAt,
                minutesToLastTrain = -1,
                riskLevel = GetStationTimesDto.LastTrainRiskLevel.RISK,
                message = "막차 정보가 없습니다.",
            )
        }

        val parsedLastTime = runCatching { LocalTime.parse(lastDepartureTime) }.getOrNull()
        if (parsedLastTime == null) {
            return Result(
                nowAt = nowAt,
                minutesToLastTrain = -1,
                riskLevel = GetStationTimesDto.LastTrainRiskLevel.RISK,
                message = "막차 정보를 해석할 수 없습니다.",
            )
        }

        var departureAt = nowAt.toLocalDate().atTime(parsedLastTime)
        val nowLocalDateTime = nowAt.toLocalDateTime()

        if (departureAt.isBefore(nowLocalDateTime) && nowAt.hour >= 5) {
            departureAt = departureAt.plusDays(1)
        }

        val minutesToLastTrain = Duration.between(nowLocalDateTime, departureAt).toMinutes().toInt()
        val requiredMinutes = safeWalkingMinutes + DEFAULT_WAITING_MINUTES

        val riskLevel = when {
            minutesToLastTrain <= 0 -> GetStationTimesDto.LastTrainRiskLevel.RISK
            minutesToLastTrain <= requiredMinutes -> GetStationTimesDto.LastTrainRiskLevel.WARN
            else -> GetStationTimesDto.LastTrainRiskLevel.SAFE
        }

        val message = when (riskLevel) {
            GetStationTimesDto.LastTrainRiskLevel.SAFE -> "현재 기준 막차 여유가 있습니다."
            GetStationTimesDto.LastTrainRiskLevel.WARN -> "막차가 임박했습니다. 서둘러 이동해 주세요."
            GetStationTimesDto.LastTrainRiskLevel.RISK -> "지금 출발하면 막차 탑승이 어려울 수 있습니다."
        }

        return Result(
            nowAt = nowAt,
            minutesToLastTrain = minutesToLastTrain,
            riskLevel = riskLevel,
            message = message,
        )
    }
}
