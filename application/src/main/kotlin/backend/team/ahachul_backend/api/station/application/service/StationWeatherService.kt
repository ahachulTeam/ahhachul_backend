package backend.team.ahachul_backend.api.station.application.service

import backend.team.ahachul_backend.api.common.application.port.out.StationReader
import backend.team.ahachul_backend.api.station.adapter.`in`.dto.GetStationWeatherBriefDto
import backend.team.ahachul_backend.api.station.application.port.`in`.StationWeatherUseCase
import backend.team.ahachul_backend.api.station.application.port.`in`.dto.GetStationWeatherBriefCommand
import backend.team.ahachul_backend.common.client.OpenMeteoWeatherClient
import backend.team.ahachul_backend.common.client.RedisClient
import backend.team.ahachul_backend.common.client.dto.OpenMeteoWeatherDto
import backend.team.ahachul_backend.common.logging.Logger
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.math.roundToInt

@Service
@Transactional(readOnly = true)
class StationWeatherService(
    private val stationReader: StationReader,
    private val openMeteoWeatherClient: OpenMeteoWeatherClient,
    private val redisClient: RedisClient,
    private val objectMapper: ObjectMapper,
) : StationWeatherUseCase {

    private val logger = Logger(javaClass)

    override fun getStationWeatherBrief(command: GetStationWeatherBriefCommand): GetStationWeatherBriefDto.Response {
        val station = stationReader.getById(command.stationId)
        val loaded = loadWeatherSnapshot()
        val snapshot = loaded.snapshot

        return GetStationWeatherBriefDto.Response(
            stationId = command.stationId,
            stationName = station.name,
            generatedAt = snapshot.generatedAt,
            dataSource = loaded.dataSource,
            isStale = loaded.isStale,
            summaryText = resolveSummaryText(snapshot),
            cautionText = resolveCautionText(snapshot),
            friendlyText = resolveFriendlyText(snapshot),
            temperatureC = snapshot.temperatureC,
            apparentTemperatureC = snapshot.apparentTemperatureC,
            precipitationMm = snapshot.precipitationMm,
            windSpeedMps = snapshot.windSpeedMps,
            weatherCode = snapshot.weatherCode,
            weatherLabel = snapshot.weatherLabel ?: resolveWeatherLabel(snapshot.weatherCode, snapshot.isDay),
        )
    }

    private fun loadWeatherSnapshot(): LoadedWeatherSnapshot {
        getCachedSnapshot(FRESH_CACHE_KEY)?.let { cached ->
            return LoadedWeatherSnapshot(
                snapshot = cached,
                dataSource = GetStationWeatherBriefDto.WeatherDataSource.CACHE,
                isStale = false,
            )
        }

        return try {
            val apiSnapshot = requestWeatherSnapshot()
            cacheSnapshot(FRESH_CACHE_KEY, apiSnapshot, FRESH_CACHE_TTL_SEC)
            cacheSnapshot(STALE_CACHE_KEY, apiSnapshot, STALE_CACHE_TTL_SEC)
            LoadedWeatherSnapshot(
                snapshot = apiSnapshot,
                dataSource = GetStationWeatherBriefDto.WeatherDataSource.API,
                isStale = false,
            )
        } catch (exception: Exception) {
            logger.error("open-meteo weather call failed. fallback to stale/fallback response.", exception)
            getCachedSnapshot(STALE_CACHE_KEY)?.let { stale ->
                return LoadedWeatherSnapshot(
                    snapshot = stale,
                    dataSource = GetStationWeatherBriefDto.WeatherDataSource.STALE_CACHE,
                    isStale = true,
                )
            }

            LoadedWeatherSnapshot(
                snapshot = WeatherSnapshot.fallback(),
                dataSource = GetStationWeatherBriefDto.WeatherDataSource.FALLBACK,
                isStale = true,
            )
        }
    }

    private fun requestWeatherSnapshot(): WeatherSnapshot {
        val response = openMeteoWeatherClient.getCurrentWeather(
            OpenMeteoWeatherDto.Request(
                latitude = DEFAULT_LATITUDE,
                longitude = DEFAULT_LONGITUDE,
                timezone = DEFAULT_TIMEZONE,
            )
        )
        val current = response.current

        return WeatherSnapshot(
            generatedAt = OffsetDateTime.now(ZoneId.of(DEFAULT_TIMEZONE)).toString(),
            temperatureC = current?.temperatureC,
            apparentTemperatureC = current?.apparentTemperatureC,
            precipitationMm = current?.precipitationMm,
            windSpeedMps = current?.windSpeedMps,
            weatherCode = current?.weatherCode,
            weatherLabel = resolveWeatherLabel(current?.weatherCode, current?.isDay),
            isDay = current?.isDay,
        )
    }

    private fun resolveSummaryText(snapshot: WeatherSnapshot): String {
        val weatherLabel = snapshot.weatherLabel ?: resolveWeatherLabel(snapshot.weatherCode, snapshot.isDay)
        val temperature = snapshot.temperatureC?.roundToInt()
        return if (temperature == null) {
            "현재 날씨 정보를 확인 중입니다."
        } else {
            "현재 $weatherLabel, ${temperature}°C"
        }
    }

    private fun resolveCautionText(snapshot: WeatherSnapshot): String {
        val weatherCode = snapshot.weatherCode
        val precipitation = snapshot.precipitationMm ?: 0.0
        val windSpeed = snapshot.windSpeedMps ?: 0.0
        val temperature = snapshot.temperatureC
        val apparentTemperature = snapshot.apparentTemperatureC

        if (isRainOrSnow(weatherCode) || precipitation >= 0.1) {
            return "비나 눈이 올 수 있어요. 우산을 챙기고 미끄럼을 주의하세요."
        }

        if (apparentTemperature != null && apparentTemperature <= 0.0) {
            return "체감온도가 낮아요. 장갑/겉옷을 챙기세요."
        }

        if (windSpeed >= 8.0) {
            return "바람이 강해요. 외투와 소지품 관리에 주의하세요."
        }

        if (temperature != null && apparentTemperature != null && abs(temperature - apparentTemperature) >= 5.0) {
            return "일교차가 커요. 얇은 겉옷을 챙기면 좋아요."
        }

        return "큰 기상 위험은 없어요. 이동 전 열차 정보를 한 번 더 확인하세요."
    }

    private fun resolveFriendlyText(snapshot: WeatherSnapshot): String {
        val weatherCode = snapshot.weatherCode
        val isDay = snapshot.isDay == 1

        if (weatherCode == 0 && isDay) {
            return "오늘은 날씨가 화창합니다. 좋은 하루 되세요."
        }

        if (isRainCode(weatherCode)) {
            return "비 소식이 있어요. 이동 전 우산을 확인해 주세요."
        }

        if (isSnowCode(weatherCode)) {
            return "눈길 미끄럼에 주의하세요. 안전한 하루 되세요."
        }

        return "오늘도 안전하고 편안한 이동 되세요."
    }

    private fun resolveWeatherLabel(weatherCode: Int?, isDay: Int?): String {
        return when (weatherCode) {
            0 -> if (isDay == 1) "맑음" else "맑은 밤"
            1 -> "대체로 맑음"
            2 -> "구름 조금"
            3 -> "흐림"
            45, 48 -> "안개"
            51, 53, 55, 56, 57 -> "이슬비"
            61, 63, 65, 66, 67, 80, 81, 82 -> "비"
            71, 73, 75, 77, 85, 86 -> "눈"
            95, 96, 99 -> "뇌우"
            else -> "기상 정보 확인 중"
        }
    }

    private fun isRainOrSnow(weatherCode: Int?): Boolean {
        return isRainCode(weatherCode) || isSnowCode(weatherCode)
    }

    private fun isRainCode(weatherCode: Int?): Boolean {
        return weatherCode in setOf(51, 53, 55, 56, 57, 61, 63, 65, 66, 67, 80, 81, 82)
    }

    private fun isSnowCode(weatherCode: Int?): Boolean {
        return weatherCode in setOf(71, 73, 75, 77, 85, 86)
    }

    private fun getCachedSnapshot(cacheKey: String): WeatherSnapshot? {
        return runCatching {
            val cached = redisClient.get(cacheKey) ?: return null
            objectMapper.readValue(cached, WeatherSnapshot::class.java)
        }.getOrElse {
            logger.error("weather cache read failed: key=$cacheKey", it)
            null
        }
    }

    private fun cacheSnapshot(cacheKey: String, snapshot: WeatherSnapshot, ttlSeconds: Long) {
        runCatching {
            redisClient.set(cacheKey, snapshot, ttlSeconds, TimeUnit.SECONDS)
        }.onFailure {
            logger.error("weather cache write failed: key=$cacheKey", it)
        }
    }

    private data class LoadedWeatherSnapshot(
        val snapshot: WeatherSnapshot,
        val dataSource: GetStationWeatherBriefDto.WeatherDataSource,
        val isStale: Boolean,
    )

    data class WeatherSnapshot(
        val generatedAt: String,
        val temperatureC: Double?,
        val apparentTemperatureC: Double?,
        val precipitationMm: Double?,
        val windSpeedMps: Double?,
        val weatherCode: Int?,
        val weatherLabel: String?,
        val isDay: Int?,
    ) {
        companion object {
            fun fallback(): WeatherSnapshot {
                return WeatherSnapshot(
                    generatedAt = OffsetDateTime.now(ZoneId.of(DEFAULT_TIMEZONE)).toString(),
                    temperatureC = null,
                    apparentTemperatureC = null,
                    precipitationMm = null,
                    windSpeedMps = null,
                    weatherCode = null,
                    weatherLabel = "기상 정보 확인 중",
                    isDay = null,
                )
            }
        }
    }

    companion object {
        private const val FRESH_CACHE_KEY = "STATION_WEATHER_BRIEF:FRESH"
        private const val STALE_CACHE_KEY = "STATION_WEATHER_BRIEF:STALE"
        private const val FRESH_CACHE_TTL_SEC = 600L
        private const val STALE_CACHE_TTL_SEC = 10800L
        private const val DEFAULT_LATITUDE = 37.566
        private const val DEFAULT_LONGITUDE = 126.9784
        private const val DEFAULT_TIMEZONE = "Asia/Seoul"
    }
}
