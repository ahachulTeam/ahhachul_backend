package backend.team.ahachul_backend.api.rank.adapter.web.`in`

import backend.team.ahachul_backend.api.rank.adapter.web.`in`.dto.GetHashTagRankDto
import backend.team.ahachul_backend.api.rank.application.service.HashTagRankService
import backend.team.ahachul_backend.config.controller.CommonDocsTestConfig
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(HashTagRankController::class)
class HashTagRankControllerTest: CommonDocsTestConfig() {

    @MockBean
    lateinit var hashTagRankService: HashTagRankService

    @Test
    fun getHashTagRank() {
        // given
        val response = GetHashTagRankDto.Response(
            ranks = listOf("1호선 만찢남", "2호선 빌런")
        )

        given(hashTagRankService.getRank()).willReturn(response)

        // when
        val result = mockMvc.perform(
            get("/v1/ranks/hashtags")
                .accept(MediaType.APPLICATION_JSON))

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(
                MockMvcRestDocumentation.document(
                    "get-hashtag-ranks",
                    getDocsRequest(),
                    getDocsResponse(),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.ranks").type(JsonFieldType.ARRAY).description("인기 해시태그 랭킹 배열")
                    )
                )
            )
    }
}
