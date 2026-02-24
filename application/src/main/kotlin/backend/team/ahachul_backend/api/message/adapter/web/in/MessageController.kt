package backend.team.ahachul_backend.api.message.adapter.web.`in`

import backend.team.ahachul_backend.api.message.adapter.web.`in`.dto.GetMessageRoomMessagesDto
import backend.team.ahachul_backend.api.message.adapter.web.`in`.dto.GetMessageRoomsDto
import backend.team.ahachul_backend.api.message.adapter.web.`in`.dto.SendMessageDto
import backend.team.ahachul_backend.api.message.application.port.`in`.MessageUseCase
import backend.team.ahachul_backend.common.annotation.Authentication
import backend.team.ahachul_backend.common.response.CommonResponse
import org.springframework.web.bind.annotation.*

@RestController
class MessageController(
    private val messageUseCase: MessageUseCase,
) {

    @Authentication
    @GetMapping("/v1/message-rooms")
    fun getMessageRooms(): CommonResponse<GetMessageRoomsDto.Response> {
        return CommonResponse.success(messageUseCase.getMessageRooms())
    }

    @Authentication
    @GetMapping("/v1/message-rooms/{roomId}/messages")
    fun getMessageRoomMessages(
        @PathVariable roomId: Long,
        @RequestParam(required = false) cursorId: Long?,
        @RequestParam(defaultValue = "30") pageSize: Int,
    ): CommonResponse<GetMessageRoomMessagesDto.Response> {
        return CommonResponse.success(messageUseCase.getMessageRoomMessages(roomId, cursorId, pageSize))
    }

    @Authentication
    @PostMapping("/v1/message-rooms/messages")
    fun sendMessage(@RequestBody request: SendMessageDto.Request): CommonResponse<SendMessageDto.Response> {
        return CommonResponse.success(messageUseCase.sendMessage(request.toCommand()))
    }
}
