package backend.team.ahachul_backend.api.station.adapter.`in`

import backend.team.ahachul_backend.api.station.adapter.`in`.dto.GetStationWeatherBriefDto
import backend.team.ahachul_backend.api.station.application.port.`in`.StationWeatherUseCase
import backend.team.ahachul_backend.config.controller.CommonDocsTestConfig
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.queryParameters
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(StationWeatherController::class)
class StationWeatherControllerDocsTest : CommonDocsTestConfig() {

    @MockBean
    lateinit var stationWeatherUseCase: StationWeatherUseCase

    @Test
    fun getStationWeatherBrief() {
        val response = GetStationWeatherBriefDto.Response(
            stationId = 557L,
            stationName = "강남",
            generatedAt = "2026-02-25T20:00:00+09:00",
            dataSource = GetStationWeatherBriefDto.WeatherDataSource.API,
            isStale = false,
            summaryText = "현재 대체로 맑음, 9°C",
            cautionText = "일교차가 커요. 얇은 겉옷을 챙기면 좋아요.",
            friendlyText = "오늘은 날씨가 화창합니다. 좋은 하루 되세요.",
            temperatureC = 9.2,
            apparentTemperatureC = 7.1,
            precipitationMm = 0.0,
            windSpeedMps = 2.4,
            weatherCode = 1,
            weatherLabel = "대체로 맑음",
        )

        given(stationWeatherUseCase.getStationWeatherBrief(any()))
            .willReturn(response)

        val result = mockMvc.perform(
            get("/v2/stations/weather/brief")
                .queryParam("stationId", "557")
                .accept(MediaType.APPLICATION_JSON)
        )

        result.andExpect(status().isOk)
            .andDo(
                document(
                    "get-station-weather-brief",
                    getDocsRequest(),
                    getDocsResponse(),
                    queryParameters(
                        parameterWithName("stationId").description("정류장 ID"),
                    ),
                    PayloadDocumentation.responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.stationId").type(JsonFieldType.NUMBER).description("정류장 ID"),
                        fieldWithPath("result.stationName").type(JsonFieldType.STRING).description("정류장 이름"),
                        fieldWithPath("result.generatedAt").type(JsonFieldType.STRING).description("생성 시각"),
                        fieldWithPath("result.dataSource").type(JsonFieldType.STRING).description("데이터 소스(API/CACHE/STALE_CACHE/FALLBACK)"),
                        fieldWithPath("result.isStale").type(JsonFieldType.BOOLEAN).description("지연 데이터 여부"),
                        fieldWithPath("result.summaryText").type(JsonFieldType.STRING).description("요약 날씨 문구"),
                        fieldWithPath("result.cautionText").type(JsonFieldType.STRING).description("주의 안내 문구"),
                        fieldWithPath("result.friendlyText").type(JsonFieldType.STRING).description("친화 문구"),
                        fieldWithPath("result.temperatureC").type(JsonFieldType.NUMBER).optional().description("현재 기온(°C)"),
                        fieldWithPath("result.apparentTemperatureC").type(JsonFieldType.NUMBER).optional().description("체감 기온(°C)"),
                        fieldWithPath("result.precipitationMm").type(JsonFieldType.NUMBER).optional().description("현재 강수량(mm)"),
                        fieldWithPath("result.windSpeedMps").type(JsonFieldType.NUMBER).optional().description("풍속(m/s)"),
                        fieldWithPath("result.weatherCode").type(JsonFieldType.NUMBER).optional().description("WMO 날씨 코드"),
                        fieldWithPath("result.weatherLabel").type(JsonFieldType.STRING).description("표시용 날씨 라벨"),
                    )
                )
            )
    }
}
