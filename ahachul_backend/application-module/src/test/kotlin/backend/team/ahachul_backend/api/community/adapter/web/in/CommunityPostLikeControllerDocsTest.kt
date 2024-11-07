package backend.team.ahachul_backend.api.community.adapter.web.`in`

import backend.team.ahachul_backend.api.community.application.port.`in`.CommunityPostLikeUseCase
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.*
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.restdocs.headers.HeaderDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(CommunityPostLikeController::class)
class CommunityPostLikeControllerDocsTest: CommonDocsTestConfig() {

    @MockBean
    lateinit var communityPostLikeUseCase: CommunityPostLikeUseCase

    @Test
    fun communityPostLikeTest() {
        // given
        willDoNothing().given(communityPostLikeUseCase).like(anyLong())

        // when
        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.post("/v1/community-posts/{postId}/like", 1)
                .header("Authorization", "Bearer <Access Token>")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(
                MockMvcRestDocumentation.document(
                    "community-post-like",
                    getDocsRequest(),
                    getDocsResponse(),
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("엑세스 토큰")
                    ),
                    RequestDocumentation.pathParameters(
                        parameterWithName("postId").description("좋아요 할 게시물 아이디")
                    ),
                    PayloadDocumentation.responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result").optional().description("X")
                    )
                )
            )
    }

    @Test
    fun communityPostNotLikeTest() {
        // given
        willDoNothing().given(communityPostLikeUseCase).notLike(anyLong())

        // when
        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.delete("/v1/community-posts/{postId}/like", 1)
                .header("Authorization", "Bearer <Access Token>")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(
                MockMvcRestDocumentation.document(
                    "community-post-not-like",
                    getDocsRequest(),
                    getDocsResponse(),
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("엑세스 토큰")
                    ),
                    RequestDocumentation.pathParameters(
                        parameterWithName("postId").description("좋아요 해제할 게시물 아이디")
                    ),
                    PayloadDocumentation.responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result").optional().description("X")
                    )
                )
            )
    }

    @Test
    fun communityPostHateTest() {
        // given
        willDoNothing().given(communityPostLikeUseCase).like(anyLong())

        // when
        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.post("/v1/community-posts/{postId}/hate", 1)
                .header("Authorization", "Bearer <Access Token>")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(
                MockMvcRestDocumentation.document(
                    "community-post-hate",
                    getDocsRequest(),
                    getDocsResponse(),
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("엑세스 토큰")
                    ),
                    RequestDocumentation.pathParameters(
                        parameterWithName("postId").description("싫어요 할 게시물 아이디")
                    ),
                    PayloadDocumentation.responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result").optional().description("X")
                    )
                )
            )
    }

    @Test
    fun communityPostNotHateTest() {
        // given
        willDoNothing().given(communityPostLikeUseCase).notLike(anyLong())

        // when
        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.delete("/v1/community-posts/{postId}/hate", 1)
                .header("Authorization", "Bearer <Access Token>")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(
                MockMvcRestDocumentation.document(
                    "community-post-not-hate",
                    getDocsRequest(),
                    getDocsResponse(),
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("엑세스 토큰")
                    ),
                    RequestDocumentation.pathParameters(
                        parameterWithName("postId").description("싫어요 해제할 게시물 아이디")
                    ),
                    PayloadDocumentation.responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result").optional().description("X")
                    )
                )
            )
    }
}
