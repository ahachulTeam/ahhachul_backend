package backend.team.ahachul_backend.api.lost.adapter.web.`in`

import backend.team.ahachul_backend.api.lost.adapter.web.`in`.dto.*
import backend.team.ahachul_backend.api.lost.application.port.`in`.LostPostUseCase
import backend.team.ahachul_backend.api.lost.domain.model.LostStatus
import backend.team.ahachul_backend.api.lost.domain.model.LostType
import backend.team.ahachul_backend.config.controller.CommonDocsConfig
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
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(LostPostController::class)
class LostPostControllerDocsTest: CommonDocsConfig() {

    @MockBean lateinit var lostPostUseCase: LostPostUseCase

    @Test
    fun getLostPost() {
        // given
        val response = GetLostPostDto.Response(
            title = "title",
            content = "content",
            writer = "writer",
            date = "2023/01/23",
            lostLine = "1호선",
            chats = 1,
            status = LostStatus.PROGRESS,
            imgUrls = listOf(),
            storage = "우리집",
            storageNumber = "02-2222-3333"
        )

        given(lostPostUseCase.getLostPost())
            .willReturn(response)

        // when
        val result = mockMvc.perform(
            get("/v1/lost-posts/{lostId}", 1)
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
            .andDo(document("get-lost-post",
                getDocsRequest(),
                getDocsResponse(),
                pathParameters(
                    parameterWithName("lostId").description("유실물 아이디")
                ),
                responseFields(
                    *commonResponseFields(),
                    fieldWithPath("result.title").type(JsonFieldType.STRING).description("유실물 제목"),
                    fieldWithPath("result.content").type(JsonFieldType.STRING).description("유실물 내용"),
                    fieldWithPath("result.writer").type(JsonFieldType.STRING).description("유실물 작성자 닉네임"),
                    fieldWithPath("result.date").type(JsonFieldType.STRING).description("유실물 작성 날짜"),
                    fieldWithPath("result.lostLine").type(JsonFieldType.STRING).description("유실 호선"),
                    fieldWithPath("result.chats").type(JsonFieldType.NUMBER).description("유실물 쪽지 개수"),
                    fieldWithPath("result.imgUrls").type(JsonFieldType.ARRAY).description("유실물 이미지 리스트"),
                    fieldWithPath("result.status").type(JsonFieldType.STRING).description("유실물 찾기 완료 여부 : PROGRESS / COMPLETE"),
                    fieldWithPath("result.storage" ).type(JsonFieldType.STRING).description("보관 장소 : Lost112 데이터"),
                    fieldWithPath("result.storageNumber").type(JsonFieldType.STRING).description("보관 장소 전화번호 : Lost112 데이터")
                )
            ))
    }

    @Test
    fun searchLostPosts() {
        // given
        val response = SearchLostPostsDto.Response(
            listOf(
                SearchLostPostsDto.SearchLost(
                    title = "title",
                    content = "content",
                    writer = "writer",
                    date = "2023/01/23",
                    lostLine = "1호선",
                    chats = 1,
                    status = LostStatus.PROGRESS,
                    imgUrl = "img"
                )
            )
        )

        given(lostPostUseCase.searchLostPosts())
            .willReturn(response)

        // when
        val result = mockMvc.perform(
            get("/v1/lost-posts")
                .queryParam("page", "1")
                .queryParam("size", "5")
                .queryParam("type", "LOST")
                .queryParam("line", "1호선")
                .queryParam("origin", "LOST112")
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
            .andDo(document("search-lost-posts",
                getDocsRequest(),
                getDocsResponse(),
                queryParameters(
                    parameterWithName("page").description("현재 페이지"),
                    parameterWithName("size").description("페이지 노출 데이터 수"),
                    parameterWithName("type").description("유실물 타입 : LOST(유실) / ACQUIRE(습득)"),
                    parameterWithName("line").description("유실물 호선").optional(),
                    parameterWithName("origin").description("유실물 출처 : LOST112 / APP").optional()
                ),
                responseFields(
                    *commonResponseFields(),
                    fieldWithPath("result.posts[].title").type(JsonFieldType.STRING).description("유실물 제목"),
                    fieldWithPath("result.posts[].content").type(JsonFieldType.STRING).description("유실물 내용"),
                    fieldWithPath("result.posts[].writer").type(JsonFieldType.STRING).description("유실물 작성자 닉네임"),
                    fieldWithPath("result.posts[].date").type(JsonFieldType.STRING).description("유실물 작성 날짜"),
                    fieldWithPath("result.posts[].lostLine").type(JsonFieldType.STRING).description("유실 호선"),
                    fieldWithPath("result.posts[].chats").type(JsonFieldType.NUMBER).description("유실물 쪽지 개수"),
                    fieldWithPath("result.posts[].imgUrl").type(JsonFieldType.STRING).description("유실물 이미지(썸네일)"),
                    fieldWithPath("result.posts[].status").type(JsonFieldType.STRING).description("유실물 찾기 완료 여부 : PROGRESS / COMPLETE"),
                )
            ))
    }

    @Test
    fun createLostPost() {
        // given
        val response = CreateLostPostDto.Response(id = 1)

        given(lostPostUseCase.createLostPost(any()))
            .willReturn(response)

        val request = CreateLostPostDto.Request(
            title = "title",
            content = "content",
            lostLine = "1",
            lostType = LostType.LOST,
            imgUrls = arrayListOf("url1", "url2")
        )

        // when
        val result = mockMvc.perform(
            post("/v1/lost-posts")
                .header("Authorization", "Bearer <Access Token>")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
                .andDo(document("create-lost-post",
                    getDocsRequest(),
                    getDocsResponse(),
                    requestHeaders(
                        headerWithName("Authorization").description("엑세스 토큰")
                    ),
                    requestFields(
                        fieldWithPath("title").type(JsonFieldType.STRING).description("유실물 제목"),
                        fieldWithPath("content").type(JsonFieldType.STRING).description("유실물 내용"),
                        fieldWithPath("lostLine").type(JsonFieldType.STRING).description("유실 호선 EX) 1호선 / 수인분당선"),
                        fieldWithPath("imgUrls").type(JsonFieldType.ARRAY).description("유실물 이미지 리스트").optional(),
                        fieldWithPath("lostType").type(JsonFieldType.STRING).description("유실물 타입 : LOST(유실) / ACQUIRE(습득)"),
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.id").type(JsonFieldType.NUMBER).description("저장한 유실물 아이디"),
                    )
                ))
    }

    @Test
    fun updateLostPost() {
        // given
        val response = UpdateLostPostDto.Response(
            id = 1,
            title = "title",
            content = "content",
            lostLine = "1",
            status = LostStatus.COMPLETE
        )

        given(lostPostUseCase.updateLostPost(any(), any()))
            .willReturn(response)

        val request = UpdateLostPostDto.Request(
            title = "title",
            content = "content",
            imgUrls = arrayListOf("url1", "url2"),
            lostLine = "1",
            status = LostStatus.COMPLETE
        )

        // when
        val result = mockMvc.perform(
            patch("/v1/lost-posts/{lostId}", 1)
                .header("Authorization", "Bearer <Access Token>")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
            .andDo(document("update-lost-post",
                getDocsRequest(),
                getDocsResponse(),
                requestHeaders(
                    headerWithName("Authorization").description("엑세스 토큰")
                ),
                pathParameters(
                    parameterWithName("lostId").description("유실물 아이디")
                ),
                requestFields(
                    fieldWithPath("title").type(JsonFieldType.STRING).description("유실물 제목").optional(),
                    fieldWithPath("content").type(JsonFieldType.STRING).description("유실물 내용").optional(),
                    fieldWithPath("imgUrls").type(JsonFieldType.ARRAY).description("유실물 이미지 리스트").optional(),
                    fieldWithPath("lostLine").type(JsonFieldType.STRING).description("유실 호선").optional(),
                    fieldWithPath("status").type(JsonFieldType.STRING).description("유실물 찾기 완료 상태 : PROGRESS / COMPLETE").optional(),
                ),
                responseFields(
                    *commonResponseFields(),
                    fieldWithPath("result.id").type(JsonFieldType.NUMBER).description("수정한 유실물 아이디"),
                )
            ))
    }

    @Test
    fun deleteLostPost() {
        // given
        val response = DeleteLostPostDto.Response(id = 1)

        given(lostPostUseCase.deleteLostPost())
            .willReturn(response)

        // when
        val result = mockMvc.perform(
            delete("/v1/lost-posts/{lostId}", 1)
                .header("Authorization", "Bearer <Access Token>")
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
            .andDo(document("delete-lost-post",
                getDocsRequest(),
                getDocsResponse(),
                pathParameters(
                    parameterWithName("lostId").description("유실물 아이디")
                ),
                requestHeaders(
                    headerWithName("Authorization").description("엑세스 토큰")
                ),
                responseFields(
                    *commonResponseFields(),
                    fieldWithPath("result.id").type(JsonFieldType.NUMBER).description("삭제한 유실물 아이디"),
                )
            ))
    }
}