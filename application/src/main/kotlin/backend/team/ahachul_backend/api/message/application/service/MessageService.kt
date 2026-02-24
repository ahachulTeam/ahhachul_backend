package backend.team.ahachul_backend.api.message.application.service

import backend.team.ahachul_backend.api.member.application.port.out.MemberReader
import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity
import backend.team.ahachul_backend.api.message.adapter.web.`in`.dto.GetMessageRoomMessagesDto
import backend.team.ahachul_backend.api.message.adapter.web.`in`.dto.GetMessageRoomsDto
import backend.team.ahachul_backend.api.message.adapter.web.`in`.dto.SendMessageDto
import backend.team.ahachul_backend.api.message.application.command.`in`.SendMessageCommand
import backend.team.ahachul_backend.api.message.application.port.`in`.MessageUseCase
import backend.team.ahachul_backend.api.message.application.port.out.MessageReader
import backend.team.ahachul_backend.api.message.application.port.out.MessageRoomReader
import backend.team.ahachul_backend.api.message.application.port.out.MessageRoomWriter
import backend.team.ahachul_backend.api.message.application.port.out.MessageWriter
import backend.team.ahachul_backend.api.message.domain.entity.MessageEntity
import backend.team.ahachul_backend.api.message.domain.entity.MessageRoomEntity
import backend.team.ahachul_backend.common.exception.CommonException
import backend.team.ahachul_backend.common.response.ResponseCode
import backend.team.ahachul_backend.common.utils.RequestUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
@Transactional(readOnly = true)
class MessageService(
    private val memberReader: MemberReader,
    private val messageRoomReader: MessageRoomReader,
    private val messageRoomWriter: MessageRoomWriter,
    private val messageReader: MessageReader,
    private val messageWriter: MessageWriter,
) : MessageUseCase {

    companion object {
        const val MAX_MESSAGE_LENGTH = 1000
        const val MIN_PAGE_SIZE = 1
        const val MAX_PAGE_SIZE = 100
        val DATE_TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
    }

    override fun getMessageRooms(): GetMessageRoomsDto.Response {
        val memberId = getMemberId()
        val rooms = messageRoomReader.findByMember(memberId)
            .sortedWith(
                compareByDescending<MessageRoomEntity> { it.lastMessageAt ?: it.createdAt }
                    .thenByDescending { it.id },
            )

        return GetMessageRoomsDto.Response(
            rooms = rooms.map { room ->
                val partner = room.partnerOf(memberId)
                GetMessageRoomsDto.Room(
                    roomId = room.id,
                    partnerMemberId = partner.id,
                    partnerNickname = partner.nickname ?: "알 수 없음",
                    lastMessageContent = room.lastMessageContent,
                    lastMessageAt = room.lastMessageAt?.format(DATE_TIME_FORMATTER),
                    unreadCount = messageReader.countUnreadMessages(room.id, memberId),
                )
            },
        )
    }

    @Transactional
    override fun getMessageRoomMessages(
        roomId: Long,
        cursorId: Long?,
        pageSize: Int,
    ): GetMessageRoomMessagesDto.Response {
        val memberId = getMemberId()
        val room = messageRoomReader.getById(roomId)

        validateRoomParticipant(room, memberId)

        val normalizedPageSize = pageSize.coerceIn(MIN_PAGE_SIZE, MAX_PAGE_SIZE)
        val fetchedMessages = messageReader.getMessages(roomId, cursorId, normalizedPageSize + 1)
        val hasNext = fetchedMessages.size > normalizedPageSize
        val pageMessages = if (hasNext) fetchedMessages.take(normalizedPageSize) else fetchedMessages

        markUnreadMessagesAsRead(roomId, memberId)

        val partner = room.partnerOf(memberId)

        return GetMessageRoomMessagesDto.Response(
            roomId = room.id,
            partnerMemberId = partner.id,
            partnerNickname = partner.nickname ?: "알 수 없음",
            hasNext = hasNext,
            nextCursorId = if (hasNext) pageMessages.last().id else null,
            messages = pageMessages
                .asReversed()
                .map { message ->
                    GetMessageRoomMessagesDto.Message(
                        messageId = message.id,
                        senderMemberId = message.senderMember.id,
                        senderNickname = message.senderMember.nickname ?: "알 수 없음",
                        content = message.content,
                        createdAt = message.createdAt.format(DATE_TIME_FORMATTER),
                        mine = message.senderMember.id == memberId,
                        readYn = message.readYn,
                    )
                },
        )
    }

    @Transactional
    override fun sendMessage(command: SendMessageCommand): SendMessageDto.Response {
        val memberId = getMemberId()
        val sender = memberReader.getMember(memberId)
        val normalizedContent = command.content.trim()

        validateSendRequest(command, normalizedContent)

        val room = resolveMessageRoom(sender, command)
        validateRoomParticipant(room, memberId)

        val message = messageWriter.save(
            MessageEntity.of(
                messageRoom = room,
                senderMember = sender,
                content = normalizedContent,
            ),
        )

        room.updateLastMessage(message.content, message.createdAt)

        return SendMessageDto.Response(
            roomId = room.id,
            messageId = message.id,
            createdAt = message.createdAt.format(DATE_TIME_FORMATTER),
        )
    }

    private fun getMemberId(): Long {
        return RequestUtils.getAttribute(RequestUtils.Attribute.MEMBER_ID)!!.toLong()
    }

    private fun validateRoomParticipant(room: MessageRoomEntity, memberId: Long) {
        if (!room.isParticipant(memberId)) {
            throw CommonException(ResponseCode.MESSAGE_ROOM_FORBIDDEN)
        }
    }

    private fun validateSendRequest(command: SendMessageCommand, content: String) {
        if (command.roomId == null && command.receiverMemberId == null) {
            throw CommonException(ResponseCode.INVALID_MESSAGE_REQUEST)
        }

        if (content.isBlank() || content.length > MAX_MESSAGE_LENGTH) {
            throw CommonException(ResponseCode.INVALID_MESSAGE_REQUEST)
        }
    }

    private fun resolveMessageRoom(sender: MemberEntity, command: SendMessageCommand): MessageRoomEntity {
        command.roomId?.let { roomId ->
            return messageRoomReader.getById(roomId)
        }

        val receiverMemberId = command.receiverMemberId ?: throw CommonException(ResponseCode.INVALID_MESSAGE_REQUEST)

        if (receiverMemberId == sender.id) {
            throw CommonException(ResponseCode.INVALID_MESSAGE_REQUEST)
        }

        val receiver = memberReader.getMember(receiverMemberId)
        val normalizedPair = normalizeMemberPair(sender.id, receiver.id)

        val existed = messageRoomReader.findByMemberPair(
            memberAId = normalizedPair.first,
            memberBId = normalizedPair.second,
        )
        if (existed != null) {
            return existed
        }

        val memberA = if (sender.id == normalizedPair.first) sender else receiver
        val memberB = if (sender.id == normalizedPair.second) sender else receiver

        return messageRoomWriter.save(MessageRoomEntity.of(memberA = memberA, memberB = memberB))
    }

    private fun normalizeMemberPair(memberId: Long, otherMemberId: Long): Pair<Long, Long> {
        return if (memberId < otherMemberId) {
            memberId to otherMemberId
        } else {
            otherMemberId to memberId
        }
    }

    private fun markUnreadMessagesAsRead(roomId: Long, memberId: Long) {
        val unreadMessages = messageReader.findUnreadMessages(roomId, memberId)
        if (unreadMessages.isEmpty()) {
            return
        }

        unreadMessages.forEach(MessageEntity::markRead)
        messageWriter.saveAll(unreadMessages)
    }
}
