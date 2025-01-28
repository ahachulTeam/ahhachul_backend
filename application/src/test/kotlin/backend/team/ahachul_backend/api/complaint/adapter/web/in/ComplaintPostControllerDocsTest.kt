package backend.team.ahachul_backend.api.complaint.adapter.web.`in`

import backend.team.ahachul_backend.api.complaint.adapter.web.`in`.dto.*
import backend.team.ahachul_backend.api.complaint.application.port.`in`.ComplaintPostUseCase
import backend.team.ahachul_backend.api.complaint.domain.model.ComplaintPostType
import backend.team.ahachul_backend.api.complaint.domain.model.ComplaintType
import backend.team.ahachul_backend.api.complaint.domain.model.ShortContentType
import backend.team.ahachul_backend.common.dto.ImageDto
import backend.team.ahachul_backend.common.dto.PageInfoDto
import backend.team.ahachul_backend.config.controller.CommonDocsTestConfig
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.anyLong
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

@WebMvcTest(ComplaintPostController::class)
class ComplaintPostControllerDocsTest : CommonDocsTestConfig() {

    @MockBean
    lateinit var complaintPostUseCase: ComplaintPostUseCase

    @Test
    fun searchComplaintPostsTest() {
        // given
        val response = PageInfoDto.of(
            data = listOf(
                SearchComplaintPostDto.Response(
                    id = 1,
                    complaintType = ComplaintType.EMERGENCY_PATIENT,
                    shortContentType = ShortContentType.WITNESS,
                    content = "민원 내용",
                    phoneNumber = "010-1234-5678",
                    trainNo = "12345",
                    location = 1,
                    status = ComplaintPostType.CREATED,
                    commentCnt = 0,
                    subwayLineId = 1L,
                    createdBy = "1",
                    createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")),
                    writer = "닉네임",
                    image = ImageDto(1, "https://img.png"),
                )
            ),
            pageSize = 1,
            arrayOf(SearchComplaintPostDto.Response::createdAt, SearchComplaintPostDto.Response::id)
        )

        given(complaintPostUseCase.searchComplaintPosts(any()))
            .willReturn(response)

        // when
        val result = mockMvc.perform(
            get("/v1/complaint-posts")
                .queryParam("subwayLineId", "1")
                .queryParam("keyword", "검색 키워드 이름")
                .queryParam("pageToken", "MTIzMTI5MTU6MTI=")
                .queryParam("pageSize", "10")
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
            .andDo(
                document(
                    "search-complaint-posts",
                    getDocsRequest(),
                    getDocsResponse(),
                    queryParameters(
                        parameterWithName("subwayLineId").description("민원 호선").optional(),
                        parameterWithName("keyword").description("검색 키워드 명칭").optional(),
                        parameterWithName("pageToken").description("base64로 인코딩 된 페이지 토큰 문자열").optional(),
                        parameterWithName("pageSize").description("페이지 노출 데이터 수. index 0부터 시작"),
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.hasNext").type(JsonFieldType.BOOLEAN).description("다음 민원 포스트 존재 여부"),
                        fieldWithPath("result.pageToken").type(JsonFieldType.STRING).description("다음 민원을 가져오기 위한 base64로 인코딩 된 페이지 토큰 문자열").optional(),
                        fieldWithPath("result.data[].id").type(JsonFieldType.NUMBER).description("민원 아이디"),
                        fieldWithPath("result.data[].complaintType").type(JsonFieldType.STRING).description("민원 타입 대분류").attributes(getFormatAttribute("민원 코드 - 민원 타입 (대분류)")),
                        fieldWithPath("result.data[].shortContentType").type(JsonFieldType.STRING).description("민원 타입 소분류").attributes(getFormatAttribute("민원 코드 - 민원 타입 (소분류)")),
                        fieldWithPath("result.data[].content").type(JsonFieldType.STRING).description("민원 내용"),
                        fieldWithPath("result.data[].phoneNumber").type(JsonFieldType.STRING).description("민원 전화번호"),
                        fieldWithPath("result.data[].trainNo").type(JsonFieldType.STRING).description("민원 열차 번호"),
                        fieldWithPath("result.data[].location").type(JsonFieldType.NUMBER).description("민원 열차 칸"),
                        fieldWithPath("result.data[].status").type(JsonFieldType.STRING).description("민원 상태").attributes(getFormatAttribute("민원 코드 - 민원 상태")),
                        fieldWithPath("result.data[].commentCnt").type(JsonFieldType.NUMBER).description("민원 댓글 수"),
                        fieldWithPath("result.data[].subwayLineId").type(JsonFieldType.NUMBER).description("민원 지하철 노선"),
                        fieldWithPath("result.data[].createdBy").type(JsonFieldType.STRING).description("작성자 ID"),
                        fieldWithPath("result.data[].createdAt").type(JsonFieldType.STRING).description("작성일자"),
                        fieldWithPath("result.data[].writer").type(JsonFieldType.STRING).description("작성자 닉네임"),
                        fieldWithPath("result.data[].image").type(JsonFieldType.OBJECT).description("등록된 이미지"),
                        fieldWithPath("result.data[].image.imageId").type(JsonFieldType.NUMBER).description("등록된 첫 번쨰 이미지 ID"),
                        fieldWithPath("result.data[].image.imageUrl").type(JsonFieldType.STRING).description("등록된 첫 번째 이미지 URI"),
                    )
                )
            )
    }

    @Test
    fun getComplaintPostTest() {
        // given
        val response = GetComplaintPostDto.Response(
            id = 1,
            complaintType = ComplaintType.EMERGENCY_PATIENT,
            shortContentType = ShortContentType.WITNESS,
            content = "민원 내용",
            phoneNumber = "010-1234-5678",
            trainNo = "12345",
            location = 1,
            status = ComplaintPostType.CREATED,
            commentCnt = 0,
            subwayLineId = 1L,
            createdBy = "1",
            createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")),
            writer = "닉네임",
            images = listOf(ImageDto(1, "https://img.png")),
        )

        given(complaintPostUseCase.getComplaintPost(anyLong()))
            .willReturn(response)

        // when
        val result = mockMvc.perform(
            get("/v1/complaint-posts/{postId}", 1)
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
            .andDo(
                document(
                    "get-complaint-post",
                    getDocsRequest(),
                    getDocsResponse(),
                    pathParameters(
                        parameterWithName("postId").description("민원 아이디")
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.id").type(JsonFieldType.NUMBER).description("민원 아이디"),
                        fieldWithPath("result.complaintType").type(JsonFieldType.STRING).description("민원 타입 대분류").attributes(getFormatAttribute("민원 코드 - 민원 타입 (대분류)")),
                        fieldWithPath("result.shortContentType").type(JsonFieldType.STRING).description("민원 타입 소분류").attributes(getFormatAttribute("민원 코드 - 민원 타입 (소분류)")),
                        fieldWithPath("result.content").type(JsonFieldType.STRING).description("민원 내용"),
                        fieldWithPath("result.phoneNumber").type(JsonFieldType.STRING).description("민원 전화번호"),
                        fieldWithPath("result.trainNo").type(JsonFieldType.STRING).description("민원 열차 번호"),
                        fieldWithPath("result.location").type(JsonFieldType.NUMBER).description("민원 열차 칸"),
                        fieldWithPath("result.status").type(JsonFieldType.STRING).description("민원 상태").attributes(getFormatAttribute("민원 코드 - 민원 상태")),
                        fieldWithPath("result.commentCnt").type(JsonFieldType.NUMBER).description("민원 댓글 수"),
                        fieldWithPath("result.subwayLineId").type(JsonFieldType.NUMBER).description("민원 지하철 노선"),
                        fieldWithPath("result.createdBy").type(JsonFieldType.STRING).description("작성자 ID"),
                        fieldWithPath("result.createdAt").type(JsonFieldType.STRING).description("작성일자"),
                        fieldWithPath("result.writer").type(JsonFieldType.STRING).description("작성자 닉네임"),
                        fieldWithPath("result.images[]").type(JsonFieldType.ARRAY).description("등록된 이미지 목록"),
                        fieldWithPath("result.images[].imageId").type(JsonFieldType.NUMBER).description("등록된 이미지 ID"),
                        fieldWithPath("result.images[].imageUrl").type(JsonFieldType.STRING).description("등록된 이미지 URI"),

                )
            )
        )
    }

    @Test
    fun createComplaintPostTest() {
        // given
        val response = CreateComplaintPostDto.Response(
            id = 1,
            images = listOf(ImageDto(1, "https://img.png")),
        )

        given(complaintPostUseCase.createComplaintPost(any()))
            .willReturn(response)

        val request = CreateComplaintPostDto.Request(
            complaintType = ComplaintType.EMERGENCY_PATIENT,
            shortContentType = ShortContentType.WITNESS,
            content = "민원 내용",
            phoneNumber = "010-1234-5678",
            trainNo = "1234",
            location = 1,
            subwayLine = 1L
        )

        val mapper = ObjectMapper()
        val requestFile = MockMultipartFile(
            "content",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            mapper.writeValueAsString(request).toByteArray())

        val imageFile = MockMultipartFile(
            "files",
            "image.png",
            MediaType.IMAGE_PNG_VALUE,
            "<< png data >>".toByteArray())

        // when
        val result = mockMvc.perform(
            multipart("/v1/complaint-posts")
                .file(requestFile)
                .file(imageFile)
                .header("Authorization", "Bearer <Access Token>")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON)
        )

        result.andExpect(status().isOk)
            .andDo(document("create-complaint-post",
                getDocsRequest(),
                getDocsResponse(),
                requestHeaders(
                    headerWithName("Authorization").description("엑세스 토큰")
                ),
                requestParts(
                    partWithName("files").description("업로드할 이미지"),
                    partWithName("content").description("request dto")
                ),
                requestPartFields(
                    "content",
                    fieldWithPath("complaintType").type(JsonFieldType.STRING).description("민원 타입 대분류").attributes(getFormatAttribute("민원 코드 - 민원 타입 (대분류)")),
                    fieldWithPath("shortContentType").type(JsonFieldType.STRING).description("민원 타입 소분류").attributes(getFormatAttribute("민원 코드 - 민원 타입 (소분류)")),
                    fieldWithPath("content").type(JsonFieldType.STRING).description("민원 내용"),
                    fieldWithPath("phoneNumber").type(JsonFieldType.STRING).description("민원 전화번호").optional(),
                    fieldWithPath("trainNo").type(JsonFieldType.STRING).description("민원 열차 번호").optional(),
                    fieldWithPath("location").type(JsonFieldType.NUMBER).description("민원 열차 칸").optional(),
                    fieldWithPath("subwayLine").type(JsonFieldType.NUMBER).description("민원 지하철 노선"),
                ),
                responseFields(
                    *commonResponseFields(),
                    fieldWithPath("result.id").type(JsonFieldType.NUMBER).description("저장한 민원 아이디"),
                    fieldWithPath("result.images[]").type(JsonFieldType.ARRAY).description("등록된 이미지 목록"),
                    fieldWithPath("result.images[].imageId").type(JsonFieldType.NUMBER).description("등록된 이미지 ID"),
                    fieldWithPath("result.images[].imageUrl").type(JsonFieldType.STRING).description("등록된 이미지 URI"),
                )
            ))
    }

    @Test
    fun updateComplaintPostTest() {
        // given
        val response = UpdateComplaintPostDto.Response(
            id = 1,
            complaintType = ComplaintType.OTHER_COMPLAINT,
            shortContentType = ShortContentType.DRUNK,
            content = "민원 내용",
            phoneNumber = "010-1234-5678",
            trainNo = "12345",
            location = 1,
            status = ComplaintPostType.IN_PROGRESS,
            subwayLineId = 1L,
        )

        given(complaintPostUseCase.updateComplaintPost(any()))
            .willReturn(response)

        val request = UpdateComplaintPostDto.Request(
            complaintType = ComplaintType.OTHER_COMPLAINT,
            shortContentType = ShortContentType.DRUNK,
            content = "민원 내용",
            phoneNumber = "010-1234-5678",
            trainNo = "12345",
            location = 1,
            status = ComplaintPostType.IN_PROGRESS,
            subwayLineId = 1L,
            removeFileIds = listOf(1L)
        )

        val mapper = ObjectMapper()
        val requestFile = MockMultipartFile(
            "content",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            mapper.writeValueAsString(request).toByteArray())

        val imageFile = MockMultipartFile(
            "files",
            "image.png",
            MediaType.IMAGE_PNG_VALUE,
            "<< png data >>".toByteArray())

        // when
        val result = mockMvc.perform(
            multipart("/v1/complaint-posts/{postId}", 1)
                .file(requestFile)
                .file(imageFile)
                .header("Authorization", "Bearer <Access Token>")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
            .andDo(document("update-complaint-post",
                getDocsRequest(),
                getDocsResponse(),
                requestHeaders(
                    headerWithName("Authorization").description("엑세스 토큰")
                ),
                pathParameters(
                    parameterWithName("postId").description("민원 아이디")
                ),
                requestParts(
                    partWithName("files").description("업로드할 이미지"),
                    partWithName("content").description("request dto")
                ),
                requestPartFields(
                    "content",
                    fieldWithPath("complaintType").type(JsonFieldType.STRING).description("민원 타입 대분류").optional().attributes(getFormatAttribute("민원 코드 - 민원 타입 (대분류)")),
                    fieldWithPath("shortContentType").type(JsonFieldType.STRING).description("민원 타입 소분류").optional().attributes(getFormatAttribute("민원 코드 - 민원 타입 (소분류)")),
                    fieldWithPath("content").type(JsonFieldType.STRING).description("민원 내용").optional(),
                    fieldWithPath("phoneNumber").type(JsonFieldType.STRING).description("민원 전화번호").optional(),
                    fieldWithPath("trainNo").type(JsonFieldType.STRING).description("민원 열차 번호").optional(),
                    fieldWithPath("location").type(JsonFieldType.NUMBER).description("민원 열차 칸").optional(),
                    fieldWithPath("subwayLineId").type(JsonFieldType.NUMBER).description("민원 지하철 노선").optional(),
                    fieldWithPath("status").type(JsonFieldType.STRING).description("민원 상태").optional().attributes(getFormatAttribute("민원 코드 - 민원 상태")),
                    fieldWithPath("removeFileIds").type(JsonFieldType.ARRAY).description("삭제할 민원 이미지 번호 리스트").optional(),
                ),
                responseFields(
                    *commonResponseFields(),
                    fieldWithPath("result.id").type(JsonFieldType.NUMBER).description("민원 아이디"),
                    fieldWithPath("result.complaintType").type(JsonFieldType.STRING).description("민원 타입 대분류").attributes(getFormatAttribute("민원 코드 - 민원 타입 (대분류)")),
                    fieldWithPath("result.shortContentType").type(JsonFieldType.STRING).description("민원 타입 소분류").attributes(getFormatAttribute("민원 코드 - 민원 타입 (소분류)")),
                    fieldWithPath("result.content").type(JsonFieldType.STRING).description("민원 내용"),
                    fieldWithPath("result.phoneNumber").type(JsonFieldType.STRING).description("민원 전화번호"),
                    fieldWithPath("result.trainNo").type(JsonFieldType.STRING).description("민원 열차 번호"),
                    fieldWithPath("result.location").type(JsonFieldType.NUMBER).description("민원 열차 칸"),
                    fieldWithPath("result.status").type(JsonFieldType.STRING).description("민원 상태").attributes(getFormatAttribute("민원 코드 - 민원 상태")),
                    fieldWithPath("result.subwayLineId").type(JsonFieldType.NUMBER).description("민원 지하철 노선"),
                )
            ))
    }

    @Test
    fun updateComplaintPostStatusTest() {
        // given
        val response = UpdateComplaintPostStatusDto.Response(
            id = 1,
            complaintType = ComplaintType.OTHER_COMPLAINT,
            shortContentType = ShortContentType.DRUNK,
            content = "민원 내용",
            phoneNumber = "010-1234-5678",
            trainNo = "12345",
            location = 1,
            status = ComplaintPostType.IN_PROGRESS,
            subwayLineId = 1L,
        )

        given(complaintPostUseCase.updateComplaintPostStatus(any()))
            .willReturn(response)

        val request = UpdateComplaintPostStatusDto.Request(
            status = ComplaintPostType.COMPLETED
        )

        // when
        val result = mockMvc.perform(
            patch("/v1/complaint-posts/{postId}/status", 1L)
                .header("Authorization", "Bearer <Access Token>")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
            .andDo(document("update-complaint-post-status",
                getDocsRequest(),
                getDocsResponse(),
                requestHeaders(
                    headerWithName("Authorization").description("엑세스 토큰")
                ),
                pathParameters(
                    parameterWithName("postId").description("민원 아이디")
                ),
                requestFields(
                    fieldWithPath("status").type(JsonFieldType.STRING).description("민원 상태").attributes(getFormatAttribute("민원 코드 - 민원 상태")),
                ),
                responseFields(
                    *commonResponseFields(),
                    fieldWithPath("result.id").type(JsonFieldType.NUMBER).description("민원 아이디"),
                    fieldWithPath("result.complaintType").type(JsonFieldType.STRING).description("민원 타입 대분류").attributes(getFormatAttribute("민원 코드 - 민원 타입 (대분류)")),
                    fieldWithPath("result.shortContentType").type(JsonFieldType.STRING).description("민원 타입 소분류").attributes(getFormatAttribute("민원 코드 - 민원 타입 (소분류)")),
                    fieldWithPath("result.content").type(JsonFieldType.STRING).description("민원 내용"),
                    fieldWithPath("result.phoneNumber").type(JsonFieldType.STRING).description("민원 전화번호"),
                    fieldWithPath("result.trainNo").type(JsonFieldType.STRING).description("민원 열차 번호"),
                    fieldWithPath("result.location").type(JsonFieldType.NUMBER).description("민원 열차 칸"),
                    fieldWithPath("result.status").type(JsonFieldType.STRING).description("민원 상태").attributes(getFormatAttribute("민원 코드 - 민원 상태")),
                    fieldWithPath("result.subwayLineId").type(JsonFieldType.NUMBER).description("민원 지하철 노선"),
                )
            ))
    }

    @Test
    fun deleteComplaintPostTest() {
        // given
        val response = DeleteComplaintPostDto.Response(
            id = 1
        )

        given(complaintPostUseCase.deleteComplaintPost(anyLong()))
            .willReturn(response)

        // when
        val result = mockMvc.perform(
            delete("/v1/complaint-posts/{postId}", 1L)
                .header("Authorization", "Bearer <Access Token>")
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
            .andDo(document("delete-complaint-post",
                getDocsRequest(),
                getDocsResponse(),
                requestHeaders(
                    headerWithName("Authorization").description("엑세스 토큰")
                ),
                pathParameters(
                    parameterWithName("postId").description("민원 아이디")
                ),
                responseFields(
                    *commonResponseFields(),
                    fieldWithPath("result.id").type(JsonFieldType.NUMBER).description("민원 아이디"),
                )
            ))
    }
}