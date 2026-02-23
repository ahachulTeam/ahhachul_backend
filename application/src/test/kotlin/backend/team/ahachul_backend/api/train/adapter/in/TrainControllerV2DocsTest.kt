package backend.team.ahachul_backend.api.train.adapter.`in`

import backend.team.ahachul_backend.api.train.adapter.`in`.dto.GetTrainRealTimesV2Dto
import backend.team.ahachul_backend.api.train.application.port.`in`.TrainUseCase
import backend.team.ahachul_backend.api.train.domain.model.UpDownType
import backend.team.ahachul_backend.config.controller.CommonDocsTestConfig
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.queryParameters
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(TrainController::class)
class TrainControllerV2DocsTest : CommonDocsTestConfig() {

    @MockBean
    lateinit var trainUseCase: TrainUseCase

    @Test
    fun getTrainRealTimesV2Test() {
        val response = GetTrainRealTimesV2Dto.Response(
            generatedAt = "2026-02-23T22:00:00+09:00",
            dataSource = "API",
            isStale = false,
            lastExternalRecptnAt = "2026-02-23T21:59:45+09:00",
            freshnessSec = 15,
            confidenceLevel = "HIGH",
            trainRealTimes = listOf(
                GetTrainRealTimesV2Dto.TrainRealTimeV2(
                    trainNo = "2234",
                    upDownType = UpDownType.UP,
                    arrivalCode = "BEFORE_STATION_ARRIVE",
                    etaSec = 75,
                    etaMinDisplay = 2,
                    destinationStationDirection = "성수행",
                    nextStationDirection = "신대방방면",
                )
            ),
        )

        given(trainUseCase.getTrainRealTimesV2(1L, 1L, UpDownType.UP, 2))
            .willReturn(response)

        val result = mockMvc.perform(
            get("/v2/trains/real-times")
                .queryParam("stationId", "1")
                .queryParam("subwayLineId", "1")
                .queryParam("upDownType", "UP")
                .queryParam("limit", "2")
                .header("Authorization", "Bearer <Access Token>")
                .accept(MediaType.APPLICATION_JSON)
        )

        result.andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(
                document(
                    "get-train-real-times-v2",
                    getDocsRequest(),
                    getDocsResponse(),
                    requestHeaders(
                        headerWithName("Authorization").description("엑세스 토큰")
                    ),
                    queryParameters(
                        parameterWithName("stationId").description("정류장 ID"),
                        parameterWithName("subwayLineId").description("지하철 노선 ID"),
                        parameterWithName("upDownType").description("상행(UP), 하행(DOWN)").optional(),
                        parameterWithName("limit").description("응답 개수 제한(기본 2, 최대 4)").optional(),
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.generatedAt").type(JsonFieldType.STRING).description("응답 생성 시각"),
                        fieldWithPath("result.dataSource").type(JsonFieldType.STRING).description("데이터 소스(API/STALE_CACHE)"),
                        fieldWithPath("result.isStale").type(JsonFieldType.BOOLEAN).description("스테일 데이터 여부"),
                        fieldWithPath("result.lastExternalRecptnAt").type(JsonFieldType.STRING).description("외부 수신 시각"),
                        fieldWithPath("result.freshnessSec").type(JsonFieldType.NUMBER).description("데이터 신선도(초)"),
                        fieldWithPath("result.confidenceLevel").type(JsonFieldType.STRING).description("신뢰도(HIGH/MEDIUM/LOW)"),
                        fieldWithPath("result.trainRealTimes[]").type(JsonFieldType.ARRAY).description("열차 도착 목록"),
                        fieldWithPath("result.trainRealTimes[].trainNo").type(JsonFieldType.STRING).description("열차 번호"),
                        fieldWithPath("result.trainRealTimes[].upDownType").type(JsonFieldType.STRING).description("상하행 구분"),
                        fieldWithPath("result.trainRealTimes[].arrivalCode").type(JsonFieldType.STRING).description("도착 코드"),
                        fieldWithPath("result.trainRealTimes[].etaSec").type(JsonFieldType.NUMBER).description("예상 도착 초"),
                        fieldWithPath("result.trainRealTimes[].etaMinDisplay").type(JsonFieldType.NUMBER).description("예상 도착 분(표시용)"),
                        fieldWithPath("result.trainRealTimes[].destinationStationDirection").type(JsonFieldType.STRING).description("목적지 방향"),
                        fieldWithPath("result.trainRealTimes[].nextStationDirection").type(JsonFieldType.STRING).description("다음 역 방향"),
                    )
                )
            )
    }
}
