package backend.team.ahachul_backend.api.message.application.port.out

import backend.team.ahachul_backend.api.message.domain.entity.MessageEntity

interface MessageReader {

    fun getMessages(roomId: Long, cursorId: Long?, pageSize: Int): List<MessageEntity>

    fun countUnreadMessages(roomId: Long, memberId: Long): Long

    fun findUnreadMessages(roomId: Long, memberId: Long): List<MessageEntity>
}
