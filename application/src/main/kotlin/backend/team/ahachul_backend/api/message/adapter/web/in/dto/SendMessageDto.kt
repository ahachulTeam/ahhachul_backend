package backend.team.ahachul_backend.api.message.adapter.web.`in`.dto

import backend.team.ahachul_backend.api.message.application.command.`in`.SendMessageCommand

class SendMessageDto {

    data class Request(
        val roomId: Long? = null,
        val receiverMemberId: Long? = null,
        val content: String,
    ) {
        fun toCommand(): SendMessageCommand {
            return SendMessageCommand(
                roomId = roomId,
                receiverMemberId = receiverMemberId,
                content = content,
            )
        }
    }

    data class Response(
        val roomId: Long,
        val messageId: Long,
        val createdAt: String,
    )
}
