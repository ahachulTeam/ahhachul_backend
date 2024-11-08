package backend.team.ahachul_backend.api.community.adapter.web.`in`

import backend.team.ahachul_backend.api.community.adapter.web.`in`.dto.comment.CreateCommunityCommentDto
import backend.team.ahachul_backend.api.community.adapter.web.`in`.dto.comment.DeleteCommunityCommentDto
import backend.team.ahachul_backend.api.community.adapter.web.`in`.dto.comment.GetCommunityCommentsDto
import backend.team.ahachul_backend.api.community.adapter.web.`in`.dto.comment.UpdateCommunityCommentDto
import backend.team.ahachul_backend.api.community.application.port.`in`.CommunityCommentUseCase
import backend.team.ahachul_backend.api.community.domain.model.CommunityCommentType
import backend.team.ahachul_backend.config.controller.CommonDocsTestConfig
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.queryParameters
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime

@WebMvcTest(CommunityCommentController::class)
class CommunityCommentControllerDocsTest : CommonDocsTestConfig() {

    @MockBean
    lateinit var communityCommentUseCase: CommunityCommentUseCase

    @Test
    fun getCommunityCommentsTest() {
        // given
        val response = GetCommunityCommentsDto.Response(
            listOf(
                GetCommunityCommentsDto.CommunityCommentList(
                    parentComment = GetCommunityCommentsDto.CommunityComment(
                        id = 1,
                        upperCommentId = null,
                        content = "상위 내용",
                        status = CommunityCommentType.CREATED,
                        LocalDateTime.now(),
                        "작성자 ID",
                        "작성자 닉네임"
                    ),
                    childComments = listOf(
                        GetCommunityCommentsDto.CommunityComment(
                            id = 2,
                            upperCommentId = 1,
                            content = "하위 내용",
                            status = CommunityCommentType.CREATED,
                            LocalDateTime.now(),
                            "작성자 ID",
                            "작성자 닉네임"
                        )
                    )
                )
            )
        )

        given(communityCommentUseCase.getCommunityComments(any()))
            .willReturn(response)

        // when
        val result = mockMvc.perform(
            get("/v1/community-comments")
                .param("postId", "1")
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
            .andDo(
                document(
                    "get-community-comments",
                    getDocsRequest(),
                    getDocsResponse(),
                    queryParameters(
                        parameterWithName("postId").description("코멘트 조회할 게시글 아이디")
                    ),
                    PayloadDocumentation.responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.comments[].parentComment.id").type(JsonFieldType.NUMBER).description("코멘트 아이디"),
                        fieldWithPath("result.comments[].parentComment.upperCommentId").type(JsonFieldType.NUMBER).description("상위 코멘트 아이디").optional(),
                        fieldWithPath("result.comments[].parentComment.content").type(JsonFieldType.STRING).description("코멘트 내용"),
                        fieldWithPath("result.comments[].parentComment.status").type("CommunityCommentType").description("코멘트 상태").attributes(getFormatAttribute("CREATED, DELETED")),
                        fieldWithPath("result.comments[].parentComment.createdAt").type("LocalDateTime").description("작성일자"),
                        fieldWithPath("result.comments[].parentComment.createdBy").type(JsonFieldType.STRING).description("작성자 ID"),
                        fieldWithPath("result.comments[].parentComment.writer").type(JsonFieldType.STRING).description("작성자 닉네임"),
                        fieldWithPath("result.comments[].childComments[].id").type(JsonFieldType.NUMBER).description("코멘트 아이디"),
                        fieldWithPath("result.comments[].childComments[].upperCommentId").type(JsonFieldType.NUMBER).description("상위 코멘트 아이디").optional(),
                        fieldWithPath("result.comments[].childComments[].content").type(JsonFieldType.STRING).description("코멘트 내용"),
                        fieldWithPath("result.comments[].childComments[].status").type("CommunityCommentType").description("코멘트 상태").attributes(getFormatAttribute("CREATED, DELETED")),
                        fieldWithPath("result.comments[].childComments[].createdAt").type("LocalDateTime").description("작성일자"),
                        fieldWithPath("result.comments[].childComments[].createdBy").type(JsonFieldType.STRING).description("작성자 ID"),
                        fieldWithPath("result.comments[].childComments[].writer").type(JsonFieldType.STRING).description("작성자 닉네임"),
                    )
                )
            )
    }

    @Test
    fun createCommunityCommentTest() {
        // given
        val response = CreateCommunityCommentDto.Response(
            id = 2,
            upperCommentId = 1,
            content = "생성된 코멘트 내용"
        )

        given(communityCommentUseCase.createCommunityComment(any()))
            .willReturn(response)

        val request = CreateCommunityCommentDto.Request(
            postId = 1,
            upperCommentId = 1,
            content = "생성할 코멘트 내용"
        )

        // when
        val result = mockMvc.perform(
            post("/v1/community-comments")
                .header("Authorization", "Bearer <Access Token>")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
            .andDo(
                document(
                    "create-community-comment",
                    getDocsRequest(),
                    getDocsResponse(),
                    requestHeaders(
                        headerWithName("Authorization").description("엑세스 토큰")
                    ),
                    PayloadDocumentation.requestFields(
                        fieldWithPath("postId").description("코멘트 생성할 게시글 아이디"),
                        fieldWithPath("upperCommentId").description("상위 코멘트 아이디").optional(),
                        fieldWithPath("content").description("생성할 내용")
                    ),
                    PayloadDocumentation.responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.id").type(JsonFieldType.NUMBER).description("생성된 코멘트 아이디"),
                        fieldWithPath("result.upperCommentId").type(JsonFieldType.NUMBER).description("연결된 상위 코멘트 아이디").optional(),
                        fieldWithPath("result.content").type(JsonFieldType.STRING).description("생성된 내용"),
                    )
                )
            )
    }

    @Test
    fun updateCommunityCommentTest() {
        // given
        val response = UpdateCommunityCommentDto.Response(
            id = 1,
            content = "변경된 코멘트 내용"
        )

        given(communityCommentUseCase.updateCommunityComment(any()))
            .willReturn(response)

        val request = UpdateCommunityCommentDto.Request(
            content = "변경할 코멘트 내용"
        )

        // when
        val result = mockMvc.perform(
            patch("/v1/community-comments/{commentId}", 1)
                .header("Authorization", "Bearer <Access Token>")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
            .andDo(
                document(
                    "update-community-comment",
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
    fun deleteCommunityCommentTest() {
        // given
        val response = DeleteCommunityCommentDto.Response(
            id = 1
        )

        given(communityCommentUseCase.deleteCommunityComment(any()))
            .willReturn(response)

        // when
        val result = mockMvc.perform(
            delete("/v1/community-comments/{commentId}", 1)
                .header("Authorization", "Bearer <Access Token>")
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
            .andDo(
                document(
                    "delete-community-comment",
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
