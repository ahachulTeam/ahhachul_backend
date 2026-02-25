package backend.team.ahachul_backend.common.client.dto

import com.fasterxml.jackson.annotation.JsonProperty

class OpenMeteoWeatherDto {

    data class Request(
        val latitude: Double,
        val longitude: Double,
        val timezone: String,
    )

    data class Response(
        @JsonProperty("current") val current: CurrentWeather?,
    )

    data class CurrentWeather(
        @JsonProperty("temperature_2m") val temperatureC: Double?,
        @JsonProperty("apparent_temperature") val apparentTemperatureC: Double?,
        @JsonProperty("weather_code") val weatherCode: Int?,
        @JsonProperty("is_day") val isDay: Int?,
        @JsonProperty("precipitation") val precipitationMm: Double?,
        @JsonProperty("wind_speed_10m") val windSpeedMps: Double?,
    )
}
