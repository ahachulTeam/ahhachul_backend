package backend.team.ahachul_backend.api.community.adapter.web.`in`

import backend.team.ahachul_backend.api.community.adapter.web.`in`.dto.*
import backend.team.ahachul_backend.api.community.application.port.`in`.CommunityPostUseCase
import backend.team.ahachul_backend.api.community.domain.model.CommunityCategoryType
import backend.team.ahachul_backend.common.dto.ImageDto
import backend.team.ahachul_backend.common.domain.model.RegionType
import backend.team.ahachul_backend.common.domain.model.YNType
import backend.team.ahachul_backend.common.dto.PageInfoDto
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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@WebMvcTest(CommunityPostController::class)
class CommunityPostControllerDocsTest : CommonDocsTestConfig() {

    @MockBean
    lateinit var communityPostUseCase: CommunityPostUseCase

    @Test
    fun searchCommunityPostsTest() {
        // given
        val response = PageInfoDto.of(
            data=listOf(
                SearchCommunityPostDto.Response(
                    id = 1,
                    title = "제목",
                    content = "내용",
                    categoryType = CommunityCategoryType.ISSUE,
                    hashTags = arrayListOf("여행", "취미"),
                    commentCnt = 0,
                    viewCnt = 0,
                    likeCnt = 0,
                    hotPostYn = YNType.Y,
                    regionType = RegionType.METROPOLITAN,
                    subwayLineId = 1L,
                    createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")),
                    createdBy = "작성자 ID",
                    writer = "작성자 닉네임",
                    image = ImageDto.of(1L, "url1")
                )
            ),
            pageSize=1,
            arrayOf(SearchCommunityPostDto.Response::createdAt, SearchCommunityPostDto.Response::id)
        )

        given(communityPostUseCase.searchCommunityPosts(any()))
            .willReturn(response)

        // when
        val result = mockMvc.perform(
            get("/v1/community-posts")
                .queryParam("categoryType", "ISSUE")
                .queryParam("subwayLineId", "1")
                .queryParam("content", "내용")
                .queryParam("hashTag", "여행")
                .queryParam("hotPostYn", "Y")
                .queryParam("writer", "작성자")
                .queryParam("sort", "createdAt,desc")
                .queryParam("pageToken", "MTIzMTI5MTU6MTI=")
                .queryParam("pageSize", "10" )
                .header("Authorization", "Bearer <Access Token>")
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
            .andDo(
                document(
                    "search-community-posts",
                    getDocsRequest(),
                    getDocsResponse(),
                    queryParameters(
                        parameterWithName("categoryType").description("카테고리 타입").attributes(getFormatAttribute("FREE, INSIGHT, ISSUE, HUMOR")).optional(),
                        parameterWithName("subwayLineId").description("노선 ID").optional(),
                        parameterWithName("content").description("검색하고자 하는 내용").optional(),
                        parameterWithName("hashTag").description("검색하고자 하는 해시 태그").optional(),
                        parameterWithName("hotPostYn").description("검색하고자 하는 핫 게시글 여부").optional(),
                        parameterWithName("writer").description("검색하고자 하는 작성자 닉네임").optional(),
                        parameterWithName("sort").description("정렬 조건").attributes(getFormatAttribute("(likes|createdAt|views),(asc|desc)")),
                        parameterWithName("pageToken").description("base64로 인코딩 된 페이지 토큰 문자열").optional(),
                        parameterWithName("pageSize").description("페이지 노출 데이터 수. index 0부터 시작"),
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 존재 여부"),
                        fieldWithPath("result.pageToken").type(JsonFieldType.STRING).description("다음 게시글을 가져오기 위한 base64로 인코딩 된 페이지 토큰 문자열").optional(),
                        fieldWithPath("result.data[].id").type(JsonFieldType.NUMBER).description("게시글 아이디"),
                        fieldWithPath("result.data[].title").type(JsonFieldType.STRING).description("게시글 제목"),
                        fieldWithPath("result.data[].content").type(JsonFieldType.STRING).description("게시글 내용"),
                        fieldWithPath("result.data[].categoryType").type("CategoryType").description("카테고리 타입").attributes(getFormatAttribute("FREE, INSIGHT, ISSUE, HUMOR")),
                        fieldWithPath("result.data[].hashTags").type(JsonFieldType.ARRAY).description("해시 태그 목록"),
                        fieldWithPath("result.data[].commentCnt").type(JsonFieldType.NUMBER).description("댓글 수"),
                        fieldWithPath("result.data[].viewCnt").type(JsonFieldType.NUMBER).description("조회수"),
                        fieldWithPath("result.data[].likeCnt").type(JsonFieldType.NUMBER).description("좋아요 수"),
                        fieldWithPath("result.data[].hotPostYn").type("YNType").description("핫 게시글 여부").attributes(getFormatAttribute("Y, N")),
                        fieldWithPath("result.data[].regionType").type("RegionType").description("지역").attributes(getFormatAttribute("METROPOLITAN")),
                        fieldWithPath("result.data[].subwayLineId").type(JsonFieldType.NUMBER).description("지하철 노선 ID"),
                        fieldWithPath("result.data[].createdAt").type("LocalDateTime").description("작성일자"),
                        fieldWithPath("result.data[].createdBy").type(JsonFieldType.STRING).description("작성자 ID"),
                        fieldWithPath("result.data[].writer").type(JsonFieldType.STRING).description("작성자 닉네임"),
                        fieldWithPath("result.data[].image").type(JsonFieldType.OBJECT).description("등록된 이미지"),
                        fieldWithPath("result.data[].image.imageId").type(JsonFieldType.NUMBER).description("등록된 첫 번쨰 이미지 ID"),
                        fieldWithPath("result.data[].image.imageUrl").type(JsonFieldType.STRING).description("등록된 첫 번째 이미지 URI"),
                    )
                )
            )
    }

    @Test
    fun searchCommunityHotPostsTest() {
        // given
        val response = PageInfoDto.of(
            data=listOf(
                SearchCommunityPostDto.Response(
                    id = 1,
                    title = "인기글 제목",
                    content = "인기글 내용",
                    categoryType = CommunityCategoryType.ISSUE,
                    hashTags = arrayListOf("여행", "취미"),
                    commentCnt = 0,
                    viewCnt = 0,
                    likeCnt = 0,
                    hotPostYn = YNType.Y,
                    regionType = RegionType.METROPOLITAN,
                    subwayLineId = 1L,
                    createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")),
                    createdBy = "작성자 ID",
                    writer = "작성자 닉네임",
                    image = ImageDto.of(1L, "url1")
                )
            ),
            pageSize=1,
            arrayOf(SearchCommunityPostDto.Response::createdAt, SearchCommunityPostDto.Response::id)
        )

        given(communityPostUseCase.searchCommunityHotPosts(any()))
            .willReturn(response)

        // when
        val result = mockMvc.perform(
            get("/v1/community-hot-posts")
                .queryParam("subwayLineId", "1")
                .queryParam("content", "내용")
                .queryParam("hashTag", "여행")
                .queryParam("writer", "작성자")
                .queryParam("sort", "createdAt,desc")
                .queryParam("pageToken", "MTIzMTI5MTU6MTI=")
                .queryParam("pageSize", "10" )
                .header("Authorization", "Bearer <Access Token>")
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
            .andDo(
                document(
                    "search-community-hot-posts",
                    getDocsRequest(),
                    getDocsResponse(),
                    queryParameters(
                        parameterWithName("subwayLineId").description("노선 ID").optional(),
                        parameterWithName("content").description("검색하고자 하는 내용").optional(),
                        parameterWithName("hashTag").description("검색하고자 하는 해시 태그").optional(),
                        parameterWithName("writer").description("검색하고자 하는 작성자 닉네임").optional(),
                        parameterWithName("sort").description("정렬 조건").attributes(getFormatAttribute("(likes|createdAt|views),(asc|desc)")),
                        parameterWithName("pageToken").description("base64로 인코딩 된 페이지 토큰 문자열").optional(),
                        parameterWithName("pageSize").description("페이지 노출 데이터 수. index 0부터 시작"),
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 존재 여부"),
                        fieldWithPath("result.pageToken").type(JsonFieldType.STRING).description("다음 게시글을 가져오기 위한 base64로 인코딩 된 페이지 토큰 문자열").optional(),
                        fieldWithPath("result.data[].id").type(JsonFieldType.NUMBER).description("게시글 아이디"),
                        fieldWithPath("result.data[].title").type(JsonFieldType.STRING).description("게시글 제목"),
                        fieldWithPath("result.data[].content").type(JsonFieldType.STRING).description("게시글 내용"),
                        fieldWithPath("result.data[].categoryType").type("CategoryType").description("카테고리 타입").attributes(getFormatAttribute("FREE, INSIGHT, ISSUE, HUMOR")),
                        fieldWithPath("result.data[].hashTags").type(JsonFieldType.ARRAY).description("해시 태그 목록"),
                        fieldWithPath("result.data[].commentCnt").type(JsonFieldType.NUMBER).description("댓글 수"),
                        fieldWithPath("result.data[].viewCnt").type(JsonFieldType.NUMBER).description("조회수"),
                        fieldWithPath("result.data[].likeCnt").type(JsonFieldType.NUMBER).description("좋아요 수"),
                        fieldWithPath("result.data[].hotPostYn").type("YNType").description("핫 게시글 여부").attributes(getFormatAttribute("Y, N")),
                        fieldWithPath("result.data[].regionType").type("RegionType").description("지역").attributes(getFormatAttribute("METROPOLITAN")),
                        fieldWithPath("result.data[].subwayLineId").type(JsonFieldType.NUMBER).description("지하철 노선 ID"),
                        fieldWithPath("result.data[].createdAt").type("LocalDateTime").description("작성일자"),
                        fieldWithPath("result.data[].createdBy").type(JsonFieldType.STRING).description("작성자 ID"),
                        fieldWithPath("result.data[].writer").type(JsonFieldType.STRING).description("작성자 닉네임"),
                        fieldWithPath("result.data[].image").type(JsonFieldType.OBJECT).description("등록된 이미지"),
                        fieldWithPath("result.data[].image.imageId").type(JsonFieldType.NUMBER).description("등록된 첫 번쨰 이미지 ID"),
                        fieldWithPath("result.data[].image.imageUrl").type(JsonFieldType.STRING).description("등록된 첫 번째 이미지 URI"),
                    )
                )
            )
    }

    @Test
    fun getCommunityPostTest() {
        // given
        val response = GetCommunityPostDto.Response(
            1,
            "제목",
            "내용",
            CommunityCategoryType.ISSUE,
            arrayListOf("여행", "취미"),
            0,
            0,
            0,
            YNType.Y,
            YNType.N,
            YNType.Y,
            RegionType.METROPOLITAN,
            1L,
            LocalDateTime.now(),
            "작성자 ID",
            "작성자 닉네임",
            listOf(ImageDto.of(1L, "url1"), ImageDto.of(2L, "url2"))
        )

        given(communityPostUseCase.getCommunityPost(any()))
            .willReturn(response)

        // when
        val result = mockMvc.perform(
            get("/v1/community-posts/{postId}", 1)
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
            .andDo(
                document(
                    "get-community-post",
                    getDocsRequest(),
                    getDocsResponse(),
                    pathParameters(
                        parameterWithName("postId").description("게시물 아이디")
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.id").type(JsonFieldType.NUMBER).description("게시글 아이디"),
                        fieldWithPath("result.title").type(JsonFieldType.STRING).description("게시글 제목"),
                        fieldWithPath("result.content").type(JsonFieldType.STRING).description("게시글 내용"),
                        fieldWithPath("result.categoryType").type("CategoryType").description("카테고리 타입").attributes(getFormatAttribute("FREE, INSIGHT, ISSUE, HUMOR")),
                        fieldWithPath("result.hashTags").type(JsonFieldType.ARRAY).description("해시 태그 목록"),
                        fieldWithPath("result.viewCnt").type(JsonFieldType.NUMBER).description("조회수"),
                        fieldWithPath("result.likeCnt").type(JsonFieldType.NUMBER).description("좋아요 수"),
                        fieldWithPath("result.hateCnt").type(JsonFieldType.NUMBER).description("싫어요 수"),
                        fieldWithPath("result.likeYn").type("YNType").description("좋아요 눌렀는지 여부").attributes(getFormatAttribute("Y, N")),
                        fieldWithPath("result.hateYn").type("YNType").description("싫어요 눌렀는지 여부").attributes(getFormatAttribute("Y, N")),
                        fieldWithPath("result.hotPostYn").type("YNType").description("핫 게시글 여부").attributes(getFormatAttribute("Y, N")),
                        fieldWithPath("result.regionType").type("RegionType").description("지역").attributes(getFormatAttribute("METROPOLITAN")),
                        fieldWithPath("result.subwayLineId").type(JsonFieldType.NUMBER).description("지하철 노선 ID"),
                        fieldWithPath("result.createdAt").type("LocalDateTime").description("작성일자"),
                        fieldWithPath("result.createdBy").type(JsonFieldType.STRING).description("작성자 ID"),
                        fieldWithPath("result.writer").type(JsonFieldType.STRING).description("작성자 닉네임"),
                        fieldWithPath("result.images[]").type(JsonFieldType.ARRAY).description("등록된 이미지 목록"),
                        fieldWithPath("result.images[].imageId").type(JsonFieldType.NUMBER).description("등록된 이미지 ID"),
                        fieldWithPath("result.images[].imageUrl").type(JsonFieldType.STRING).description("등록된 이미지 URI"),
                    )
                )
            )
    }

    @Test
    fun createCommunityPostTest() {
        // given
        val response = CreateCommunityPostDto.Response(
            id = 1,
            title = "생성된 제목",
            content = "생성된 내용",
            categoryType = CommunityCategoryType.ISSUE,
            region = RegionType.METROPOLITAN,
            subwayLineId = 1,
            images = listOf(ImageDto.of(1L, "url1"), ImageDto.of(2L, "url2"))
        )

        given(communityPostUseCase.createCommunityPost(any()))
            .willReturn(response)

        // when
        val result = mockMvc.perform(
            multipart("/v1/community-posts")
                .file("imageFiles", MockMultipartFile("files", "file1.txt", MediaType.TEXT_PLAIN_VALUE, "File 1 Content".toByteArray()).bytes)
                .queryParam("title", "생성할 제목")
                .queryParam("content", "생성할 내용")
                .queryParam("categoryType", CommunityCategoryType.ISSUE.name)
                .queryParam("subwayLineId", "1")
                .queryParam("hashTags", "여행, 취미")
                .header("Authorization", "Bearer <Access Token>")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
            .andDo(
                document(
                    "create-community-post",
                    getDocsRequest(),
                    getDocsResponse(),
                    requestHeaders(
                        headerWithName("Authorization").description("엑세스 토큰")
                    ),
                    queryParameters(
                        parameterWithName("title").description("생성할 제목"),
                        parameterWithName("content").description("생성할 내용"),
                        parameterWithName("categoryType").description("카테고리 타입").attributes(getFormatAttribute("FREE, INSIGHT, ISSUE, HUMOR")),
                        parameterWithName("subwayLineId").description("지하철 노선 ID"),
                        parameterWithName("hashTags").description("해시 태그 목록").optional(),
                    ),
                    requestParts(
                        partWithName("imageFiles").description("이미지 파일").optional(),
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.id").type(JsonFieldType.NUMBER).description("생성된 게시글 아이디"),
                        fieldWithPath("result.title").type(JsonFieldType.STRING).description("생성된 게시글 제목"),
                        fieldWithPath("result.content").type(JsonFieldType.STRING).description("생성된 게시글 내용"),
                        fieldWithPath("result.categoryType").type("CategoryType").description("카테고리 타입").attributes(getFormatAttribute("FREE, INSIGHT, ISSUE, HUMOR")),
                        fieldWithPath("result.region").type(JsonFieldType.STRING).description("지역"),
                        fieldWithPath("result.subwayLineId").type(JsonFieldType.NUMBER).description("지하철 노선 ID"),
                        fieldWithPath("result.images[]").type(JsonFieldType.ARRAY).description("등록된 이미지 목록"),
                        fieldWithPath("result.images[].imageId").type(JsonFieldType.NUMBER).description("등록된 이미지 ID"),
                        fieldWithPath("result.images[].imageUrl").type(JsonFieldType.STRING).description("등록된 이미지 URI"),
                    )
                )
            )
    }

    @Test
    fun updateCommunityPostTest() {
        // given
        val response = UpdateCommunityPostDto.Response(
            id = 1,
            title = "변경된 제목",
            content = "변경된 내용",
            categoryType = CommunityCategoryType.ISSUE,
            images = listOf(ImageDto.of(3L, "url3"))
        )

        given(communityPostUseCase.updateCommunityPost(any()))
            .willReturn(response)

        // when
        val result = mockMvc.perform(
             multipart("/v1/community-posts/{postId}", 1)
                 .file("uploadFiles", MockMultipartFile("files", "file1.txt", MediaType.TEXT_PLAIN_VALUE, "File 1 Content".toByteArray()).bytes)
                 .queryParam("title", "변경할 제목")
                 .queryParam("content", "변경할 내용")
                 .queryParam("categoryType", CommunityCategoryType.ISSUE.name)
                 .queryParam("hashTags", "여행, 취미")
                 .queryParam("removeFileIds", "1, 2")
                 .header("Authorization", "Bearer <Access Token>")
                 .contentType(MediaType.MULTIPART_FORM_DATA)
                 .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
            .andDo(
                document(
                    "update-community-post",
                    getDocsRequest(),
                    getDocsResponse(),
                    requestHeaders(
                        headerWithName("Authorization").description("엑세스 토큰")
                    ),
                    pathParameters(
                        parameterWithName("postId").description("게시물 아이디")
                    ),
                    queryParameters(
                        parameterWithName("title").description("변경할 제목"),
                        parameterWithName("content").description("변경할 내용"),
                        parameterWithName("categoryType").description("변경할 카테고리 타입").attributes(getFormatAttribute("FREE, INSIGHT, ISSUE, HUMOR")),
                        parameterWithName("hashTags").description("해시 태그 목록").optional(),
                        parameterWithName("removeFileIds").description("삭제할 파일 ID 목록")
                    ),
                    requestParts(
                        partWithName("uploadFiles").description("이미지 파일").optional(),
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.id").type(JsonFieldType.NUMBER).description("게시글 아이디"),
                        fieldWithPath("result.title").type(JsonFieldType.STRING).description("변경된 게시글 제목"),
                        fieldWithPath("result.content").type(JsonFieldType.STRING).description("변경된 게시글 내용"),
                        fieldWithPath("result.categoryType").type("CategoryType").description("변경된 카테고리 타입").attributes(getFormatAttribute("FREE, INSIGHT, ISSUE, HUMOR")),
                        fieldWithPath("result.images[]").type(JsonFieldType.ARRAY).description("등록된 이미지 목록"),
                        fieldWithPath("result.images[].imageId").type(JsonFieldType.NUMBER).description("등록된 이미지 ID"),
                        fieldWithPath("result.images[].imageUrl").type(JsonFieldType.STRING).description("등록된 이미지 URI"),
                    )
                )
            )
    }

    @Test
    fun deleteCommunityPostTest() {
        // given
        val response = DeleteCommunityPostDto.Response(
            id = 1
        )

        given(communityPostUseCase.deleteCommunityPost(any()))
            .willReturn(response)

        // when
        val result = mockMvc.perform(
            delete("/v1/community-posts/{postId}", 1)
                .header("Authorization", "Bearer <Access Token>")
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
            .andDo(
                document(
                    "delete-community-post",
                    getDocsRequest(),
                    getDocsResponse(),
                    requestHeaders(
                        headerWithName("Authorization").description("엑세스 토큰")
                    ),
                    pathParameters(
                        parameterWithName("postId").description("삭제할 게시물 아이디")
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.id").type(JsonFieldType.NUMBER).description("삭제된 게시글 아이디"),
                    )
                )
            )
    }
}
