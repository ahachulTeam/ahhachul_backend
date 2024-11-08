package backend.team.ahachul_backend.api.report.adpater.`in`

import backend.team.ahachul_backend.api.report.adpater.`in`.dto.CreateReportDto
import backend.team.ahachul_backend.api.report.application.port.`in`.ReportUseCase
import backend.team.ahachul_backend.config.controller.CommonDocsTestConfig
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.anyLong
import org.mockito.BDDMockito.given
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(ReportController::class)
class ReportControllerTest: CommonDocsTestConfig() {

    @MockBean(name = "communityPostReportService")
    lateinit var communityPostReportService: ReportUseCase

    @MockBean(name = "lostPostReportService")
    lateinit var lostPostReportService: ReportUseCase

    @Test
    fun saveReportCommunityPost() {
        // given
        val response = CreateReportDto.Response(
            id = 1,
            sourceMemberId = 1,
            targetMemberId = 2,
            targetId = 3
        )

        // when
        given(communityPostReportService.save(anyLong()))
            .willReturn(response)

        // then
        val result = mockMvc.perform(
            post("/v1/reports/community-post/{postId}", 1)
                .accept(MediaType.APPLICATION_JSON)
        )

        result.andExpect(status().isOk)
            .andDo(
                document(
                    "save-report-community-post",
                    getDocsRequest(),
                    getDocsResponse(),
                    pathParameters(
                        parameterWithName("postId").description("신고 대상 커뮤니티 게시물 아이디")
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.id").type(JsonFieldType.NUMBER).description("신고 아이디"),
                        fieldWithPath("result.sourceMemberId").type(JsonFieldType.NUMBER).description("신고한 유저 아이디"),
                        fieldWithPath("result.targetMemberId").type(JsonFieldType.NUMBER).description("신고 받은 유저 아이디"),
                        fieldWithPath("result.targetId").type(JsonFieldType.NUMBER).description("신고 대상 커뮤니티 게시물 아이디"),
                    )
            ))
    }

    @Test
    fun saveReportLostPost() {
        val response = CreateReportDto.Response(
            id = 1,
            sourceMemberId = 1,
            targetMemberId = 2,
            targetId = 3
        )

        // when
        given(lostPostReportService.save(anyLong()))
            .willReturn(response)

        // then
        val result = mockMvc.perform(
            post("/v1/reports/lost-post/{postId}", 1)
                .accept(MediaType.APPLICATION_JSON)
        )

        result.andExpect(status().isOk)
            .andDo(
                document(
                    "save-report-lost-post",
                    getDocsRequest(),
                    getDocsResponse(),
                    pathParameters(
                        parameterWithName("postId").description("신고 대상 유실물 게시물 아이디")
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.id").type(JsonFieldType.NUMBER).description("신고 아이디"),
                        fieldWithPath("result.sourceMemberId").type(JsonFieldType.NUMBER).description("신고한 유저 아이디"),
                        fieldWithPath("result.targetMemberId").type(JsonFieldType.NUMBER).description("신고 받은 유저 아이디"),
                        fieldWithPath("result.targetId").type(JsonFieldType.NUMBER).description("신고 대상 유실물 게시물 아이디"),
                    )
                )
            )
    }
}
