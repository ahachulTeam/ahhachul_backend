package backend.team.ahachul_backend.api.train.adapter.`in`

import backend.team.ahachul_backend.api.train.adapter.`in`.dto.GetCongestionDto
import backend.team.ahachul_backend.api.train.adapter.`in`.dto.GetTrainDto
import backend.team.ahachul_backend.api.train.adapter.`in`.dto.GetTrainRealTimesDto
import backend.team.ahachul_backend.api.train.application.port.`in`.TrainUseCase
import backend.team.ahachul_backend.api.train.domain.model.Congestion
import backend.team.ahachul_backend.api.train.domain.model.TrainArrivalCode
import backend.team.ahachul_backend.api.train.domain.model.UpDownType
import backend.team.ahachul_backend.config.controller.CommonDocsTestConfig
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyLong
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
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(TrainController::class)
class TrainControllerDocsTest : CommonDocsTestConfig() {

    @MockBean
    lateinit var trainUseCase: TrainUseCase

    @Test
    fun getTrainTest() {
        // given
        val response = GetTrainDto.Response(
            id = 1L,
            GetTrainDto.SubwayLine(1L, "1호선"),
            location = 3,
            organizationTrainNo = "52"
        )

        given(trainUseCase.getTrain(any()))
            .willReturn(response)

        // when
        val result = mockMvc.perform(
            get("/v1/trains/{trainNo}", 1)
                .header("Authorization", "Bearer <Access Token>")
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(
                document(
                    "get-train",
                    getDocsRequest(),
                    getDocsResponse(),
                    pathParameters(
                        parameterWithName("trainNo").description("열차 번호")
                    ),
                    requestHeaders(
                        headerWithName("Authorization").description("엑세스 토큰")
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.id").type(JsonFieldType.NUMBER).description("열차 식별 번호"),
                        fieldWithPath("result.subwayLine.id").type(JsonFieldType.NUMBER).description("노선 식별 번호"),
                        fieldWithPath("result.subwayLine.name").type(JsonFieldType.STRING).description("노선 이름"),
                        fieldWithPath("result.location").type(JsonFieldType.NUMBER).description("열차 내 현재 위치"),
                        fieldWithPath("result.organizationTrainNo").type(JsonFieldType.STRING).description("열차 편대 번호"),
                    )
                )
            )
    }

    @Test
    fun getTrainCongestionTest() {
        // given
        val response = GetCongestionDto.Response(
            trainNo = "2023",
            congestions =
            listOf(
                GetCongestionDto.Section(
                    sectionNo = 1,
                    congestionColor = Congestion.CONGESTED.name),
                GetCongestionDto.Section(
                    sectionNo = 2,
                    congestionColor = Congestion.MODERATE.name)
            )
        )

        given(trainUseCase.getTrainCongestion(any()))
            .willReturn(response)

        // when
        val result = mockMvc.perform(
            get("/v1/trains/real-times/congestion")
                .queryParam("subwayLineId", "2")
                .queryParam("trainNo", "2023")
                .header("Authorization", "Bearer <Access Token>")
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(
                document(
                    "get-train-congestion",
                    getDocsRequest(),
                    getDocsResponse(),
                    queryParameters(
                        parameterWithName("subwayLineId").description("지하철 고유 번호"),
                        parameterWithName("trainNo").description("열차 번호"),
                    ),
                    requestHeaders(
                        headerWithName("Authorization").description("엑세스 토큰")
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.trainNo").type(JsonFieldType.STRING).description("열차 식별 번호"),
                        fieldWithPath("result.congestions[]").type(JsonFieldType.ARRAY).description("열차 혼잡도 배열"),
                        fieldWithPath("result.congestions[].sectionNo").type(JsonFieldType.NUMBER).description("칸 번호"),
                        fieldWithPath("result.congestions[].congestionColor").type(JsonFieldType.STRING).description("칸 혼잡도 정도")
                            .attributes(getFormatAttribute("SMOOTH(원활), MODERATE(보통), CONGESTED(혼잡), VERY_CONGESTED(매우 혼잡)"))
                    )
                )
            )
    }

    @Test
    fun getTrainRealTimesTest() {
        // given
        val trainRealTimes = listOf(
            GetTrainRealTimesDto.TrainRealTime(
                subwayId = "",
                stationOrder = 1,
                upDownType = UpDownType.UP,
                nextStationDirection = "신대방방면",
                destinationStationDirection = "성수행",
                trainNum = "2234",
                currentArrivalTime = 0,
                currentTrainArrivalCode = TrainArrivalCode.BEFORE_STATION_ARRIVE
            ),
            GetTrainRealTimesDto.TrainRealTime(
                subwayId = "",
                stationOrder = 1,
                upDownType = UpDownType.UP,
                nextStationDirection = "봉천방면",
                destinationStationDirection = "성수행",
                trainNum = "2236",
                currentArrivalTime = 6,
                currentTrainArrivalCode = TrainArrivalCode.RUNNING
            )
        )

        given(trainUseCase.getTrainRealTimes(anyLong(), anyLong(), any()))
            .willReturn(trainRealTimes)

        // when
        val result = mockMvc.perform(
            get("/v1/trains/real-times")
                .queryParam("stationId", "1")
                .queryParam("subwayLineId", "1")
                .queryParam("upDownType", "UP")
                .header("Authorization", "Bearer <Access Token>")
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(
                document(
                    "get-train-real-times",
                    getDocsRequest(),
                    getDocsResponse(),
                    requestHeaders(
                        headerWithName("Authorization").description("엑세스 토큰")
                    ),
                    queryParameters(
                        parameterWithName("stationId").description("정류장 ID"),
                        parameterWithName("subwayLineId").description("지하철 노선 ID"),
                        parameterWithName("upDownType").description("상행(UP), 하행(DOWN)").optional()
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.trainRealTimes[]").type(JsonFieldType.ARRAY).description("해당 정류장 실시간 열차 정보 리스트"),
                        fieldWithPath("result.trainRealTimes[].upDownType").type("UpDownType").description("상하행선구분").attributes(getFormatAttribute("UP, DOWN")),
                        fieldWithPath("result.trainRealTimes[].nextStationDirection").type(JsonFieldType.STRING).description("다음 정류장 방향"),
                        fieldWithPath("result.trainRealTimes[].destinationStationDirection").type(JsonFieldType.STRING).description("목적지 방향"),
                        fieldWithPath("result.trainRealTimes[].trainNum").type(JsonFieldType.STRING).description("해당 열차 ID"),
                        fieldWithPath("result.trainRealTimes[].currentArrivalTime").type(JsonFieldType.NUMBER).description("열차 도착 시간(몇분 후)"),
                        fieldWithPath("result.trainRealTimes[].currentTrainArrivalCode").type(JsonFieldType.STRING).description("해당 열차 현재 위치 코드").attributes(getFormatAttribute("ENTER(진입), ARRIVE(도착), DEPARTURE(출발), BEFORE_STATION_DEPARTURE(전역출발), BEFORE_STATION_ENTER(전역진입), BEFORE_STATION_ARRIVE(전역도착), RUNNING(운행중)")),
                    )
                )
            )
    }
}
