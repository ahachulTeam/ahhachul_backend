package backend.team.ahachul_backend.api.message.adapter.web.out

import backend.team.ahachul_backend.api.message.domain.entity.MessageRoomEntity
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface MessageRoomRepository : JpaRepository<MessageRoomEntity, Long> {

    @EntityGraph(attributePaths = ["memberA", "memberB"])
    fun findByMemberAIdOrMemberBId(memberAId: Long, memberBId: Long): List<MessageRoomEntity>

    @EntityGraph(attributePaths = ["memberA", "memberB"])
    override fun findById(id: Long): Optional<MessageRoomEntity>

    @EntityGraph(attributePaths = ["memberA", "memberB"])
    fun findByMemberAIdAndMemberBId(memberAId: Long, memberBId: Long): MessageRoomEntity?
}
