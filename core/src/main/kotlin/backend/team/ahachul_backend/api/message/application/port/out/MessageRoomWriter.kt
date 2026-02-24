package backend.team.ahachul_backend.api.message.application.port.out

import backend.team.ahachul_backend.api.message.domain.entity.MessageRoomEntity

interface MessageRoomWriter {

    fun save(messageRoomEntity: MessageRoomEntity): MessageRoomEntity
}
