package backend.team.ahachul_backend.api.station.adapter.`in`.dto

import backend.team.ahachul_backend.api.station.application.port.`in`.dto.GetStationWeatherBriefCommand

class GetStationWeatherBriefDto {

    data class Request(
        val stationId: Long,
    ) {
        fun toCommand(): GetStationWeatherBriefCommand {
            return GetStationWeatherBriefCommand(
                stationId = stationId,
            )
        }
    }

    data class Response(
        val stationId: Long,
        val stationName: String,
        val generatedAt: String,
        val dataSource: WeatherDataSource,
        val isStale: Boolean,
        val summaryText: String,
        val cautionText: String,
        val friendlyText: String,
        val temperatureC: Double?,
        val apparentTemperatureC: Double?,
        val precipitationMm: Double?,
        val windSpeedMps: Double?,
        val weatherCode: Int?,
        val weatherLabel: String,
    )

    enum class WeatherDataSource {
        API, CACHE, STALE_CACHE, FALLBACK
    }
}
