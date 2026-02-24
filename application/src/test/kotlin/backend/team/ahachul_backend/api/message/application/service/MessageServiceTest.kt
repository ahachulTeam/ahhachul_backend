package backend.team.ahachul_backend.api.message.application.service

import backend.team.ahachul_backend.api.member.adapter.web.out.MemberRepository
import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity
import backend.team.ahachul_backend.api.member.domain.model.GenderType
import backend.team.ahachul_backend.api.member.domain.model.MemberStatusType
import backend.team.ahachul_backend.api.member.domain.model.ProviderType
import backend.team.ahachul_backend.api.message.adapter.web.out.MessageRepository
import backend.team.ahachul_backend.api.message.adapter.web.out.MessageRoomRepository
import backend.team.ahachul_backend.api.message.application.command.`in`.SendMessageCommand
import backend.team.ahachul_backend.api.message.application.port.`in`.MessageUseCase
import backend.team.ahachul_backend.common.domain.model.YNType
import backend.team.ahachul_backend.common.exception.CommonException
import backend.team.ahachul_backend.common.response.ResponseCode
import backend.team.ahachul_backend.common.utils.RequestUtils
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class MessageServiceTest(
    @Autowired private val messageUseCase: MessageUseCase,
    @Autowired private val memberRepository: MemberRepository,
    @Autowired private val messageRoomRepository: MessageRoomRepository,
    @Autowired private val messageRepository: MessageRepository,
) {

    private lateinit var memberA: MemberEntity
    private lateinit var memberB: MemberEntity
    private lateinit var memberC: MemberEntity

    @BeforeEach
    fun setup() {
        memberA = createMember(1)
        memberB = createMember(2)
        memberC = createMember(3)
    }

    @Test
    @DisplayName("새 대화 전송 시 방을 생성하고 메시지를 저장한다")
    fun 새_대화_전송시_방_생성() {
        // given
        login(memberA.id)
        val command = SendMessageCommand(
            roomId = null,
            receiverMemberId = memberB.id,
            content = " 안녕하세요 ",
        )

        // when
        val result = messageUseCase.sendMessage(command)

        // then
        assertThat(result.roomId).isPositive()
        assertThat(result.messageId).isPositive()

        val room = messageRoomRepository.findById(result.roomId).orElseThrow()
        val message = messageRepository.findById(result.messageId).orElseThrow()

        assertThat(room.lastMessageContent).isEqualTo("안녕하세요")
        assertThat(room.memberA.id).isEqualTo(minOf(memberA.id, memberB.id))
        assertThat(room.memberB.id).isEqualTo(maxOf(memberA.id, memberB.id))
        assertThat(message.content).isEqualTo("안녕하세요")
        assertThat(message.senderMember.id).isEqualTo(memberA.id)
        assertThat(message.readYn).isEqualTo(YNType.N)
    }

    @Test
    @DisplayName("같은 상대에게 재전송하면 기존 방을 재사용한다")
    fun 같은_상대_재전송시_기존방_재사용() {
        // given
        login(memberA.id)
        val first = messageUseCase.sendMessage(
            SendMessageCommand(
                roomId = null,
                receiverMemberId = memberB.id,
                content = "첫 메시지",
            ),
        )

        // when
        val second = messageUseCase.sendMessage(
            SendMessageCommand(
                roomId = null,
                receiverMemberId = memberB.id,
                content = "두 번째 메시지",
            ),
        )

        // then
        assertThat(second.roomId).isEqualTo(first.roomId)
        assertThat(messageRoomRepository.count()).isEqualTo(1L)
    }

    @Test
    @DisplayName("방 조회 시 상대 미열람 메시지를 읽음 처리한다")
    fun 방_조회시_읽음처리() {
        // given
        login(memberB.id)
        val sendResult = messageUseCase.sendMessage(
            SendMessageCommand(
                roomId = null,
                receiverMemberId = memberA.id,
                content = "지연 안내",
            ),
        )

        login(memberA.id)

        // when
        val response = messageUseCase.getMessageRoomMessages(sendResult.roomId, null, 30)

        // then
        assertThat(response.messages).hasSize(1)
        assertThat(response.messages.first().content).isEqualTo("지연 안내")
        assertThat(response.messages.first().mine).isFalse()

        val savedMessage = messageRepository.findById(sendResult.messageId).orElseThrow()
        assertThat(savedMessage.readYn).isEqualTo(YNType.Y)

        val roomSummary = messageUseCase.getMessageRooms().rooms.first { it.roomId == sendResult.roomId }
        assertThat(roomSummary.unreadCount).isZero()
    }

    @Test
    @DisplayName("참여하지 않은 방 조회 시 예외가 발생한다")
    fun 미참여_방_조회_예외() {
        // given
        login(memberA.id)
        val sendResult = messageUseCase.sendMessage(
            SendMessageCommand(
                roomId = null,
                receiverMemberId = memberB.id,
                content = "비공개 메시지",
            ),
        )
        login(memberC.id)

        // when
        val throwable = catchThrowable {
            messageUseCase.getMessageRoomMessages(sendResult.roomId, null, 30)
        }

        // then
        assertThat(throwable).isInstanceOf(CommonException::class.java)
        assertThat((throwable as CommonException).code).isEqualTo(ResponseCode.MESSAGE_ROOM_FORBIDDEN)
    }

    private fun createMember(index: Int): MemberEntity {
        return memberRepository.save(
            MemberEntity(
                nickname = "member$index",
                providerUserId = "providerUserId$index",
                provider = ProviderType.KAKAO,
                email = "member$index@ahhachul.com",
                gender = GenderType.MALE,
                ageRange = "20",
                status = MemberStatusType.ACTIVE,
            ),
        )
    }

    private fun login(memberId: Long) {
        RequestUtils.setAttribute(RequestUtils.Attribute.MEMBER_ID, memberId)
    }
}
