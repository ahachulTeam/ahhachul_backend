package backend.team.ahachul_backend.common.client.impl

import backend.team.ahachul_backend.common.client.OpenMeteoWeatherClient
import backend.team.ahachul_backend.common.client.dto.OpenMeteoWeatherDto
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Component
class OpenMeteoWeatherClientImpl(
    private val restTemplate: RestTemplate,
) : OpenMeteoWeatherClient {

    override fun getCurrentWeather(request: OpenMeteoWeatherDto.Request): OpenMeteoWeatherDto.Response {
        val uri = UriComponentsBuilder.fromHttpUrl(OPEN_METEO_FORECAST_URL)
            .queryParam("latitude", request.latitude)
            .queryParam("longitude", request.longitude)
            .queryParam(
                "current",
                "temperature_2m,apparent_temperature,weather_code,is_day,precipitation,wind_speed_10m"
            )
            .queryParam("timezone", request.timezone)
            .build(true)
            .toUri()

        return restTemplate.exchange(uri, HttpMethod.GET, null, OpenMeteoWeatherDto.Response::class.java)
            .body ?: OpenMeteoWeatherDto.Response(current = null)
    }

    companion object {
        private const val OPEN_METEO_FORECAST_URL = "https://api.open-meteo.com/v1/forecast"
    }
}
