package backend.team.ahachul_backend.api.station.application.service

import backend.team.ahachul_backend.api.common.application.port.out.SubwayLineStationReader
import backend.team.ahachul_backend.api.common.domain.entity.StationEntity
import backend.team.ahachul_backend.api.common.domain.entity.SubwayLineStationEntity
import backend.team.ahachul_backend.api.station.adapter.`in`.dto.SearchSubwayRouteDto
import backend.team.ahachul_backend.api.station.application.port.`in`.dto.SearchSubwayRouteCommand
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
}
