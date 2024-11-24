package backend.team.ahachul_backend.api.comment.adapter.web.`in`

import backend.team.ahachul_backend.api.comment.adapter.web.`in`.dto.DeleteCommentDto
import backend.team.ahachul_backend.api.comment.adapter.web.`in`.dto.UpdateCommentDto
import backend.team.ahachul_backend.api.comment.application.port.`in`.CommentUseCase
import backend.team.ahachul_backend.config.controller.CommonDocsTestConfig
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(CommentController::class)
class CommentControllerDocsTest : CommonDocsTestConfig() {

    @MockBean
    lateinit var commentUseCase: CommentUseCase

    @Test
    fun updateCommentTest() {
        // given
        val response = UpdateCommentDto.Response(
            id = 1,
            content = "변경된 코멘트 내용"
        )

        given(commentUseCase.updateComment(any()))
            .willReturn(response)

        val request = UpdateCommentDto.Request(
            content = "변경할 코멘트 내용"
        )

        // when
        val result = mockMvc.perform(
            patch("/v1/comments/{commentId}", 1)
                .header("Authorization", "Bearer <Access Token>")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
            .andDo(
                document(
                    "update-comment",
                    getDocsRequest(),
                    getDocsResponse(),
                    requestHeaders(
                        headerWithName("Authorization").description("엑세스 토큰")
                    ),
                    RequestDocumentation.pathParameters(
                        parameterWithName("commentId").description("변경할 코멘트 아이디")
                    ),
                    PayloadDocumentation.requestFields(
                        fieldWithPath("content").description("변경할 내용"),
                    ),
                    PayloadDocumentation.responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.id").type(JsonFieldType.NUMBER).description("변경된 코멘트 아이디"),
                        fieldWithPath("result.content").type(JsonFieldType.STRING).description("변경된 내용"),
                    )
                )
            )
    }

    @Test
    fun deleteCommentTest() {
        // given
        val response = DeleteCommentDto.Response(
            id = 1
        )

        given(commentUseCase.deleteComment(any()))
            .willReturn(response)

        // when
        val result = mockMvc.perform(
            delete("/v1/comments/{commentId}", 1)
                .header("Authorization", "Bearer <Access Token>")
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
            .andDo(
                document(
                    "delete-comment",
                    getDocsRequest(),
                    getDocsResponse(),
                    requestHeaders(
                        headerWithName("Authorization").description("엑세스 토큰")
                    ),
                    RequestDocumentation.pathParameters(
                        parameterWithName("commentId").description("삭제할 코멘트 아이디")
                    ),
                    PayloadDocumentation.responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.id").type(JsonFieldType.NUMBER).description("삭제된 코멘트 아이디"),
                    )
                )
            )
    }
}
