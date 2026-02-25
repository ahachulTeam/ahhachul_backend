package backend.team.ahachul_backend.api.station.adapter.`in`.dto

import backend.team.ahachul_backend.api.station.application.port.`in`.dto.GetStationLastTrainRiskCommand
import backend.team.ahachul_backend.api.station.application.port.`in`.dto.GetStationQuickExitCommand
import backend.team.ahachul_backend.api.station.application.port.`in`.dto.GetStationTimesFullCommand
import backend.team.ahachul_backend.api.station.application.port.`in`.dto.GetStationTimesQualityReportCommand
import backend.team.ahachul_backend.api.station.application.port.`in`.dto.GetStationTimesCommand
import backend.team.ahachul_backend.api.station.application.port.`in`.dto.GetStationTimesSummaryCommand
import backend.team.ahachul_backend.api.train.domain.model.TrainType
import backend.team.ahachul_backend.api.train.domain.model.UpDownType

class GetStationTimesDto {

    data class Request(
        val stationId: Long,
        val subwayLineId: Long,
        val upDownType: UpDownType,
        val stationTimeWeekType: StationTimeWeekType? = null,
        val weekTag: Int? = null,
    ) {
        private fun resolveStationTimeWeekType(): StationTimeWeekType {
            return stationTimeWeekType
                ?: StationTimeWeekType.fromPublicCodeOrNull(weekTag)
                ?: StationTimeWeekType.WEEKDAY
        }

        fun toCommand(): GetStationTimesCommand {
            return GetStationTimesCommand(
                stationId = stationId,
                subwayLineId = subwayLineId,
                upDownType = upDownType,
                stationTimeWeekType = resolveStationTimeWeekType(),
            )
        }
    }

    data class Response(
        val stationTimes: List<StationTimes>
    )

    data class FullRequest(
        val stationId: Long,
        val subwayLineId: Long,
    ) {
        fun toCommand(): GetStationTimesFullCommand {
            return GetStationTimesFullCommand(
                stationId = stationId,
                subwayLineId = subwayLineId,
            )
        }
    }

    data class FullResponse(
        val generatedAt: String,
        val stationId: Long,
        val subwayLineId: Long,
        val weeks: List<WeekTimetable>,
    )

    data class WeekTimetable(
        val stationTimeWeekType: StationTimeWeekType,
        val upDownTimetables: List<UpDownTimetable>,
    )

    data class UpDownTimetable(
        val upDownType: UpDownType,
        val stationTimes: List<StationTimes>,
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
        val meta: SummaryMeta,
    )

    data class UpDownSummary(
        val upDownType: UpDownType,
        val firstDepartureTime: String?,
        val lastDepartureTime: String?,
        val firstDestinationStationName: String?,
        val lastDestinationStationName: String?,
    )

    data class SummaryMeta(
        val generatedAt: String,
        val availabilityStatus: StationSummaryAvailabilityStatus,
        val coveragePercent: Int,
        val guidanceMessage: String,
        val sourceDetails: List<SummarySourceDetail>,
    )

    data class SummarySourceDetail(
        val upDownType: UpDownType,
        val dataSource: StationSummaryDataSource,
        val stationTimesCount: Int,
        val fallbackReasonCode: String?,
    )

    data class QualityReportRequest(
        val stationTimeWeekType: StationTimeWeekType = StationTimeWeekType.WEEKDAY,
        val samplePerLine: Int = 10,
    ) {
        fun toCommand(): GetStationTimesQualityReportCommand {
            return GetStationTimesQualityReportCommand(
                stationTimeWeekType = stationTimeWeekType,
                samplePerLine = samplePerLine.coerceIn(1, 50),
            )
        }
    }

    data class QualityReportResponse(
        val generatedAt: String,
        val stationTimeWeekType: StationTimeWeekType,
        val totalLineCount: Int,
        val totalSampledStations: Int,
        val totalNoDataStations: Int,
        val overallNoDataRatioPercent: Int,
        val lines: List<LineQualityReport>,
    )

    data class LineQualityReport(
        val subwayLineId: Long,
        val subwayLineName: String,
        val sampledStations: Int,
        val noDataStations: Int,
        val noDataRatioPercent: Int,
        val fallbackStations: Int,
        val missingStationCodeStations: Int,
        val qualityLevel: StationTimeQualityLevel,
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

    data class QuickExitRequest(
        val stationId: Long,
        val subwayLineId: Long,
        val upDownType: UpDownType,
    ) {
        fun toCommand(): GetStationQuickExitCommand {
            return GetStationQuickExitCommand(
                stationId = stationId,
                subwayLineId = subwayLineId,
                upDownType = upDownType,
            )
        }
    }

    data class QuickExitResponse(
        val stationId: Long,
        val subwayLineId: Long,
        val upDownType: UpDownType,
        val recommendations: List<QuickExitRecommendation>,
    )

    data class QuickExitRecommendation(
        val carNo: String,
        val exitNo: String,
        val directionHint: String,
        val walkingBenefitMinutes: Int,
        val confidenceLevel: QuickExitConfidenceLevel,
    )

    enum class LastTrainRiskLevel {
        SAFE, WARN, RISK
    }

    enum class QuickExitConfidenceLevel {
        HIGH, MEDIUM, LOW
    }

    enum class StationSummaryAvailabilityStatus {
        AVAILABLE, PARTIAL, EMPTY
    }

    enum class StationSummaryDataSource {
        CACHE, API, FALLBACK_EMPTY
    }

    enum class StationTimeQualityLevel {
        GOOD, WARN, CRITICAL
    }
}
