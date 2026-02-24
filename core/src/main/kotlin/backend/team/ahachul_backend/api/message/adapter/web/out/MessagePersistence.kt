package backend.team.ahachul_backend.api.message.adapter.web.out

import backend.team.ahachul_backend.api.message.application.port.out.MessageReader
import backend.team.ahachul_backend.api.message.application.port.out.MessageRoomReader
import backend.team.ahachul_backend.api.message.application.port.out.MessageRoomWriter
import backend.team.ahachul_backend.api.message.application.port.out.MessageWriter
import backend.team.ahachul_backend.api.message.domain.entity.MessageEntity
import backend.team.ahachul_backend.api.message.domain.entity.MessageRoomEntity
import backend.team.ahachul_backend.common.domain.model.YNType
import backend.team.ahachul_backend.common.exception.AdapterException
import backend.team.ahachul_backend.common.response.ResponseCode
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component

@Component
class MessagePersistence(
    private val messageRoomRepository: MessageRoomRepository,
    private val messageRepository: MessageRepository,
) : MessageRoomReader, MessageRoomWriter, MessageReader, MessageWriter {

    override fun findByMember(memberId: Long): List<MessageRoomEntity> {
        return messageRoomRepository.findByMemberAIdOrMemberBId(memberId, memberId)
    }

    override fun getById(roomId: Long): MessageRoomEntity {
        return messageRoomRepository.findById(roomId).orElseThrow {
            AdapterException(ResponseCode.MESSAGE_ROOM_NOT_FOUND)
        }
    }

    override fun findByMemberPair(memberAId: Long, memberBId: Long): MessageRoomEntity? {
        return messageRoomRepository.findByMemberAIdAndMemberBId(memberAId, memberBId)
    }

    override fun save(messageRoomEntity: MessageRoomEntity): MessageRoomEntity {
        return messageRoomRepository.save(messageRoomEntity)
    }

    override fun getMessages(roomId: Long, cursorId: Long?, pageSize: Int): List<MessageEntity> {
        val pageable = PageRequest.of(0, pageSize)

        return if (cursorId == null) {
            messageRepository.findByMessageRoomIdOrderByIdDesc(roomId, pageable)
        } else {
            messageRepository.findByMessageRoomIdAndIdLessThanOrderByIdDesc(roomId, cursorId, pageable)
        }
    }

    override fun countUnreadMessages(roomId: Long, memberId: Long): Long {
        return messageRepository.countByMessageRoomIdAndReadYnAndSenderMemberIdNot(
            roomId = roomId,
            readYn = YNType.N,
            senderMemberId = memberId,
        )
    }

    override fun findUnreadMessages(roomId: Long, memberId: Long): List<MessageEntity> {
        return messageRepository.findByMessageRoomIdAndReadYnAndSenderMemberIdNot(
            roomId = roomId,
            readYn = YNType.N,
            senderMemberId = memberId,
        )
    }

    override fun save(messageEntity: MessageEntity): MessageEntity {
        return messageRepository.save(messageEntity)
    }

    override fun saveAll(messageEntities: List<MessageEntity>) {
        messageRepository.saveAll(messageEntities)
    }
}
