package backend.team.ahachul_backend.api.station.application.service

import backend.team.ahachul_backend.api.common.application.port.out.SubwayLineStationReader
import backend.team.ahachul_backend.api.common.domain.entity.StationEntity
import backend.team.ahachul_backend.api.common.domain.entity.SubwayLineStationEntity
import backend.team.ahachul_backend.api.station.adapter.`in`.dto.SearchSubwayRouteDto
import backend.team.ahachul_backend.api.station.adapter.`in`.dto.SearchSubwayRouteQualityV3Dto
import backend.team.ahachul_backend.api.station.adapter.`in`.dto.StationTimeWeekType
import backend.team.ahachul_backend.api.station.application.port.`in`.dto.SearchSubwayRouteCommand
import backend.team.ahachul_backend.api.station.application.port.`in`.dto.SearchSubwayRouteQualityV3Command
import backend.team.ahachul_backend.common.client.SeoulTrainClient
import backend.team.ahachul_backend.common.domain.entity.SubwayLineEntity
import backend.team.ahachul_backend.common.domain.model.RegionType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito

class StationServiceRouteSearchUnitTest {

    private val subwayLineStationReader: SubwayLineStationReader = Mockito.mock(SubwayLineStationReader::class.java)
    private val stationTimesCacheUtils: StationTimesCacheUtils = Mockito.mock(StationTimesCacheUtils::class.java)
    private val seoulTrainClient: SeoulTrainClient = Mockito.mock(SeoulTrainClient::class.java)
    private val stationService = StationService(
        subwayLineStationReader = subwayLineStationReader,
        stationTimesCacheUtils = stationTimesCacheUtils,
        seoulTrainClient = seoulTrainClient,
    )

    @Test
    @DisplayName("길찾기 검색은 정차역/환승 정보를 포함한 경로를 반환한다.")
    fun searchSubwayRoutesReturnsRoute() {
        // given
        val line1 = SubwayLineEntity(id = 1L, name = "1호선", regionType = RegionType.METROPOLITAN)
        val line2 = SubwayLineEntity(id = 2L, name = "2호선", regionType = RegionType.METROPOLITAN)
        val stationA = StationEntity(id = 101L, name = "A역")
        val stationB = StationEntity(id = 102L, name = "B역")
        val stationC = StationEntity(id = 103L, name = "C역")
        val stationD = StationEntity(id = 104L, name = "D역")

        given(subwayLineStationReader.findAllOrderedForGraph()).willReturn(
            listOf(
                SubwayLineStationEntity(id = 1L, stationCode = "1001", station = stationA, subwayLine = line1),
                SubwayLineStationEntity(id = 2L, stationCode = "1002", station = stationB, subwayLine = line1),
                SubwayLineStationEntity(id = 3L, stationCode = "1003", station = stationC, subwayLine = line1),
                SubwayLineStationEntity(id = 4L, stationCode = "2001", station = stationC, subwayLine = line2),
                SubwayLineStationEntity(id = 5L, stationCode = "2002", station = stationD, subwayLine = line2),
            )
        )

        // when
        val result = stationService.searchSubwayRoutes(
            SearchSubwayRouteCommand(
                sourceStationId = 101L,
                destinationStationId = 104L,
                strategy = SearchSubwayRouteDto.RouteSearchStrategy.BALANCED,
                alternatives = 2,
            )
        )

        // then
        assertThat(result.routes).isNotEmpty()
        val bestRoute = result.routes.first()
        assertThat(bestRoute.summary.totalStops).isEqualTo(3)
        assertThat(bestRoute.summary.transferCount).isEqualTo(1)
        assertThat(bestRoute.nodes.map { it.stationId }).containsExactly(101L, 102L, 103L, 104L)
        assertThat(bestRoute.nodes.count { it.isTransfer }).isGreaterThanOrEqualTo(1)
    }

    @Test
    @DisplayName("길찾기 그래프는 DB id 순서가 아니라 stationCode 순서로 간선을 생성한다.")
    fun searchSubwayRoutesUsesStationCodeOrder() {
        // given
        val line1 = SubwayLineEntity(id = 1L, name = "1호선", regionType = RegionType.METROPOLITAN)
        val stationA = StationEntity(id = 201L, name = "A역")
        val stationB = StationEntity(id = 202L, name = "B역")
        val stationC = StationEntity(id = 203L, name = "C역")
        val stationD = StationEntity(id = 204L, name = "D역")

        // id 순서로는 D -> A -> C -> B 이지만, stationCode 순서로는 A -> B -> C -> D 가 되어야 한다.
        given(subwayLineStationReader.findAllOrderedForGraph()).willReturn(
            listOf(
                SubwayLineStationEntity(id = 10L, stationCode = "1004", station = stationD, subwayLine = line1),
                SubwayLineStationEntity(id = 11L, stationCode = "1001", station = stationA, subwayLine = line1),
                SubwayLineStationEntity(id = 12L, stationCode = "1003", station = stationC, subwayLine = line1),
                SubwayLineStationEntity(id = 13L, stationCode = "1002", station = stationB, subwayLine = line1),
            )
        )

        // when
        val result = stationService.searchSubwayRoutes(
            SearchSubwayRouteCommand(
                sourceStationId = 201L,
                destinationStationId = 204L,
                strategy = SearchSubwayRouteDto.RouteSearchStrategy.BALANCED,
                alternatives = 1,
            )
        )

        // then
        val route = result.routes.first()
        assertThat(route.nodes.map { it.stationId }).containsExactly(201L, 202L, 203L, 204L)
        assertThat(route.summary.totalStops).isEqualTo(3)
        assertThat(route.summary.transferCount).isEqualTo(0)
    }

    @Test
    @DisplayName("2호선은 순환 간선을 포함해 시작-끝 역 사이를 직접 연결한다.")
    fun searchSubwayRoutesSupportsCircularLineWrapEdge() {
        // given
        val line2 = SubwayLineEntity(id = 2L, name = "2호선", regionType = RegionType.METROPOLITAN)
        val stationA = StationEntity(id = 301L, name = "A역")
        val stationB = StationEntity(id = 302L, name = "B역")
        val stationC = StationEntity(id = 303L, name = "C역")

        given(subwayLineStationReader.findAllOrderedForGraph()).willReturn(
            listOf(
                SubwayLineStationEntity(id = 21L, stationCode = "2001", station = stationA, subwayLine = line2),
                SubwayLineStationEntity(id = 22L, stationCode = "2002", station = stationB, subwayLine = line2),
                SubwayLineStationEntity(id = 23L, stationCode = "2003", station = stationC, subwayLine = line2),
            )
        )

        // when
        val result = stationService.searchSubwayRoutes(
            SearchSubwayRouteCommand(
                sourceStationId = 301L,
                destinationStationId = 303L,
                strategy = SearchSubwayRouteDto.RouteSearchStrategy.BALANCED,
                alternatives = 1,
            )
        )

        // then
        val route = result.routes.first()
        assertThat(route.nodes.map { it.stationId }).containsExactly(301L, 303L)
        assertThat(route.summary.totalStops).isEqualTo(1)
        assertThat(route.summary.transferCount).isEqualTo(0)
    }

    @Test
    @DisplayName("길찾기 V3는 경로별 품질 점수/사유를 포함한다.")
    fun searchSubwayRoutesV3IncludesQuality() {
        val line1 = SubwayLineEntity(id = 1L, name = "1호선", regionType = RegionType.METROPOLITAN)
        val line2 = SubwayLineEntity(id = 2L, name = "2호선", regionType = RegionType.METROPOLITAN)
        val stationA = StationEntity(id = 401L, name = "A역")
        val stationB = StationEntity(id = 402L, name = "B역")
        val stationC = StationEntity(id = 403L, name = "C역")
        val stationD = StationEntity(id = 404L, name = "D역")

        given(subwayLineStationReader.findAllOrderedForGraph()).willReturn(
            listOf(
                SubwayLineStationEntity(id = 1L, stationCode = "1001", station = stationA, subwayLine = line1),
                SubwayLineStationEntity(id = 2L, stationCode = "1002", station = stationB, subwayLine = line1),
                SubwayLineStationEntity(id = 3L, stationCode = "1003", station = stationC, subwayLine = line1),
                SubwayLineStationEntity(id = 4L, stationCode = "2001", station = stationC, subwayLine = line2),
                SubwayLineStationEntity(id = 5L, stationCode = "2002", station = stationD, subwayLine = line2),
            )
        )

        val result = stationService.searchSubwayRoutesV3(
            SearchSubwayRouteQualityV3Command(
                sourceStationId = 401L,
                destinationStationId = 404L,
                strategy = SearchSubwayRouteDto.RouteSearchStrategy.BALANCED,
                alternatives = 3,
                walkingPreference = SearchSubwayRouteQualityV3Dto.RouteWalkingPreference.FAST,
                stationTimeWeekType = StationTimeWeekType.WEEKDAY,
                accessibilityMode = SearchSubwayRouteQualityV3Dto.RouteAccessibilityMode.BALANCED,
                crowdingPreference = SearchSubwayRouteQualityV3Dto.RouteCrowdingPreference.BALANCED,
                luggageMode = SearchSubwayRouteQualityV3Dto.RouteLuggageMode.NORMAL,
                travelerContext = SearchSubwayRouteQualityV3Dto.RouteTravelerContext.COMMUTE,
                locale = "ko",
            )
        )

        assertThat(result.modelVersion).isEqualTo("ROUTE_QUALITY_V3")
        assertThat(result.routes).isNotEmpty()
        val topRoute = result.routes.first()
        assertThat(topRoute.quality.totalScore).isBetween(0, 100)
        assertThat(topRoute.quality.delayProbabilityPercent).isBetween(0, 100)
        assertThat(topRoute.quality.reasons).isNotEmpty
    }

    @Test
    @DisplayName("길찾기 V3의 보행 점수는 walkingPreference에 따라 달라진다.")
    fun searchSubwayRoutesV3WalkingPreferenceAffectsWalkingScore() {
        val line1 = SubwayLineEntity(id = 1L, name = "1호선", regionType = RegionType.METROPOLITAN)
        val line2 = SubwayLineEntity(id = 2L, name = "2호선", regionType = RegionType.METROPOLITAN)
        val stationA = StationEntity(id = 501L, name = "A역")
        val stationB = StationEntity(id = 502L, name = "B역")
        val stationC = StationEntity(id = 503L, name = "C역")
        val stationD = StationEntity(id = 504L, name = "D역")

        given(subwayLineStationReader.findAllOrderedForGraph()).willReturn(
            listOf(
                SubwayLineStationEntity(id = 1L, stationCode = "1001", station = stationA, subwayLine = line1),
                SubwayLineStationEntity(id = 2L, stationCode = "1002", station = stationB, subwayLine = line1),
                SubwayLineStationEntity(id = 3L, stationCode = "1003", station = stationC, subwayLine = line1),
                SubwayLineStationEntity(id = 4L, stationCode = "2001", station = stationC, subwayLine = line2),
                SubwayLineStationEntity(id = 5L, stationCode = "2002", station = stationD, subwayLine = line2),
            )
        )

        val fast = stationService.searchSubwayRoutesV3(
            SearchSubwayRouteQualityV3Command(
                sourceStationId = 501L,
                destinationStationId = 504L,
                strategy = SearchSubwayRouteDto.RouteSearchStrategy.BALANCED,
                alternatives = 2,
                walkingPreference = SearchSubwayRouteQualityV3Dto.RouteWalkingPreference.FAST,
                stationTimeWeekType = StationTimeWeekType.WEEKDAY,
                accessibilityMode = SearchSubwayRouteQualityV3Dto.RouteAccessibilityMode.BALANCED,
                crowdingPreference = SearchSubwayRouteQualityV3Dto.RouteCrowdingPreference.BALANCED,
                luggageMode = SearchSubwayRouteQualityV3Dto.RouteLuggageMode.NORMAL,
                travelerContext = SearchSubwayRouteQualityV3Dto.RouteTravelerContext.COMMUTE,
                locale = "ko",
            )
        )

        val lessStairs = stationService.searchSubwayRoutesV3(
            SearchSubwayRouteQualityV3Command(
                sourceStationId = 501L,
                destinationStationId = 504L,
                strategy = SearchSubwayRouteDto.RouteSearchStrategy.BALANCED,
                alternatives = 2,
                walkingPreference = SearchSubwayRouteQualityV3Dto.RouteWalkingPreference.LESS_STAIRS,
                stationTimeWeekType = StationTimeWeekType.WEEKDAY,
                accessibilityMode = SearchSubwayRouteQualityV3Dto.RouteAccessibilityMode.BALANCED,
                crowdingPreference = SearchSubwayRouteQualityV3Dto.RouteCrowdingPreference.BALANCED,
                luggageMode = SearchSubwayRouteQualityV3Dto.RouteLuggageMode.NORMAL,
                travelerContext = SearchSubwayRouteQualityV3Dto.RouteTravelerContext.COMMUTE,
                locale = "ko",
            )
        )

        val fastWalkingScore = fast.routes.first().quality.walkingScore
        val lessStairsWalkingScore = lessStairs.routes.first().quality.walkingScore
        assertThat(fastWalkingScore).isGreaterThan(lessStairsWalkingScore)
    }

    @Test
    @DisplayName("길찾기 V3의 접근성 점수는 accessibilityMode에 따라 달라진다.")
    fun searchSubwayRoutesV3AccessibilityModeAffectsScore() {
        val line2 = SubwayLineEntity(id = 2L, name = "2호선", regionType = RegionType.METROPOLITAN)
        val stationA = StationEntity(id = 601L, name = "A역")
        val stationB = StationEntity(id = 602L, name = "B역")
        val stationC = StationEntity(id = 603L, name = "C역")

        given(subwayLineStationReader.findAllOrderedForGraph()).willReturn(
            listOf(
                SubwayLineStationEntity(id = 1L, stationCode = "2001", station = stationA, subwayLine = line2),
                SubwayLineStationEntity(id = 2L, stationCode = "2002", station = stationB, subwayLine = line2),
                SubwayLineStationEntity(id = 3L, stationCode = "2003", station = stationC, subwayLine = line2),
            )
        )

        val balanced = stationService.searchSubwayRoutesV3(
            SearchSubwayRouteQualityV3Command(
                sourceStationId = 601L,
                destinationStationId = 603L,
                strategy = SearchSubwayRouteDto.RouteSearchStrategy.BALANCED,
                alternatives = 1,
                walkingPreference = SearchSubwayRouteQualityV3Dto.RouteWalkingPreference.FAST,
                stationTimeWeekType = StationTimeWeekType.WEEKDAY,
                accessibilityMode = SearchSubwayRouteQualityV3Dto.RouteAccessibilityMode.BALANCED,
                crowdingPreference = SearchSubwayRouteQualityV3Dto.RouteCrowdingPreference.BALANCED,
                luggageMode = SearchSubwayRouteQualityV3Dto.RouteLuggageMode.NORMAL,
                travelerContext = SearchSubwayRouteQualityV3Dto.RouteTravelerContext.COMMUTE,
                locale = "ko",
            )
        )

        val wheelchair = stationService.searchSubwayRoutesV3(
            SearchSubwayRouteQualityV3Command(
                sourceStationId = 601L,
                destinationStationId = 603L,
                strategy = SearchSubwayRouteDto.RouteSearchStrategy.BALANCED,
                alternatives = 1,
                walkingPreference = SearchSubwayRouteQualityV3Dto.RouteWalkingPreference.FAST,
                stationTimeWeekType = StationTimeWeekType.WEEKDAY,
                accessibilityMode = SearchSubwayRouteQualityV3Dto.RouteAccessibilityMode.WHEELCHAIR,
                crowdingPreference = SearchSubwayRouteQualityV3Dto.RouteCrowdingPreference.BALANCED,
                luggageMode = SearchSubwayRouteQualityV3Dto.RouteLuggageMode.NORMAL,
                travelerContext = SearchSubwayRouteQualityV3Dto.RouteTravelerContext.COMMUTE,
                locale = "ko",
            )
        )

        assertThat(wheelchair.routes.first().quality.accessibilityScore)
            .isGreaterThanOrEqualTo(balanced.routes.first().quality.accessibilityScore)
    }

    @Test
    @DisplayName("길찾기 V3는 다국어 원클릭 액션을 포함한다.")
    fun searchSubwayRoutesV3IncludesOneClickActions() {
        val line2 = SubwayLineEntity(id = 2L, name = "2호선", regionType = RegionType.METROPOLITAN)
        val stationA = StationEntity(id = 701L, name = "A역")
        val stationB = StationEntity(id = 702L, name = "B역")

        given(subwayLineStationReader.findAllOrderedForGraph()).willReturn(
            listOf(
                SubwayLineStationEntity(id = 1L, stationCode = "2001", station = stationA, subwayLine = line2),
                SubwayLineStationEntity(id = 2L, stationCode = "2002", station = stationB, subwayLine = line2),
            )
        )

        val result = stationService.searchSubwayRoutesV3(
            SearchSubwayRouteQualityV3Command(
                sourceStationId = 701L,
                destinationStationId = 702L,
                strategy = SearchSubwayRouteDto.RouteSearchStrategy.BALANCED,
                alternatives = 1,
                walkingPreference = SearchSubwayRouteQualityV3Dto.RouteWalkingPreference.FAST,
                stationTimeWeekType = StationTimeWeekType.WEEKDAY,
                accessibilityMode = SearchSubwayRouteQualityV3Dto.RouteAccessibilityMode.BALANCED,
                crowdingPreference = SearchSubwayRouteQualityV3Dto.RouteCrowdingPreference.BALANCED,
                luggageMode = SearchSubwayRouteQualityV3Dto.RouteLuggageMode.NORMAL,
                travelerContext = SearchSubwayRouteQualityV3Dto.RouteTravelerContext.TRAVEL,
                locale = "en",
            )
        )

        assertThat(result.oneClickActions).isNotEmpty
        assertThat(result.oneClickActions.map { it.actionType.name }).contains("CALL_EMERGENCY_112")
        assertThat(result.oneClickActions.first().title).isNotBlank
    }
}
