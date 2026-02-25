package backend.team.ahachul_backend.common.client

import backend.team.ahachul_backend.common.client.dto.OpenMeteoWeatherDto

interface OpenMeteoWeatherClient {

    fun getCurrentWeather(request: OpenMeteoWeatherDto.Request): OpenMeteoWeatherDto.Response
}
