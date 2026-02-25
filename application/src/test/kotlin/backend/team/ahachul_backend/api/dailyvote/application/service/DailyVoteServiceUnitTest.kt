package backend.team.ahachul_backend.api.dailyvote.application.service

import backend.team.ahachul_backend.api.common.application.port.out.SubwayLineStationReader
import backend.team.ahachul_backend.api.common.domain.entity.StationEntity
import backend.team.ahachul_backend.api.common.domain.entity.SubwayLineStationEntity
import backend.team.ahachul_backend.api.dailyvote.application.port.`in`.dto.CreateDailyVoteCommentCommand
import backend.team.ahachul_backend.api.dailyvote.application.port.`in`.dto.GetTodayDailyVoteCommand
import backend.team.ahachul_backend.api.dailyvote.application.port.out.DailyVoteCommentLikeReader
import backend.team.ahachul_backend.api.dailyvote.application.port.out.DailyVoteCommentLikeWriter
import backend.team.ahachul_backend.api.dailyvote.application.port.out.DailyVoteCommentReader
import backend.team.ahachul_backend.api.dailyvote.application.port.out.DailyVoteCommentWriter
import backend.team.ahachul_backend.api.dailyvote.application.port.out.DailyVotePollReader
import backend.team.ahachul_backend.api.dailyvote.application.port.out.DailyVotePollWriter
import backend.team.ahachul_backend.api.dailyvote.application.port.out.DailyVoteResponseReader
import backend.team.ahachul_backend.api.dailyvote.application.port.out.DailyVoteResponseWriter
import backend.team.ahachul_backend.api.dailyvote.domain.entity.DailyVoteCommentEntity
import backend.team.ahachul_backend.api.dailyvote.domain.entity.DailyVotePollEntity
import backend.team.ahachul_backend.api.dailyvote.domain.model.DailyVoteCommentStatusType
import backend.team.ahachul_backend.api.dailyvote.domain.model.DailyVoteContextType
import backend.team.ahachul_backend.api.dailyvote.domain.model.DailyVoteKindType
import backend.team.ahachul_backend.api.dailyvote.domain.model.DailyVotePollStatusType
import backend.team.ahachul_backend.api.dailyvote.domain.model.DailyVoteSlotType
import backend.team.ahachul_backend.api.member.application.port.out.MemberReader
import backend.team.ahachul_backend.api.member.application.port.out.MemberStationReader
import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity
import backend.team.ahachul_backend.api.member.domain.entity.MemberStationEntity
import backend.team.ahachul_backend.api.member.domain.model.MemberStatusType
import backend.team.ahachul_backend.api.member.domain.model.ProviderType
import backend.team.ahachul_backend.common.domain.entity.SubwayLineEntity
import backend.team.ahachul_backend.common.domain.model.RegionType
import backend.team.ahachul_backend.common.domain.model.YNType
import backend.team.ahachul_backend.common.utils.RequestUtils
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mockito
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.time.LocalDate

class DailyVoteServiceUnitTest {

    private val memberReader: MemberReader = Mockito.mock(MemberReader::class.java)
    private val memberStationReader: MemberStationReader = Mockito.mock(MemberStationReader::class.java)
    private val subwayLineStationReader: SubwayLineStationReader = Mockito.mock(SubwayLineStationReader::class.java)
    private val dailyVotePollReader: DailyVotePollReader = Mockito.mock(DailyVotePollReader::class.java)
    private val dailyVotePollWriter: DailyVotePollWriter = Mockito.mock(DailyVotePollWriter::class.java)
    private val dailyVoteResponseReader: DailyVoteResponseReader = Mockito.mock(DailyVoteResponseReader::class.java)
    private val dailyVoteResponseWriter: DailyVoteResponseWriter = Mockito.mock(DailyVoteResponseWriter::class.java)
    private val dailyVoteCommentReader: DailyVoteCommentReader = Mockito.mock(DailyVoteCommentReader::class.java)
    private val dailyVoteCommentWriter: DailyVoteCommentWriter = Mockito.mock(DailyVoteCommentWriter::class.java)
    private val dailyVoteCommentLikeReader: DailyVoteCommentLikeReader = Mockito.mock(DailyVoteCommentLikeReader::class.java)
    private val dailyVoteCommentLikeWriter: DailyVoteCommentLikeWriter = Mockito.mock(DailyVoteCommentLikeWriter::class.java)

    private val dailyVoteService = DailyVoteService(
        memberReader = memberReader,
        memberStationReader = memberStationReader,
        subwayLineStationReader = subwayLineStationReader,
        dailyVotePollReader = dailyVotePollReader,
        dailyVotePollWriter = dailyVotePollWriter,
        dailyVoteResponseReader = dailyVoteResponseReader,
        dailyVoteResponseWriter = dailyVoteResponseWriter,
        dailyVoteCommentReader = dailyVoteCommentReader,
        dailyVoteCommentWriter = dailyVoteCommentWriter,
        dailyVoteCommentLikeReader = dailyVoteCommentLikeReader,
        dailyVoteCommentLikeWriter = dailyVoteCommentLikeWriter,
        objectMapper = ObjectMapper(),
    )

    @BeforeEach
    fun setupRequestContext() {
        RequestContextHolder.setRequestAttributes(ServletRequestAttributes(MockHttpServletRequest()))
    }

    @AfterEach
    fun clearRequestContext() {
        RequestContextHolder.resetRequestAttributes()
    }

    @Test
    @DisplayName("학생 라벨이 있으면 주 투표는 등/하교 컨텍스트로 생성된다")
    fun getTodayWithStudentProfile() {
        val member = MemberEntity(
            id = 11L,
            nickname = "tester",
            providerUserId = "provider-11",
            provider = ProviderType.GOOGLE,
            email = "tester@ahhachul.dev",
            gender = null,
            ageRange = null,
            status = MemberStatusType.ACTIVE,
        )
        val station = StationEntity(id = 557L, name = "안암")
        val line = SubwayLineEntity(id = 2L, name = "2호선", regionType = RegionType.METROPOLITAN)
        val mapping = SubwayLineStationEntity(id = 1L, stationCode = "2001", station = station, subwayLine = line)
        val memberStation = MemberStationEntity(
            id = 1L,
            label = "학교",
            member = member,
            station = station,
        )

        RequestUtils.setAttribute(RequestUtils.Attribute.MEMBER_ID, member.id)
        given(memberReader.getMember(member.id)).willReturn(member)
        given(memberStationReader.getByMember(member)).willReturn(listOf(memberStation))
        given(subwayLineStationReader.findByStation(station)).willReturn(listOf(mapping))

        given(dailyVotePollWriter.save(any())).willReturn(
            buildPoll(101L, station, line, DailyVoteContextType.SCHOOL, DailyVoteKindType.MAIN, true, "오늘 2호선 등교길 어땠나요?"),
            buildPoll(102L, station, line, DailyVoteContextType.COMMUTE, DailyVoteKindType.MAIN, false, "오늘 2호선 출근길 어땠나요?"),
            buildPoll(103L, station, line, DailyVoteContextType.SCHOOL, DailyVoteKindType.STATION_DIARY, true, "오늘의 안암역은 어떠셨나요?"),
        )
        given(dailyVoteResponseReader.findByPollId(anyLong())).willReturn(emptyList())
        given(dailyVoteCommentReader.countByPollIdAndStatus(103L, DailyVoteCommentStatusType.CREATED)).willReturn(0L)

        val result = dailyVoteService.getToday(GetTodayDailyVoteCommand(timezone = "Asia/Seoul"))

        assertThat(result.profileHint).isEqualTo("SCHOOL")
        assertThat(result.primaryPoll?.question).contains("등교길")
        assertThat(result.secondaryPoll?.question).contains("출근길")
        assertThat(result.stationDiary?.visible).isFalse()
    }

    @Test
    @DisplayName("일일 투표 댓글은 유효한 이미지 URL만 저장한다")
    fun createCommentSanitizeImageUrls() {
        val member = MemberEntity(
            id = 12L,
            nickname = "tester2",
            providerUserId = "provider-12",
            provider = ProviderType.GOOGLE,
            email = "tester2@ahhachul.dev",
            gender = null,
            ageRange = null,
            status = MemberStatusType.ACTIVE,
        )
        val station = StationEntity(id = 557L, name = "안암")
        val line = SubwayLineEntity(id = 2L, name = "2호선", regionType = RegionType.METROPOLITAN)
        val poll = buildPoll(501L, station, line, DailyVoteContextType.COMMUTE, DailyVoteKindType.MAIN, true, "오늘 2호선 출근길 어땠나요?")

        RequestUtils.setAttribute(RequestUtils.Attribute.MEMBER_ID, member.id)
        given(memberReader.getMember(member.id)).willReturn(member)
        given(dailyVotePollReader.getById(poll.id)).willReturn(poll)
        var persistedComment: DailyVoteCommentEntity? = null
        given(dailyVoteCommentWriter.save(any())).willAnswer { invocation ->
            val source = invocation.arguments[0] as DailyVoteCommentEntity
            persistedComment = source
            DailyVoteCommentEntity(
                id = 700L,
                poll = source.poll,
                member = source.member,
                content = source.content,
                imageUrls = source.imageUrls,
                status = source.status,
            )
        }

        val result = dailyVoteService.createComment(
            CreateDailyVoteCommentCommand(
                pollId = poll.id,
                content = "오늘 진짜 혼잡했어요",
                imageUrls = listOf(
                    "https://cdn.ahhachul.com/a.gif",
                    "ftp://invalid.example.com/b.gif",
                    "   ",
                    "http://cdn.ahhachul.com/c.png",
                ),
            ),
        )

        assertThat(result.commentId).isEqualTo(700L)
        assertThat(persistedComment).isNotNull
        assertThat(persistedComment!!.imageUrls).contains("https://cdn.ahhachul.com/a.gif")
        assertThat(persistedComment!!.imageUrls).contains("http://cdn.ahhachul.com/c.png")
        assertThat(persistedComment!!.imageUrls).doesNotContain("ftp://")
    }

    private fun <T> any(): T {
        Mockito.any<T>()
        return null as T
    }

    private fun buildPoll(
        id: Long,
        station: StationEntity,
        line: SubwayLineEntity,
        context: DailyVoteContextType,
        kind: DailyVoteKindType,
        isPrimary: Boolean,
        question: String,
    ): DailyVotePollEntity {
        return DailyVotePollEntity(
            id = id,
            pollDate = LocalDate.of(2026, 2, 25),
            pollSlot = DailyVoteSlotType.MORNING,
            pollContext = context,
            pollKind = kind,
            status = DailyVotePollStatusType.OPEN,
            station = station,
            subwayLine = line,
            question = question,
            primaryYn = if (isPrimary) YNType.Y else YNType.N,
        )
    }
}
