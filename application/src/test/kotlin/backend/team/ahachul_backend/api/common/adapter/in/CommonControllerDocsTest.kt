package backend.team.ahachul_backend.api.common.adapter.`in`

import backend.team.ahachul_backend.api.common.adapter.`in`.dto.SearchSubwayLineDto
import backend.team.ahachul_backend.api.common.adapter.`in`.dto.Station
import backend.team.ahachul_backend.api.common.adapter.`in`.dto.SubwayLine
import backend.team.ahachul_backend.api.common.application.port.`in`.SubwayLineUseCase
import backend.team.ahachul_backend.config.controller.CommonDocsTestConfig
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(CommonController::class)
class CommonControllerDocsTest: CommonDocsTestConfig() {

    @MockBean
    lateinit var subwayLineUseCase: SubwayLineUseCase

    @Test
    fun searchSubwayLines() {
        // given
        val station1 = Station(
            id = 1,
            name = "인천역"
        )

        val station2 = Station(
            id = 3,
            name = "신림역"
        )

        val response = SearchSubwayLineDto.Response(
            arrayListOf(
                SubwayLine(
                    id = 1L,
                    name = "1호선",
                    phoneNumber = "02-111-1111",
                    stations = listOf(station1)
                ),
                SubwayLine(
                    id = 2L,
                    name = "2호선",
                    phoneNumber = "02-222-2222",
                    stations = listOf(station2)
                )
            )
        )

        BDDMockito.given(subwayLineUseCase.searchSubwayLines())
            .willReturn(response)

        // when
        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.get("/v1/subway-lines")
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(
                MockMvcRestDocumentation.document(
                    "search-subway-lines",
                    getDocsRequest(),
                    getDocsResponse(),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.subwayLines[]").type(JsonFieldType.ARRAY).description("노선 리스트"),
                        fieldWithPath("result.subwayLines[].id").type(JsonFieldType.NUMBER).description("노선 ID"),
                        fieldWithPath("result.subwayLines[].name").type(JsonFieldType.STRING).description("노선 이름"),
                        fieldWithPath("result.subwayLines[].phoneNumber").type(JsonFieldType.STRING).description("노선 전화 번호"),
                        fieldWithPath("result.subwayLines[].stations[]").type(JsonFieldType.ARRAY).description("역 정보 배열"),
                        fieldWithPath("result.subwayLines[].stations[].id").type(JsonFieldType.NUMBER).description("각 노선에 존재하는 역 ID"),
                        fieldWithPath("result.subwayLines[].stations[].name").type(JsonFieldType.STRING).description("각 노선에 존재하는 역 이름"),
                    )
                )
            )
    }
}
