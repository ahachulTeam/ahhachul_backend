package backend.team.ahachul_backend.api.complaint.adapter.`in`

import backend.team.ahachul_backend.api.complaint.adapter.`in`.dto.SearchComplaintMessagesDto
import backend.team.ahachul_backend.api.complaint.application.port.`in`.ComplaintUseCase
import backend.team.ahachul_backend.api.complaint.domain.model.ComplaintMessageStatusType
import backend.team.ahachul_backend.api.complaint.domain.model.ComplaintType
import backend.team.ahachul_backend.api.complaint.domain.model.ShortContentType
import backend.team.ahachul_backend.common.dto.ImageDto
import backend.team.ahachul_backend.config.controller.CommonDocsTestConfig
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.willDoNothing
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.restdocs.headers.HeaderDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.time.LocalDateTime

@WebMvcTest(ComplaintController::class)
class ComplaintControllerDocsTest: CommonDocsTestConfig() {

    @MockBean
    lateinit var complaintUseCase: ComplaintUseCase

    @Test
    fun searchComplaintMessagesTest() {
        // given
        val response = SearchComplaintMessagesDto.Response(
            true,
            1,
            listOf(
                SearchComplaintMessagesDto.ComplaintMessage(
                    content = "내용",
                    complainType = ComplaintType.TEMPERATURE_CONTROL,
                    shortContentType = ShortContentType.TOO_COLD,
                    complaintMessageStatusType = ComplaintMessageStatusType.CREATED,
                    subwayLineId = 1L,
                    trainNo = "4323",
                    location = 1,
                    phoneNumber = "010-1234-1234",
                    createdAt = LocalDateTime.now(),
                    createdBy = "작성자 ID",
                    writer = "작성자 닉네임",
                    images = listOf(
                        ImageDto(1, "imageUrl1"),
                        ImageDto(2, "imageUrl2")
                    )
                )
            )
        )

        given(complaintUseCase.searchComplaintMessages((any()))).willReturn(response)

        // when
        val result = mockMvc.perform(
            get("/v1/complaints/messages")
                .param("page", "1")
                .param("size", "10")
                .param("sort", "createdAt,desc")
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(
                MockMvcRestDocumentation.document(
                    "search-complaint-messages",
                    getDocsRequest(),
                    getDocsResponse(),
                    RequestDocumentation.queryParameters(
                        RequestDocumentation.parameterWithName("page").description("현재 페이지"),
                        RequestDocumentation.parameterWithName("size").description("페이지 노출 데이터 수. index 0부터 시작"),
                        RequestDocumentation.parameterWithName("sort").description("정렬 조건").attributes(getFormatAttribute("(likes|createdAt|views),(asc|desc)")),
                    ),
                    PayloadDocumentation.responseFields(
                        *commonResponseFields(),
                        PayloadDocumentation.fieldWithPath("result.hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 존재 여부"),
                        PayloadDocumentation.fieldWithPath("result.nextPageNum").type(JsonFieldType.NUMBER).description("다음 페이지 인덱스"),
                        PayloadDocumentation.fieldWithPath("result.complaintMessages[].content").type(JsonFieldType.STRING).description("민원 내용").optional(),
                        PayloadDocumentation.fieldWithPath("result.complaintMessages[].complainType").type(JsonFieldType.STRING).description("민원 타입(ComplainType)"),
                        PayloadDocumentation.fieldWithPath("result.complaintMessages[].shortContentType").type(JsonFieldType.STRING).description("민원 짧은 내용 타입(ShortContentType)"),
                        PayloadDocumentation.fieldWithPath("result.complaintMessages[].complaintMessageStatusType").type(JsonFieldType.STRING).description("민원 상태(ComplaintMessageStatusType)"),
                        PayloadDocumentation.fieldWithPath("result.complaintMessages[].subwayLineId").type(JsonFieldType.NUMBER).description("지하철 노선 ID"),
                        PayloadDocumentation.fieldWithPath("result.complaintMessages[].trainNo").type(JsonFieldType.STRING).description("열차 번호"),
                        PayloadDocumentation.fieldWithPath("result.complaintMessages[].location").type(JsonFieldType.NUMBER).description("열차 칸"),
                        PayloadDocumentation.fieldWithPath("result.complaintMessages[].phoneNumber").type(JsonFieldType.STRING).description("전화번호").optional(),
                        PayloadDocumentation.fieldWithPath("result.complaintMessages[].createdAt").type("LocalDateTime").description("작성일자"),
                        PayloadDocumentation.fieldWithPath("result.complaintMessages[].createdBy").type(JsonFieldType.STRING).description("작성자 ID"),
                        PayloadDocumentation.fieldWithPath("result.complaintMessages[].writer").type(JsonFieldType.STRING).description("작성자 닉네임"),
                        PayloadDocumentation.fieldWithPath("result.complaintMessages[].images[].imageId").type(JsonFieldType.NUMBER).description("이미지 ID"),
                        PayloadDocumentation.fieldWithPath("result.complaintMessages[].images[].imageUrl").type(JsonFieldType.STRING).description("이미지 URL"),
                    )
                )
            )
    }

    @Test
    fun sendComplaintMessageTest() {
        // given
        willDoNothing().given(complaintUseCase).sendComplaintMessage(any())

        // when
        val result = mockMvc.perform(
            multipart("/v1/complaints/messages")
                .file("imageFiles", MockMultipartFile("files", "file1.txt", MediaType.TEXT_PLAIN_VALUE, "File 1 Content".toByteArray()).bytes)
                .queryParam("content", "너무 더워요!")
                .queryParam("complainType", ComplaintType.TEMPERATURE_CONTROL.toString())
                .queryParam("shortContentType", ShortContentType.TOO_COLD.toString())
                .queryParam("phoneNumber", "02-234-5678")
                .queryParam("trainNo", "3243")
                .queryParam("location", "2")
                .queryParam("subwayLineId", "1")
                .header("Authorization", "Bearer <Access Token>")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(
                MockMvcRestDocumentation.document(
                    "send-complaint-message",
                    getDocsRequest(),
                    getDocsResponse(),
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("엑세스 토큰")
                    ),
                    RequestDocumentation.queryParameters(
                        RequestDocumentation.parameterWithName("content").description("보낸 내용").optional(),
                        RequestDocumentation.parameterWithName("complainType").description("민원 타입(ComplaintType)"),
                        RequestDocumentation.parameterWithName("shortContentType").description("민원 짧은 내용 타입(ShortContentType)"),
                        RequestDocumentation.parameterWithName("phoneNumber").description("보낸 전화번호").optional(),
                        RequestDocumentation.parameterWithName("trainNo").description("보낼 때 사용한 열차 번호").optional(),
                        RequestDocumentation.parameterWithName("location").description("열차 칸").optional(),
                        RequestDocumentation.parameterWithName("subwayLineId").description("보낸 노선 ID").optional(),
                    ),
                    RequestDocumentation.requestParts(
                        RequestDocumentation.partWithName("imageFiles").description("이미지 파일").optional(),
                    ),
                )
            )
    }
}
