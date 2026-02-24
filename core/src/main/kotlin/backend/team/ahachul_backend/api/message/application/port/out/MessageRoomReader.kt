package backend.team.ahachul_backend.api.message.application.port.out

import backend.team.ahachul_backend.api.message.domain.entity.MessageRoomEntity

interface MessageRoomReader {

    fun findByMember(memberId: Long): List<MessageRoomEntity>

    fun getById(roomId: Long): MessageRoomEntity

    fun findByMemberPair(memberAId: Long, memberBId: Long): MessageRoomEntity?
}
