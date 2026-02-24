package backend.team.ahachul_backend.api.message.application.port.out

import backend.team.ahachul_backend.api.message.domain.entity.MessageEntity

interface MessageWriter {

    fun save(messageEntity: MessageEntity): MessageEntity

    fun saveAll(messageEntities: List<MessageEntity>)
}
