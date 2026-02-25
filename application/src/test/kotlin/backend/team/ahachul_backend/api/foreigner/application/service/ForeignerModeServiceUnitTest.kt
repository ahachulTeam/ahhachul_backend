package backend.team.ahachul_backend.api.foreigner.application.service

import backend.team.ahachul_backend.api.common.application.port.out.SubwayLineStationReader
import backend.team.ahachul_backend.api.common.domain.entity.StationEntity
import backend.team.ahachul_backend.api.common.domain.entity.SubwayLineStationEntity
import backend.team.ahachul_backend.api.community.application.port.out.CommunityPostReader
import backend.team.ahachul_backend.api.community.domain.GetCommunityPost
import backend.team.ahachul_backend.api.community.domain.model.CommunityCategoryType
import backend.team.ahachul_backend.api.community.domain.model.CommunityPostType
import backend.team.ahachul_backend.api.foreigner.application.port.`in`.dto.ForeignerLocale
import backend.team.ahachul_backend.api.foreigner.application.port.`in`.dto.GetForeignerStationGuideCommand
import backend.team.ahachul_backend.api.foreigner.application.port.`in`.dto.TranslateCommunityPostCommand
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

    private val subwayLineStationReader: SubwayLineStationReader = Mockito.mock(SubwayLineStationReader::class.java)
    private val communityPostReader: CommunityPostReader = Mockito.mock(CommunityPostReader::class.java)
    private val foreignerModeService = ForeignerModeService(
        subwayLineStationReader = subwayLineStationReader,
        communityPostReader = communityPostReader,
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
}
