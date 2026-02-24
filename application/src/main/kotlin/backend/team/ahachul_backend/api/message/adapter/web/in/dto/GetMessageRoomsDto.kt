package backend.team.ahachul_backend.api.message.adapter.web.`in`.dto

class GetMessageRoomsDto {

    data class Response(
        val rooms: List<Room>,
    )

    data class Room(
        val roomId: Long,
        val partnerMemberId: Long,
        val partnerNickname: String,
        val lastMessageContent: String?,
        val lastMessageAt: String?,
        val unreadCount: Long,
    )
}
