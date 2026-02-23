package backend.team.ahachul_backend.api.station.adapter.`in`

import backend.team.ahachul_backend.api.station.adapter.`in`.dto.GetStationTimesDto
import backend.team.ahachul_backend.api.station.adapter.`in`.dto.StationTimeWeekType
import backend.team.ahachul_backend.api.station.application.port.`in`.StationUseCase
import backend.team.ahachul_backend.api.train.domain.model.TrainType
import backend.team.ahachul_backend.api.train.domain.model.UpDownType
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

@WebMvcTest(StationController::class)
class StationControllerDocsTest : CommonDocsTestConfig() {

    @MockBean
    lateinit var stationUseCase: StationUseCase

    @Test
    fun getStationTimes() {
        //given
        val response = GetStationTimesDto.Response(
            listOf(
                GetStationTimesDto.StationTimes(
                    arrivalTime = "00:00:00",
                    departureTime = "06:30:00",
                    arrivalStationName = "대화",
                    departureStationName = "수서",
                    trainType = TrainType.GENERAL
                ),
                GetStationTimesDto.StationTimes(
                    arrivalTime = "06:35:00",
                    departureTime = "06:35:30",
                    arrivalStationName = "구파발",
                    departureStationName = "오금",
                    trainType = TrainType.GENERAL
                )
            )
        )

        given(stationUseCase.getStationTimes(any()))
            .willReturn(response)

        //when
        val result = mockMvc.perform(
            get("/v1/stations/times")
                .queryParam("stationId", 622.toString())
                .queryParam("subwayLineId", 3.toString())
                .queryParam("upDownType", UpDownType.UP.name)
                .queryParam("stationTimeWeekType", StationTimeWeekType.WEEKDAY.name)
                .accept(MediaType.APPLICATION_JSON)
        )

        //then
        result.andExpect(status().isOk)
            .andDo(
                document(
                    "get-station-times",
                    getDocsRequest(),
                    getDocsResponse(),
                    queryParameters(
                        parameterWithName("stationId").description("정류장 ID"),
                        parameterWithName("subwayLineId").description("지하철 노선 ID"),
                        parameterWithName("upDownType").description("상행(UP), 하행(DOWN)"),
                        parameterWithName("stationTimeWeekType").description("평일(WEEKDAY), 토요일(SATURDAY), 공휴일(HOLIDAY)\n")
                    ),
                    PayloadDocumentation.responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.stationTimes[].arrivalTime").type(JsonFieldType.STRING).description("도착시간 - hh:mm:ss"),
                        fieldWithPath("result.stationTimes[].departureTime").type(JsonFieldType.STRING).description("출발시간 - hh:mm:ss"),
                        fieldWithPath("result.stationTimes[].arrivalStationName").type(JsonFieldType.STRING).description("도착역명"),
                        fieldWithPath("result.stationTimes[].departureStationName").type(JsonFieldType.STRING).description("출발역명"),
                        fieldWithPath("result.stationTimes[].trainType").type(JsonFieldType.STRING).description("급행(EXPRESS), 일반(GENERAL)"),
                    )
                )
            )
    }

    @Test
    fun getStationTimesSummary() {
        // given
        val response = GetStationTimesDto.SummaryResponse(
            stationTimeWeekType = StationTimeWeekType.WEEKDAY,
            summaries = listOf(
                GetStationTimesDto.UpDownSummary(
                    upDownType = UpDownType.UP,
                    firstDepartureTime = "05:31:00",
                    lastDepartureTime = "23:58:00",
                    firstDestinationStationName = "대화",
                    lastDestinationStationName = "오금",
                ),
                GetStationTimesDto.UpDownSummary(
                    upDownType = UpDownType.DOWN,
                    firstDepartureTime = "05:36:00",
                    lastDepartureTime = "23:54:00",
                    firstDestinationStationName = "수서",
                    lastDestinationStationName = "구파발",
                )
            ),
        )

        given(stationUseCase.getStationTimesSummary(any()))
            .willReturn(response)

        // when
        val result = mockMvc.perform(
            get("/v2/stations/times/summary")
                .queryParam("stationId", 622.toString())
                .queryParam("subwayLineId", 3.toString())
                .queryParam("stationTimeWeekType", StationTimeWeekType.WEEKDAY.name)
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
            .andDo(
                document(
                    "get-station-times-summary",
                    getDocsRequest(),
                    getDocsResponse(),
                    queryParameters(
                        parameterWithName("stationId").description("정류장 ID"),
                        parameterWithName("subwayLineId").description("지하철 노선 ID"),
                        parameterWithName("stationTimeWeekType").description("평일(WEEKDAY), 토요일(SATURDAY), 공휴일(HOLIDAY)")
                    ),
                    PayloadDocumentation.responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.stationTimeWeekType").type(JsonFieldType.STRING).description("요일 구분"),
                        fieldWithPath("result.summaries[].upDownType").type(JsonFieldType.STRING).description("상행(UP), 하행(DOWN)"),
                        fieldWithPath("result.summaries[].firstDepartureTime").type(JsonFieldType.STRING).optional().description("첫차 출발시간 - hh:mm:ss"),
                        fieldWithPath("result.summaries[].lastDepartureTime").type(JsonFieldType.STRING).optional().description("막차 출발시간 - hh:mm:ss"),
                        fieldWithPath("result.summaries[].firstDestinationStationName").type(JsonFieldType.STRING).optional().description("첫차 종착역명"),
                        fieldWithPath("result.summaries[].lastDestinationStationName").type(JsonFieldType.STRING).optional().description("막차 종착역명"),
                    )
                )
            )
    }
}
