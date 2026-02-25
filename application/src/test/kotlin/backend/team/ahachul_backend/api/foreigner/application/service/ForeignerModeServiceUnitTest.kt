package backend.team.ahachul_backend.api.foreigner.application.service

import backend.team.ahachul_backend.api.common.application.port.out.StationReader
import backend.team.ahachul_backend.api.common.application.port.out.SubwayLineStationReader
import backend.team.ahachul_backend.api.common.domain.entity.StationEntity
import backend.team.ahachul_backend.api.common.domain.entity.SubwayLineStationEntity
import backend.team.ahachul_backend.api.community.application.port.out.CommunityPostReader
import backend.team.ahachul_backend.api.community.domain.GetCommunityPost
import backend.team.ahachul_backend.api.community.domain.SearchCommunityPost
import backend.team.ahachul_backend.api.community.domain.model.CommunityCategoryType
import backend.team.ahachul_backend.api.community.domain.model.CommunityPostType
import backend.team.ahachul_backend.api.foreigner.application.port.`in`.dto.ForeignerLocale
import backend.team.ahachul_backend.api.foreigner.application.port.`in`.dto.GetForeignerStationGuideCommand
import backend.team.ahachul_backend.api.foreigner.application.port.`in`.dto.GetForeignerStationSocialHotspotsCommand
import backend.team.ahachul_backend.api.foreigner.application.port.`in`.dto.TranslateCommunityPostCommand
import backend.team.ahachul_backend.api.foreigner.application.port.out.StationSocialMeetupParticipantReader
import backend.team.ahachul_backend.api.foreigner.application.port.out.StationSocialMeetupParticipantWriter
import backend.team.ahachul_backend.api.foreigner.application.port.out.StationSocialMeetupReader
import backend.team.ahachul_backend.api.foreigner.application.port.out.StationSocialMeetupWriter
import backend.team.ahachul_backend.api.member.application.port.out.MemberReader
import backend.team.ahachul_backend.api.message.application.port.out.MessageRoomReader
import backend.team.ahachul_backend.api.message.application.port.out.MessageRoomWriter
import backend.team.ahachul_backend.api.message.application.port.out.MessageWriter
import backend.team.ahachul_backend.common.domain.entity.SubwayLineEntity
import backend.team.ahachul_backend.common.domain.model.RegionType
import backend.team.ahachul_backend.common.domain.model.YNType
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import java.time.LocalDateTime

class ForeignerModeServiceUnitTest {

    private val stationReader: StationReader = Mockito.mock(StationReader::class.java)
    private val subwayLineStationReader: SubwayLineStationReader = Mockito.mock(SubwayLineStationReader::class.java)
    private val communityPostReader: CommunityPostReader = Mockito.mock(CommunityPostReader::class.java)
    private val memberReader: MemberReader = Mockito.mock(MemberReader::class.java)
    private val stationSocialMeetupReader: StationSocialMeetupReader = Mockito.mock(StationSocialMeetupReader::class.java)
    private val stationSocialMeetupWriter: StationSocialMeetupWriter = Mockito.mock(StationSocialMeetupWriter::class.java)
    private val stationSocialMeetupParticipantReader: StationSocialMeetupParticipantReader =
        Mockito.mock(StationSocialMeetupParticipantReader::class.java)
    private val stationSocialMeetupParticipantWriter: StationSocialMeetupParticipantWriter =
        Mockito.mock(StationSocialMeetupParticipantWriter::class.java)
    private val messageRoomReader: MessageRoomReader = Mockito.mock(MessageRoomReader::class.java)
    private val messageRoomWriter: MessageRoomWriter = Mockito.mock(MessageRoomWriter::class.java)
    private val messageWriter: MessageWriter = Mockito.mock(MessageWriter::class.java)

    private val foreignerModeService = ForeignerModeService(
        stationReader = stationReader,
        subwayLineStationReader = subwayLineStationReader,
        communityPostReader = communityPostReader,
        memberReader = memberReader,
        stationSocialMeetupReader = stationSocialMeetupReader,
        stationSocialMeetupWriter = stationSocialMeetupWriter,
        stationSocialMeetupParticipantReader = stationSocialMeetupParticipantReader,
        stationSocialMeetupParticipantWriter = stationSocialMeetupParticipantWriter,
        messageRoomReader = messageRoomReader,
        messageRoomWriter = messageRoomWriter,
        messageWriter = messageWriter,
        objectMapper = jacksonObjectMapper(),
    )

    @Test
    @DisplayName("역 가이드는 로마자/현지화 역명/템플릿을 반환한다.")
    fun getStationGuideReturnsLocalizedFields() {
        val station = StationEntity(id = 557L, name = "안암")
        val subwayLine = SubwayLineEntity(
            id = 18L,
            name = "우이신설경전철",
            regionType = RegionType.METROPOLITAN,
        )
        given(subwayLineStationReader.findBySubwayLineIdAndStationId(18L, 557L)).willReturn(
            SubwayLineStationEntity(
                id = 1L,
                stationCode = "L18001",
                station = station,
                subwayLine = subwayLine,
            )
        )

        val result = foreignerModeService.getStationGuide(
            GetForeignerStationGuideCommand(
                stationId = 557L,
                subwayLineId = 18L,
                locale = ForeignerLocale.EN,
            )
        )

        assertThat(result.station.nameKo).isEqualTo("안암")
        assertThat(result.station.nameLocalized).contains("Station")
        assertThat(result.station.romanizedName).isNotBlank
        assertThat(result.templates.complaintBodyTemplate).contains("Location")
        assertThat(result.supportedLocales).containsExactly("ko", "en", "th", "cn")
    }

    @Test
    @DisplayName("커뮤니티 번역은 사전 매칭 문장을 번역하고 fallback=false를 반환한다.")
    fun translateCommunityPostReturnsDictionaryTranslation() {
        given(communityPostReader.getByCustom(101L, null)).willReturn(
            GetCommunityPost(
                id = 101L,
                title = "2호선 지연 안내",
                content = "지하철 환승 구간 혼잡",
                categoryType = CommunityCategoryType.ISSUE,
                likeCnt = 0L,
                hateCnt = 0L,
                likeYn = false,
                hateYn = false,
                hotPostYn = YNType.N,
                regionType = RegionType.METROPOLITAN,
                subwayLineId = 2L,
                stationId = 201L,
                createdAt = LocalDateTime.now(),
                createdBy = "1",
                writer = "tester",
                status = CommunityPostType.CREATED,
            )
        )

        val result = foreignerModeService.translateCommunityPost(
            TranslateCommunityPostCommand(
                postId = 101L,
                targetLocale = ForeignerLocale.EN,
            )
        )

        assertThat(result.targetLocale).isEqualTo("en")
        assertThat(result.translatedTitle.lowercase()).contains("line")
        assertThat(result.translatedContent.lowercase()).contains("subway")
        assertThat(result.isFallback).isFalse
    }

    @Test
    @DisplayName("미번역 문장도 안전하게 fallback 번역을 제공한다.")
    fun translateCommunityPostReturnsFallbackForUnknownPhrase() {
        given(communityPostReader.getByCustom(102L, null)).willReturn(
            GetCommunityPost(
                id = 102L,
                title = "역사 내부 공지",
                content = "오늘 운영 방침 참고 바랍니다",
                categoryType = CommunityCategoryType.FREE,
                likeCnt = 0L,
                hateCnt = 0L,
                likeYn = false,
                hateYn = false,
                hotPostYn = YNType.N,
                regionType = RegionType.METROPOLITAN,
                subwayLineId = 1L,
                stationId = 101L,
                createdAt = LocalDateTime.now(),
                createdBy = "1",
                writer = "tester",
                status = CommunityPostType.CREATED,
            )
        )

        val result = foreignerModeService.translateCommunityPost(
            TranslateCommunityPostCommand(
                postId = 102L,
                targetLocale = ForeignerLocale.CN,
            )
        )

        assertThat(result.targetLocale).isEqualTo("cn")
        assertThat(result.translatedContent).isNotBlank
        assertThat(result.isFallback).isTrue
    }

    @Test
    @DisplayName("외국인 역 소셜 핫스팟은 조회 가능한 역만 반환한다.")
    fun getStationSocialHotspotsReturnsAvailableStationsOnly() {
        val station = StationEntity(id = 201L, name = "홍대입구")
        val subwayLine = SubwayLineEntity(
            id = 2L,
            name = "2호선",
            regionType = RegionType.METROPOLITAN,
        )

        given(stationReader.getByName("명동")).willThrow(RuntimeException("not found"))
        given(stationReader.getByName("명동역")).willThrow(RuntimeException("not found"))
        given(stationReader.getByName("성수")).willThrow(RuntimeException("not found"))
        given(stationReader.getByName("성수역")).willThrow(RuntimeException("not found"))
        given(stationReader.getByName("홍대입구")).willReturn(station)
        given(stationReader.getByName("강남")).willThrow(RuntimeException("not found"))
        given(stationReader.getByName("강남역")).willThrow(RuntimeException("not found"))
        given(stationReader.getByName("안국")).willThrow(RuntimeException("not found"))
        given(stationReader.getByName("안국역")).willThrow(RuntimeException("not found"))

        given(subwayLineStationReader.findByStation(station)).willReturn(
            listOf(
                SubwayLineStationEntity(
                    id = 10L,
                    stationCode = "L02031",
                    station = station,
                    subwayLine = subwayLine,
                ),
            ),
        )
        given(communityPostReader.searchCommunityHotPosts(any())).willReturn(
            listOf(
                SearchCommunityPost(
                    id = 33L,
                    title = "홍대입구역 추천 코스",
                    content = "좋아요",
                    categoryType = CommunityCategoryType.FREE,
                    regionType = RegionType.METROPOLITAN,
                    subwayLineId = 2L,
                    stationId = 201L,
                    likeCnt = 10L,
                    commentCnt = 3L,
                    createdAt = LocalDateTime.now(),
                    createdBy = "1",
                    writer = "tester",
                ),
            ),
        )

        val result = foreignerModeService.getStationSocialHotspots(
            GetForeignerStationSocialHotspotsCommand(ForeignerLocale.EN),
        )

        assertThat(result.hotspots).hasSize(1)
        assertThat(result.hotspots.first().stationId).isEqualTo(201L)
    }

    private fun <T> any(): T {
        Mockito.any<T>()
        return null as T
    }
}
