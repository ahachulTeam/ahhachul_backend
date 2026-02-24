package backend.team.ahachul_backend.api.message.application.port.`in`

import backend.team.ahachul_backend.api.message.adapter.web.`in`.dto.GetMessageRoomMessagesDto
import backend.team.ahachul_backend.api.message.adapter.web.`in`.dto.GetMessageRoomsDto
import backend.team.ahachul_backend.api.message.adapter.web.`in`.dto.SendMessageDto
import backend.team.ahachul_backend.api.message.application.command.`in`.SendMessageCommand

interface MessageUseCase {

    fun getMessageRooms(): GetMessageRoomsDto.Response

    fun getMessageRoomMessages(roomId: Long, cursorId: Long?, pageSize: Int): GetMessageRoomMessagesDto.Response

    fun sendMessage(command: SendMessageCommand): SendMessageDto.Response
}
