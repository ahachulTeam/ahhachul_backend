package backend.team.ahachul_backend.api.message.application.command.`in`

class SendMessageCommand(
    val roomId: Long?,
    val receiverMemberId: Long?,
    val content: String,
)
