package backend.team.ahachul_backend.api.station.adapter.`in`

import backend.team.ahachul_backend.api.station.adapter.`in`.dto.GetStationNearbyPlacesDto
import backend.team.ahachul_backend.api.station.application.port.`in`.StationNearbyPlacesUseCase
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

@WebMvcTest(StationNearbyPlacesController::class)
class StationNearbyPlacesControllerDocsTest : CommonDocsTestConfig() {

    @MockBean
    lateinit var stationNearbyPlacesUseCase: StationNearbyPlacesUseCase

    @Test
    fun getNearbyPlaces() {
        val response = GetStationNearbyPlacesDto.Response(
            stationId = 622,
            subwayLineId = 3,
            exitNo = "3",
            places = listOf(
                GetStationNearbyPlacesDto.Place(
                    name = "안암 김밥스테이션",
                    category = "분식",
                    walkingMinutes = 4,
                    openNow = true,
                    supportsEnglishMenu = true,
                    confidenceLevel = GetStationNearbyPlacesDto.NearbyPlaceConfidenceLevel.MEDIUM,
                ),
                GetStationNearbyPlacesDto.Place(
                    name = "한밤 편의마트",
                    category = "편의점",
                    walkingMinutes = 3,
                    openNow = true,
                    supportsEnglishMenu = false,
                    confidenceLevel = GetStationNearbyPlacesDto.NearbyPlaceConfidenceLevel.HIGH,
                ),
            ),
        )

        given(stationNearbyPlacesUseCase.getNearbyPlaces(any()))
            .willReturn(response)

        val result = mockMvc.perform(
            get("/v2/stations/nearby-places")
                .queryParam("stationId", 622.toString())
                .queryParam("subwayLineId", 3.toString())
                .queryParam("exitNo", "3")
                .queryParam("limit", "3")
                .accept(MediaType.APPLICATION_JSON)
        )

        result.andExpect(status().isOk)
            .andDo(
                document(
                    "get-station-nearby-places",
                    getDocsRequest(),
                    getDocsResponse(),
                    queryParameters(
                        parameterWithName("stationId").description("정류장 ID"),
                        parameterWithName("subwayLineId").description("지하철 노선 ID"),
                        parameterWithName("exitNo").optional().description("출구 번호"),
                        parameterWithName("limit").optional().description("최대 반환 개수(1~5)"),
                    ),
                    PayloadDocumentation.responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.stationId").type(JsonFieldType.NUMBER).description("정류장 ID"),
                        fieldWithPath("result.subwayLineId").type(JsonFieldType.NUMBER).description("지하철 노선 ID"),
                        fieldWithPath("result.exitNo").type(JsonFieldType.STRING).optional().description("출구 번호"),
                        fieldWithPath("result.places[].name").type(JsonFieldType.STRING).description("장소명"),
                        fieldWithPath("result.places[].category").type(JsonFieldType.STRING).description("카테고리"),
                        fieldWithPath("result.places[].walkingMinutes").type(JsonFieldType.NUMBER).description("도보 시간(분)"),
                        fieldWithPath("result.places[].openNow").type(JsonFieldType.BOOLEAN).description("영업 중 여부"),
                        fieldWithPath("result.places[].supportsEnglishMenu").type(JsonFieldType.BOOLEAN).description("영문 메뉴 가능 여부"),
                        fieldWithPath("result.places[].confidenceLevel").type(JsonFieldType.STRING).description("신뢰도(HIGH/MEDIUM/LOW)"),
                    )
                )
            )
    }
}
