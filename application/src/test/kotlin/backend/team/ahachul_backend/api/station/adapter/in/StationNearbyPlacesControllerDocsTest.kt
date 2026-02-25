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
            generatedAt = "2026-02-26T09:30:00+09:00",
            stationId = 622,
            subwayLineId = 3,
            exitNo = "3",
            summary = "편의점/화장실/ATM/늦은 식당 중심으로 신뢰도 기반 추천을 제공합니다.",
            places = listOf(
                GetStationNearbyPlacesDto.Place(
                    name = "안암역 3번출구 공중화장실",
                    category = "화장실",
                    essentialType = GetStationNearbyPlacesDto.NearbyEssentialType.RESTROOM,
                    walkingMinutes = 4,
                    openNow = true,
                    supportsEnglishMenu = false,
                    confidenceLevel = GetStationNearbyPlacesDto.NearbyPlaceConfidenceLevel.MEDIUM,
                    reliabilityScore = 82,
                    reliabilityReason = "역사 시설 데이터 + 최근 점검 로그",
                    sourceCount = 6,
                    lastVerifiedAt = "2026-02-25T08:35:00+09:00",
                ),
                GetStationNearbyPlacesDto.Place(
                    name = "한밤 편의마트",
                    category = "편의점",
                    essentialType = GetStationNearbyPlacesDto.NearbyEssentialType.CONVENIENCE_STORE,
                    walkingMinutes = 3,
                    openNow = true,
                    supportsEnglishMenu = false,
                    confidenceLevel = GetStationNearbyPlacesDto.NearbyPlaceConfidenceLevel.HIGH,
                    reliabilityScore = 90,
                    reliabilityReason = "최근 7일 사용자 확인 + 운영시간 일치",
                    sourceCount = 12,
                    lastVerifiedAt = "2026-02-25T21:10:00+09:00",
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
                        parameterWithName("limit").optional().description("최대 반환 개수(1~6)"),
                    ),
                    PayloadDocumentation.responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.generatedAt").type(JsonFieldType.STRING).description("응답 생성 시각"),
                        fieldWithPath("result.stationId").type(JsonFieldType.NUMBER).description("정류장 ID"),
                        fieldWithPath("result.subwayLineId").type(JsonFieldType.NUMBER).description("지하철 노선 ID"),
                        fieldWithPath("result.exitNo").type(JsonFieldType.STRING).optional().description("출구 번호"),
                        fieldWithPath("result.summary").type(JsonFieldType.STRING).description("주변 정보 요약"),
                        fieldWithPath("result.places[].name").type(JsonFieldType.STRING).description("장소명"),
                        fieldWithPath("result.places[].category").type(JsonFieldType.STRING).description("카테고리"),
                        fieldWithPath("result.places[].essentialType").type(JsonFieldType.STRING).description("필수 정보 카테고리(CONVENIENCE_STORE/RESTROOM/ATM/LATE_NIGHT_FOOD)"),
                        fieldWithPath("result.places[].walkingMinutes").type(JsonFieldType.NUMBER).description("도보 시간(분)"),
                        fieldWithPath("result.places[].openNow").type(JsonFieldType.BOOLEAN).description("영업 중 여부"),
                        fieldWithPath("result.places[].supportsEnglishMenu").type(JsonFieldType.BOOLEAN).description("영문 메뉴 가능 여부"),
                        fieldWithPath("result.places[].confidenceLevel").type(JsonFieldType.STRING).description("신뢰도(HIGH/MEDIUM/LOW)"),
                        fieldWithPath("result.places[].reliabilityScore").type(JsonFieldType.NUMBER).description("신뢰도 점수(0~100)"),
                        fieldWithPath("result.places[].reliabilityReason").type(JsonFieldType.STRING).description("신뢰도 근거"),
                        fieldWithPath("result.places[].sourceCount").type(JsonFieldType.NUMBER).description("신뢰도 산정 소스 개수"),
                        fieldWithPath("result.places[].lastVerifiedAt").type(JsonFieldType.STRING).description("마지막 검증 시각"),
                    )
                )
            )
    }
}
