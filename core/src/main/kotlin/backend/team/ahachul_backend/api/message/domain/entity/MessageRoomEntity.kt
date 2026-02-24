package backend.team.ahachul_backend.api.message.domain.entity

import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity
import backend.team.ahachul_backend.common.domain.entity.BaseEntity
import backend.team.ahachul_backend.common.exception.CommonException
import backend.team.ahachul_backend.common.response.ResponseCode
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "tb_message_room")
class MessageRoomEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_room_id")
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_a_id")
    val memberA: MemberEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_b_id")
    val memberB: MemberEntity,

    var lastMessageContent: String? = null,

    var lastMessageAt: LocalDateTime? = null,
) : BaseEntity() {

    companion object {
        fun of(memberA: MemberEntity, memberB: MemberEntity): MessageRoomEntity {
            return MessageRoomEntity(
                memberA = memberA,
                memberB = memberB,
            )
        }
    }

    fun isParticipant(memberId: Long): Boolean {
        return memberA.id == memberId || memberB.id == memberId
    }

    fun partnerOf(memberId: Long): MemberEntity {
        return when (memberId) {
            memberA.id -> memberB
            memberB.id -> memberA
            else -> throw CommonException(ResponseCode.MESSAGE_ROOM_FORBIDDEN)
        }
    }

    fun updateLastMessage(content: String, createdAt: LocalDateTime) {
        lastMessageContent = content
        lastMessageAt = createdAt
    }
}
