package backend.team.ahachul_backend.api.community.adapter.web.`in`

import backend.team.ahachul_backend.api.comment.adapter.web.`in`.dto.CreateCommentDto
import backend.team.ahachul_backend.api.comment.adapter.web.`in`.dto.GetCommentsDto
import backend.team.ahachul_backend.api.comment.application.port.`in`.CommentUseCase
import backend.team.ahachul_backend.api.comment.domain.model.CommentType
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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime

@WebMvcTest(CommunityPostCommentController::class)
class CommunityPostCommentControllerDocsTest : CommonDocsTestConfig() {

    @MockBean
    lateinit var commentUseCase: CommentUseCase

    @Test
    fun getCommunityPostCommentsTest() {
        // given
        val response = GetCommentsDto.Response(
            listOf(
                GetCommentsDto.CommentList(
                    parentComment = GetCommentsDto.Comment(
                        id = 1,
                        upperCommentId = null,
                        content = "상위 내용",
                        status = CommentType.CREATED,
                        LocalDateTime.now(),
                        "작성자 ID",
                        "작성자 닉네임",
                        true,
                        0L
                    ),
                    childComments = listOf(
                        GetCommentsDto.Comment(
                            id = 2,
                            upperCommentId = 1,
                            content = "하위 내용",
                            status = CommentType.CREATED,
                            LocalDateTime.now(),
                            "작성자 ID",
                            "작성자 닉네임",
                            false,
                            0L
                        )
                    )
                )
            )
        )

        given(commentUseCase.getComments(any()))
            .willReturn(response)

        // when
        val result = mockMvc.perform(
            get("/v1/community-posts/{postId}/comments", 1L)
                .queryParam("sort", "createdAt,desc")
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
            .andDo(
                document(
                    "get-community-post-comments",
                    getDocsRequest(),
                    getDocsResponse(),
                    pathParameters(
                        parameterWithName("postId").description("코멘트 조회할 게시글 아이디")
                    ),
                    queryParameters(
                        parameterWithName("sort").description("정렬 조건").attributes(getFormatAttribute("(likes|createdAt),(asc|desc)")),
                    ),
                    PayloadDocumentation.responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.comments[].parentComment.id").type(JsonFieldType.NUMBER).description("코멘트 아이디"),
                        fieldWithPath("result.comments[].parentComment.upperCommentId").type(JsonFieldType.NUMBER).description("상위 코멘트 아이디").optional(),
                        fieldWithPath("result.comments[].parentComment.content").type(JsonFieldType.STRING).description("코멘트 내용"),
                        fieldWithPath("result.comments[].parentComment.status").type("CommentType").description("코멘트 상태").attributes(getFormatAttribute("CREATED, DELETED")),
                        fieldWithPath("result.comments[].parentComment.createdAt").type("LocalDateTime").description("작성일자"),
                        fieldWithPath("result.comments[].parentComment.createdBy").type(JsonFieldType.STRING).description("작성자 ID"),
                        fieldWithPath("result.comments[].parentComment.writer").type(JsonFieldType.STRING).description("작성자 닉네임"),
                        fieldWithPath("result.comments[].parentComment.isPrivate").type(JsonFieldType.BOOLEAN).description("비공개 여부").optional(),
                        fieldWithPath("result.comments[].parentComment.likeCnt").type(JsonFieldType.NUMBER).description("좋아요 수"),
                        fieldWithPath("result.comments[].childComments[].id").type(JsonFieldType.NUMBER).description("코멘트 아이디"),
                        fieldWithPath("result.comments[].childComments[].upperCommentId").type(JsonFieldType.NUMBER).description("상위 코멘트 아이디").optional(),
                        fieldWithPath("result.comments[].childComments[].content").type(JsonFieldType.STRING).description("코멘트 내용"),
                        fieldWithPath("result.comments[].childComments[].status").type("CommentType").description("코멘트 상태").attributes(getFormatAttribute("CREATED, DELETED")),
                        fieldWithPath("result.comments[].childComments[].createdAt").type("LocalDateTime").description("작성일자"),
                        fieldWithPath("result.comments[].childComments[].createdBy").type(JsonFieldType.STRING).description("작성자 ID"),
                        fieldWithPath("result.comments[].childComments[].writer").type(JsonFieldType.STRING).description("작성자 닉네임"),
                        fieldWithPath("result.comments[].childComments[].isPrivate").type(JsonFieldType.BOOLEAN).description("비공개 여부").optional(),
                        fieldWithPath("result.comments[].childComments[].likeCnt").type(JsonFieldType.NUMBER).description("좋아요 수"),
                        )
                )
            )
    }

    @Test
    fun createCommunityPostCommentTest() {
        // given
        val response = CreateCommentDto.Response(
            id = 2,
            upperCommentId = 1,
            content = "생성된 커뮤니티 코멘트 내용"
        )

        given(commentUseCase.createComment(any()))
            .willReturn(response)

        val request = CreateCommentDto.Request(
            upperCommentId = 1,
            content = "생성할 커뮤니티 코멘트 내용",
            isPrivate = null,
        )

        // when
        val result = mockMvc.perform(
            post("/v1/community-posts/{postId}/comments", 1L)
                .header("Authorization", "Bearer <Access Token>")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
            .andDo(
                document(
                    "create-community-post-comment",
                    getDocsRequest(),
                    getDocsResponse(),
                    pathParameters(
                        parameterWithName("postId").description("코멘트 생성할 게시글 아이디")
                    ),
                    requestHeaders(
                        headerWithName("Authorization").description("엑세스 토큰")
                    ),
                    PayloadDocumentation.requestFields(
                        fieldWithPath("upperCommentId").type(JsonFieldType.NUMBER).description("상위 코멘트 아이디").optional(),
                        fieldWithPath("content").type(JsonFieldType.STRING).description("생성할 내용"),
                        fieldWithPath("isPrivate").type(JsonFieldType.BOOLEAN).description("비공개 여부").optional()
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
}
