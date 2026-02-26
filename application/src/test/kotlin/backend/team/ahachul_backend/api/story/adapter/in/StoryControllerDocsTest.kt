package backend.team.ahachul_backend.api.story.adapter.`in`

import backend.team.ahachul_backend.api.story.adapter.`in`.dto.StoryDto
import backend.team.ahachul_backend.api.story.application.port.`in`.StoryUseCase
import backend.team.ahachul_backend.config.controller.CommonDocsTestConfig
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.restdocs.request.RequestDocumentation.queryParameters
import org.springframework.restdocs.request.RequestDocumentation.requestParts
import org.springframework.restdocs.request.RequestDocumentation.partWithName
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(StoryController::class)
class StoryControllerDocsTest : CommonDocsTestConfig() {

    @MockBean
    lateinit var storyUseCase: StoryUseCase

    @Test
    fun getMyStories() {
        given(storyUseCase.getMyStories(24)).willReturn(
            StoryDto.ProfileStoriesResponse(
                generatedAt = "2026-02-26T23:59:00+09:00",
                memberId = 1L,
                nickname = "아하철",
                isMine = true,
                storiesVisible = true,
                stories = listOf(
                    StoryDto.StoryItem(
                        storyId = 10L,
                        imageUrl = "https://cdn.ahhachul.com/story/10.png",
                        caption = "오늘 2호선 출근 지옥",
                        stationId = 130L,
                        stationName = "강남",
                        subwayLineId = 2L,
                        subwayLineName = "2호선",
                        createdAt = "2026-02-26T09:10:00+09:00",
                    ),
                ),
            ),
        )

        val result = mockMvc.perform(
            get("/v2/stories/me")
                .header("Authorization", "Bearer <Access Token>")
                .queryParam("limit", "24")
                .accept(MediaType.APPLICATION_JSON),
        )

        result.andExpect(status().isOk)
            .andDo(
                document(
                    "get-my-stories",
                    getDocsRequest(),
                    getDocsResponse(),
                    requestHeaders(
                        headerWithName("Authorization").description("엑세스 토큰"),
                    ),
                    queryParameters(
                        parameterWithName("limit").optional().description("조회할 스토리 개수(기본 24, 최대 60)"),
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.generatedAt").type(JsonFieldType.STRING).description("응답 생성 시각"),
                        fieldWithPath("result.memberId").type(JsonFieldType.NUMBER).description("프로필 멤버 ID"),
                        fieldWithPath("result.nickname").type(JsonFieldType.STRING).description("프로필 닉네임").optional(),
                        fieldWithPath("result.isMine").type(JsonFieldType.BOOLEAN).description("본인 프로필 여부"),
                        fieldWithPath("result.storiesVisible").type(JsonFieldType.BOOLEAN).description("스토리 공개 여부"),
                        fieldWithPath("result.stories").type(JsonFieldType.ARRAY).description("스토리 목록"),
                        fieldWithPath("result.stories[].storyId").type(JsonFieldType.NUMBER).description("스토리 ID"),
                        fieldWithPath("result.stories[].imageUrl").type(JsonFieldType.STRING).description("스토리 이미지 URL"),
                        fieldWithPath("result.stories[].caption").type(JsonFieldType.STRING).description("스토리 캡션").optional(),
                        fieldWithPath("result.stories[].stationId").type(JsonFieldType.NUMBER).description("역 ID").optional(),
                        fieldWithPath("result.stories[].stationName").type(JsonFieldType.STRING).description("역 이름").optional(),
                        fieldWithPath("result.stories[].subwayLineId").type(JsonFieldType.NUMBER).description("노선 ID").optional(),
                        fieldWithPath("result.stories[].subwayLineName").type(JsonFieldType.STRING).description("노선 이름").optional(),
                        fieldWithPath("result.stories[].createdAt").type(JsonFieldType.STRING).description("스토리 생성 시각"),
                    ),
                ),
            )
    }

    @Test
    fun getMemberStories() {
        given(storyUseCase.getMemberStories(any())).willReturn(
            StoryDto.ProfileStoriesResponse(
                generatedAt = "2026-02-26T23:59:00+09:00",
                memberId = 2L,
                nickname = "출근러",
                isMine = false,
                storiesVisible = true,
                stories = emptyList(),
            ),
        )

        val result = mockMvc.perform(
            get("/v2/members/{nickname}/stories", "출근러")
                .queryParam("asPublic", "true")
                .queryParam("limit", "12")
                .accept(MediaType.APPLICATION_JSON),
        )

        result.andExpect(status().isOk)
            .andDo(
                document(
                    "get-member-stories",
                    getDocsRequest(),
                    getDocsResponse(),
                    pathParameters(
                        parameterWithName("nickname").description("조회 대상 닉네임"),
                    ),
                    queryParameters(
                        parameterWithName("asPublic").optional().description("공개 정책 강제 적용 여부"),
                        parameterWithName("limit").optional().description("조회할 스토리 개수(기본 24, 최대 60)"),
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.generatedAt").type(JsonFieldType.STRING).description("응답 생성 시각"),
                        fieldWithPath("result.memberId").type(JsonFieldType.NUMBER).description("프로필 멤버 ID"),
                        fieldWithPath("result.nickname").type(JsonFieldType.STRING).description("프로필 닉네임").optional(),
                        fieldWithPath("result.isMine").type(JsonFieldType.BOOLEAN).description("본인 프로필 여부"),
                        fieldWithPath("result.storiesVisible").type(JsonFieldType.BOOLEAN).description("스토리 공개 여부"),
                        fieldWithPath("result.stories").type(JsonFieldType.ARRAY).description("스토리 목록"),
                        fieldWithPath("result.stories[].storyId").type(JsonFieldType.NUMBER).description("스토리 ID").optional(),
                        fieldWithPath("result.stories[].imageUrl").type(JsonFieldType.STRING).description("스토리 이미지 URL").optional(),
                        fieldWithPath("result.stories[].caption").type(JsonFieldType.STRING).description("스토리 캡션").optional(),
                        fieldWithPath("result.stories[].stationId").type(JsonFieldType.NUMBER).description("역 ID").optional(),
                        fieldWithPath("result.stories[].stationName").type(JsonFieldType.STRING).description("역 이름").optional(),
                        fieldWithPath("result.stories[].subwayLineId").type(JsonFieldType.NUMBER).description("노선 ID").optional(),
                        fieldWithPath("result.stories[].subwayLineName").type(JsonFieldType.STRING).description("노선 이름").optional(),
                        fieldWithPath("result.stories[].createdAt").type(JsonFieldType.STRING).description("스토리 생성 시각").optional(),
                    ),
                ),
            )
    }

    @Test
    fun createStory() {
        given(storyUseCase.createStory(any())).willReturn(
            StoryDto.CreateResponse(
                story = StoryDto.StoryItem(
                    storyId = 33L,
                    imageUrl = "https://cdn.ahhachul.com/story/33.png",
                    caption = "오늘도 아하철",
                    stationId = 557L,
                    stationName = "안암",
                    subwayLineId = 18L,
                    subwayLineName = "우이신설경전철",
                    createdAt = "2026-02-26T11:00:00+09:00",
                ),
            ),
        )

        val request = StoryDto.CreateRequest(
            caption = "오늘도 아하철",
            stationId = 557L,
            subwayLineId = 18L,
        )
        val contentPart = MockMultipartFile(
            "content",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsString(request).toByteArray(),
        )
        val imagePart = MockMultipartFile(
            "image",
            "story.png",
            MediaType.IMAGE_PNG_VALUE,
            "<< png data >>".toByteArray(),
        )

        val result = mockMvc.perform(
            multipart("/v2/stories")
                .file(contentPart)
                .file(imagePart)
                .header("Authorization", "Bearer <Access Token>")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON),
        )

        result.andExpect(status().isOk)
            .andDo(
                document(
                    "create-story",
                    getDocsRequest(),
                    getDocsResponse(),
                    requestHeaders(
                        headerWithName("Authorization").description("엑세스 토큰"),
                    ),
                    requestParts(
                        partWithName("content").description("스토리 생성 요청 DTO"),
                        partWithName("image").description("업로드할 스토리 이미지 파일"),
                    ),
                    requestPartFields(
                        "content",
                        fieldWithPath("caption").type(JsonFieldType.STRING).description("스토리 캡션").optional(),
                        fieldWithPath("stationId").type(JsonFieldType.NUMBER).description("스토리 연관 역 ID").optional(),
                        fieldWithPath("subwayLineId").type(JsonFieldType.NUMBER).description("스토리 연관 노선 ID").optional(),
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.story.storyId").type(JsonFieldType.NUMBER).description("스토리 ID"),
                        fieldWithPath("result.story.imageUrl").type(JsonFieldType.STRING).description("스토리 이미지 URL"),
                        fieldWithPath("result.story.caption").type(JsonFieldType.STRING).description("스토리 캡션").optional(),
                        fieldWithPath("result.story.stationId").type(JsonFieldType.NUMBER).description("역 ID").optional(),
                        fieldWithPath("result.story.stationName").type(JsonFieldType.STRING).description("역 이름").optional(),
                        fieldWithPath("result.story.subwayLineId").type(JsonFieldType.NUMBER).description("노선 ID").optional(),
                        fieldWithPath("result.story.subwayLineName").type(JsonFieldType.STRING).description("노선 이름").optional(),
                        fieldWithPath("result.story.createdAt").type(JsonFieldType.STRING).description("스토리 생성 시각"),
                    ),
                ),
            )
    }

    @Test
    fun deleteStory() {
        given(storyUseCase.deleteStory(33L)).willReturn(
            StoryDto.DeleteResponse(
                storyId = 33L,
                deleted = true,
            ),
        )

        val result = mockMvc.perform(
            delete("/v2/stories/{storyId}", 33L)
                .header("Authorization", "Bearer <Access Token>")
                .accept(MediaType.APPLICATION_JSON),
        )

        result.andExpect(status().isOk)
            .andDo(
                document(
                    "delete-story",
                    getDocsRequest(),
                    getDocsResponse(),
                    requestHeaders(
                        headerWithName("Authorization").description("엑세스 토큰"),
                    ),
                    pathParameters(
                        parameterWithName("storyId").description("삭제할 스토리 ID"),
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.storyId").type(JsonFieldType.NUMBER).description("삭제한 스토리 ID"),
                        fieldWithPath("result.deleted").type(JsonFieldType.BOOLEAN).description("삭제 성공 여부"),
                    ),
                ),
            )
    }
}
