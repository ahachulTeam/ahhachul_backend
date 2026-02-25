package backend.team.ahachul_backend.api.station.application.service

import backend.team.ahachul_backend.api.common.application.port.out.StationReader
import backend.team.ahachul_backend.api.common.domain.entity.StationEntity
import backend.team.ahachul_backend.api.station.adapter.`in`.dto.GetStationWeatherBriefDto
import backend.team.ahachul_backend.api.station.application.port.`in`.dto.GetStationWeatherBriefCommand
import backend.team.ahachul_backend.common.client.OpenMeteoWeatherClient
import backend.team.ahachul_backend.common.client.RedisClient
import backend.team.ahachul_backend.common.client.dto.OpenMeteoWeatherDto
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions

class StationWeatherServiceUnitTest {

    private val stationReader: StationReader = Mockito.mock(StationReader::class.java)
    private val openMeteoWeatherClient: OpenMeteoWeatherClient = Mockito.mock(OpenMeteoWeatherClient::class.java)
    private val redisClient: RedisClient = Mockito.mock(RedisClient::class.java)
    private val objectMapper = jacksonObjectMapper()

    private val stationWeatherService = StationWeatherService(
        stationReader = stationReader,
        openMeteoWeatherClient = openMeteoWeatherClient,
        redisClient = redisClient,
        objectMapper = objectMapper,
    )

    @Test
    @DisplayName("fresh 캐시가 존재하면 외부 API를 호출하지 않고 캐시 응답을 반환한다")
    fun getStationWeatherBriefUsesFreshCache() {
        given(stationReader.getById(557L)).willReturn(StationEntity(id = 557L, name = "강남"))
        given(redisClient.get("STATION_WEATHER_BRIEF:FRESH")).willReturn(
            """
            {
              "generatedAt":"2026-02-25T20:00:00+09:00",
              "temperatureC":9.2,
              "apparentTemperatureC":7.1,
              "precipitationMm":0.0,
              "windSpeedMps":2.4,
              "weatherCode":1,
              "weatherLabel":"대체로 맑음",
              "isDay":1
            }
            """.trimIndent()
        )

        val result = stationWeatherService.getStationWeatherBrief(
            GetStationWeatherBriefCommand(stationId = 557L)
        )

        assertThat(result.stationName).isEqualTo("강남")
        assertThat(result.dataSource).isEqualTo(GetStationWeatherBriefDto.WeatherDataSource.CACHE)
        assertThat(result.isStale).isFalse()
        assertThat(result.summaryText).contains("현재")
        verifyNoInteractions(openMeteoWeatherClient)
    }

    @Test
    @DisplayName("fresh 캐시가 없으면 외부 API 호출 후 API 응답을 반환한다")
    fun getStationWeatherBriefUsesApi() {
        given(stationReader.getById(557L)).willReturn(StationEntity(id = 557L, name = "강남"))
        given(redisClient.get("STATION_WEATHER_BRIEF:FRESH")).willReturn(null)
        val request = OpenMeteoWeatherDto.Request(
            latitude = 37.566,
            longitude = 126.9784,
            timezone = "Asia/Seoul",
        )
        given(openMeteoWeatherClient.getCurrentWeather(request)).willReturn(
            OpenMeteoWeatherDto.Response(
                current = OpenMeteoWeatherDto.CurrentWeather(
                    temperatureC = 8.9,
                    apparentTemperatureC = 6.8,
                    weatherCode = 0,
                    isDay = 1,
                    precipitationMm = 0.0,
                    windSpeedMps = 2.1,
                )
            )
        )

        val result = stationWeatherService.getStationWeatherBrief(
            GetStationWeatherBriefCommand(stationId = 557L)
        )

        assertThat(result.dataSource).isEqualTo(GetStationWeatherBriefDto.WeatherDataSource.API)
        assertThat(result.isStale).isFalse()
        assertThat(result.friendlyText).contains("좋은 하루")
        verify(openMeteoWeatherClient, times(1)).getCurrentWeather(request)
    }

    @Test
    @DisplayName("외부 API 실패 시 stale 캐시가 있으면 stale 응답을 반환한다")
    fun getStationWeatherBriefUsesStaleCacheOnApiFailure() {
        given(stationReader.getById(557L)).willReturn(StationEntity(id = 557L, name = "강남"))
        given(redisClient.get("STATION_WEATHER_BRIEF:FRESH")).willReturn(null)
        given(
            openMeteoWeatherClient.getCurrentWeather(
                OpenMeteoWeatherDto.Request(
                    latitude = 37.566,
                    longitude = 126.9784,
                    timezone = "Asia/Seoul",
                )
            )
        ).willThrow(RuntimeException("timeout"))
        given(redisClient.get("STATION_WEATHER_BRIEF:STALE")).willReturn(
            """
            {
              "generatedAt":"2026-02-25T18:00:00+09:00",
              "temperatureC":7.5,
              "apparentTemperatureC":5.0,
              "precipitationMm":0.0,
              "windSpeedMps":3.0,
              "weatherCode":2,
              "weatherLabel":"구름 조금",
              "isDay":0
            }
            """.trimIndent()
        )

        val result = stationWeatherService.getStationWeatherBrief(
            GetStationWeatherBriefCommand(stationId = 557L)
        )

        assertThat(result.dataSource).isEqualTo(GetStationWeatherBriefDto.WeatherDataSource.STALE_CACHE)
        assertThat(result.isStale).isTrue()
        assertThat(result.weatherLabel).isEqualTo("구름 조금")
    }

    @Test
    @DisplayName("외부 API와 stale 캐시가 모두 실패하면 fallback 응답을 반환한다")
    fun getStationWeatherBriefUsesFallback() {
        given(stationReader.getById(557L)).willReturn(StationEntity(id = 557L, name = "강남"))
        given(redisClient.get("STATION_WEATHER_BRIEF:FRESH")).willReturn(null)
        given(redisClient.get("STATION_WEATHER_BRIEF:STALE")).willReturn(null)
        given(
            openMeteoWeatherClient.getCurrentWeather(
                OpenMeteoWeatherDto.Request(
                    latitude = 37.566,
                    longitude = 126.9784,
                    timezone = "Asia/Seoul",
                )
            )
        ).willThrow(RuntimeException("timeout"))

        val result = stationWeatherService.getStationWeatherBrief(
            GetStationWeatherBriefCommand(stationId = 557L)
        )

        assertThat(result.dataSource).isEqualTo(GetStationWeatherBriefDto.WeatherDataSource.FALLBACK)
        assertThat(result.isStale).isTrue()
        assertThat(result.summaryText).contains("확인")
    }
}
