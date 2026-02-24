package backend.team.ahachul_backend.api.message.domain.entity

import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity
import backend.team.ahachul_backend.common.domain.entity.BaseEntity
import backend.team.ahachul_backend.common.domain.model.YNType
import jakarta.persistence.*

@Entity
@Table(name = "tb_message")
class MessageEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_room_id")
    val messageRoom: MessageRoomEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_member_id")
    val senderMember: MemberEntity,

    var content: String,

    @Enumerated(EnumType.STRING)
    var readYn: YNType = YNType.N,
) : BaseEntity() {

    companion object {
        fun of(messageRoom: MessageRoomEntity, senderMember: MemberEntity, content: String): MessageEntity {
            return MessageEntity(
                messageRoom = messageRoom,
                senderMember = senderMember,
                content = content,
            )
        }
    }

    fun markRead() {
        readYn = YNType.Y
    }
}
