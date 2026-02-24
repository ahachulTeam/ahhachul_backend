package backend.team.ahachul_backend.api.message.adapter.web.`in`.dto

import backend.team.ahachul_backend.common.domain.model.YNType

class GetMessageRoomMessagesDto {

    data class Response(
        val roomId: Long,
        val partnerMemberId: Long,
        val partnerNickname: String,
        val hasNext: Boolean,
        val nextCursorId: Long?,
        val messages: List<Message>,
    )

    data class Message(
        val messageId: Long,
        val senderMemberId: Long,
        val senderNickname: String,
        val content: String,
        val createdAt: String,
        val mine: Boolean,
        val readYn: YNType,
    )
}
