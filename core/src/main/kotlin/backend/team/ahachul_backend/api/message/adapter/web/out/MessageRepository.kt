package backend.team.ahachul_backend.api.message.adapter.web.out

import backend.team.ahachul_backend.api.message.domain.entity.MessageEntity
import backend.team.ahachul_backend.common.domain.model.YNType
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface MessageRepository : JpaRepository<MessageEntity, Long> {

    @EntityGraph(attributePaths = ["senderMember"])
    fun findByMessageRoomIdOrderByIdDesc(roomId: Long, pageable: Pageable): List<MessageEntity>

    @EntityGraph(attributePaths = ["senderMember"])
    fun findByMessageRoomIdAndIdLessThanOrderByIdDesc(
        roomId: Long,
        id: Long,
        pageable: Pageable,
    ): List<MessageEntity>

    fun countByMessageRoomIdAndReadYnAndSenderMemberIdNot(
        roomId: Long,
        readYn: YNType,
        senderMemberId: Long,
    ): Long

    @EntityGraph(attributePaths = ["senderMember"])
    fun findByMessageRoomIdAndReadYnAndSenderMemberIdNot(
        roomId: Long,
        readYn: YNType,
        senderMemberId: Long,
    ): List<MessageEntity>
}
