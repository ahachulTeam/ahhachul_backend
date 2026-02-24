package backend.team.ahachul_backend.api.message.adapter.web.`in`

import backend.team.ahachul_backend.api.message.adapter.web.`in`.dto.GetMessageRoomMessagesDto
import backend.team.ahachul_backend.api.message.adapter.web.`in`.dto.GetMessageRoomsDto
import backend.team.ahachul_backend.api.message.adapter.web.`in`.dto.SendMessageDto
import backend.team.ahachul_backend.api.message.application.port.`in`.MessageUseCase
import backend.team.ahachul_backend.common.domain.model.YNType
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
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.restdocs.request.RequestDocumentation.queryParameters
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(MessageController::class)
class MessageControllerDocsTest : CommonDocsTestConfig() {

    @MockBean
    lateinit var messageUseCase: MessageUseCase

    @Test
    fun getMessageRoomsTest() {
        // given
        val response = GetMessageRoomsDto.Response(
            rooms = listOf(
                GetMessageRoomsDto.Room(
                    roomId = 1L,
                    partnerMemberId = 22L,
                    partnerNickname = "아하철러22",
                    lastMessageContent = "늦을 것 같아요",
                    lastMessageAt = "2026-02-24 15:10:10.010",
                    unreadCount = 2L,
                ),
            ),
        )

        given(messageUseCase.getMessageRooms()).willReturn(response)

        // when
        val result = mockMvc.perform(
            get("/v1/message-rooms")
                .header("Authorization", "Bearer <Access Token>")
                .accept(MediaType.APPLICATION_JSON),
        )

        // then
        result.andExpect(status().isOk)
            .andDo(
                document(
                    "get-message-rooms",
                    getDocsRequest(),
                    getDocsResponse(),
                    requestHeaders(
                        headerWithName("Authorization").description("엑세스 토큰"),
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.rooms").type(JsonFieldType.ARRAY).description("쪽지방 목록"),
                        fieldWithPath("result.rooms[].roomId").type(JsonFieldType.NUMBER).description("쪽지방 ID"),
                        fieldWithPath("result.rooms[].partnerMemberId").type(JsonFieldType.NUMBER).description("상대 멤버 ID"),
                        fieldWithPath("result.rooms[].partnerNickname").type(JsonFieldType.STRING).description("상대 닉네임"),
                        fieldWithPath("result.rooms[].lastMessageContent").type(JsonFieldType.STRING).description("최근 메시지 내용").optional(),
                        fieldWithPath("result.rooms[].lastMessageAt").type(JsonFieldType.STRING).description("최근 메시지 시각").optional(),
                        fieldWithPath("result.rooms[].unreadCount").type(JsonFieldType.NUMBER).description("읽지 않은 메시지 수"),
                    ),
                ),
            )
    }

    @Test
    fun getMessageRoomMessagesTest() {
        // given
        val response = GetMessageRoomMessagesDto.Response(
            roomId = 1L,
            partnerMemberId = 22L,
            partnerNickname = "아하철러22",
            hasNext = true,
            nextCursorId = 100L,
            messages = listOf(
                GetMessageRoomMessagesDto.Message(
                    messageId = 101L,
                    senderMemberId = 22L,
                    senderNickname = "아하철러22",
                    content = "지금 10분 지연이에요",
                    createdAt = "2026-02-24 15:12:00.000",
                    mine = false,
                    readYn = YNType.Y,
                ),
            ),
        )

        given(messageUseCase.getMessageRoomMessages(1L, 120L, 30)).willReturn(response)

        // when
        val result = mockMvc.perform(
            get("/v1/message-rooms/{roomId}/messages", 1)
                .queryParam("cursorId", "120")
                .queryParam("pageSize", "30")
                .header("Authorization", "Bearer <Access Token>")
                .accept(MediaType.APPLICATION_JSON),
        )

        // then
        result.andExpect(status().isOk)
            .andDo(
                document(
                    "get-message-room-messages",
                    getDocsRequest(),
                    getDocsResponse(),
                    requestHeaders(
                        headerWithName("Authorization").description("엑세스 토큰"),
                    ),
                    pathParameters(
                        parameterWithName("roomId").description("쪽지방 ID"),
                    ),
                    queryParameters(
                        parameterWithName("cursorId").description("다음 페이지 조회 커서 ID").optional(),
                        parameterWithName("pageSize").description("페이지 사이즈(기본 30)"),
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.roomId").type(JsonFieldType.NUMBER).description("쪽지방 ID"),
                        fieldWithPath("result.partnerMemberId").type(JsonFieldType.NUMBER).description("상대 멤버 ID"),
                        fieldWithPath("result.partnerNickname").type(JsonFieldType.STRING).description("상대 닉네임"),
                        fieldWithPath("result.hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 존재 여부"),
                        fieldWithPath("result.nextCursorId").type(JsonFieldType.NUMBER).description("다음 조회 커서 ID").optional(),
                        fieldWithPath("result.messages").type(JsonFieldType.ARRAY).description("메시지 목록"),
                        fieldWithPath("result.messages[].messageId").type(JsonFieldType.NUMBER).description("메시지 ID"),
                        fieldWithPath("result.messages[].senderMemberId").type(JsonFieldType.NUMBER).description("발신자 멤버 ID"),
                        fieldWithPath("result.messages[].senderNickname").type(JsonFieldType.STRING).description("발신자 닉네임"),
                        fieldWithPath("result.messages[].content").type(JsonFieldType.STRING).description("메시지 본문"),
                        fieldWithPath("result.messages[].createdAt").type(JsonFieldType.STRING).description("메시지 작성 시각"),
                        fieldWithPath("result.messages[].mine").type(JsonFieldType.BOOLEAN).description("내가 보낸 메시지 여부"),
                        fieldWithPath("result.messages[].readYn").type(JsonFieldType.STRING).description("읽음 여부(Y/N)"),
                    ),
                ),
            )
    }

    @Test
    fun sendMessageTest() {
        // given
        val request = SendMessageDto.Request(
            roomId = 1L,
            receiverMemberId = null,
            content = "곧 도착할게요",
        )

        val response = SendMessageDto.Response(
            roomId = 1L,
            messageId = 110L,
            createdAt = "2026-02-24 15:13:00.000",
        )

        given(messageUseCase.sendMessage(any())).willReturn(response)

        // when
        val result = mockMvc.perform(
            post("/v1/message-rooms/messages")
                .header("Authorization", "Bearer <Access Token>")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON),
        )

        // then
        result.andExpect(status().isOk)
            .andDo(
                document(
                    "send-message",
                    getDocsRequest(),
                    getDocsResponse(),
                    requestHeaders(
                        headerWithName("Authorization").description("엑세스 토큰"),
                    ),
                    requestFields(
                        fieldWithPath("roomId").type(JsonFieldType.NUMBER).description("기존 쪽지방 ID").optional(),
                        fieldWithPath("receiverMemberId").type(JsonFieldType.NUMBER).description("새 대화 시작 시 상대 멤버 ID").optional(),
                        fieldWithPath("content").type(JsonFieldType.STRING).description("메시지 내용"),
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.roomId").type(JsonFieldType.NUMBER).description("쪽지방 ID"),
                        fieldWithPath("result.messageId").type(JsonFieldType.NUMBER).description("생성된 메시지 ID"),
                        fieldWithPath("result.createdAt").type(JsonFieldType.STRING).description("생성 시각"),
                    ),
                ),
            )
    }
}
