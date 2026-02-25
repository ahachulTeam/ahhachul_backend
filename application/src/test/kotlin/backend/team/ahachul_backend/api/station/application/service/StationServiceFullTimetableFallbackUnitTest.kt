package backend.team.ahachul_backend.api.station.application.service

import backend.team.ahachul_backend.api.common.application.port.out.SubwayLineStationReader
import backend.team.ahachul_backend.api.common.domain.entity.StationEntity
import backend.team.ahachul_backend.api.common.domain.entity.SubwayLineStationEntity
import backend.team.ahachul_backend.api.station.application.port.`in`.dto.GetStationTimesFullCommand
import backend.team.ahachul_backend.common.client.SeoulTrainClient
import backend.team.ahachul_backend.common.domain.entity.SubwayLineEntity
import backend.team.ahachul_backend.common.domain.model.RegionType
import backend.team.ahachul_backend.common.exception.BusinessException
import backend.team.ahachul_backend.common.response.ResponseCode
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito

class StationServiceFullTimetableFallbackUnitTest {

    private val subwayLineStationReader: SubwayLineStationReader = Mockito.mock(SubwayLineStationReader::class.java)
    private val stationTimesCacheUtils: StationTimesCacheUtils = Mockito.mock(StationTimesCacheUtils::class.java)
    private val seoulTrainClient: SeoulTrainClient = Mockito.mock(SeoulTrainClient::class.java)
    private val stationService = StationService(
        subwayLineStationReader = subwayLineStationReader,
        stationTimesCacheUtils = stationTimesCacheUtils,
        seoulTrainClient = seoulTrainClient,
    )

    @Test
    @DisplayName("전체 시간표 조회에서 외부 API 실패 시 요일/상하행 슬롯은 빈 배열로 fallback 된다.")
    fun getStationTimesFullFallbackOnExternalApiFail() {
        // given
        val station = StationEntity(
            id = 557L,
            name = "강남",
        )
        val subwayLine = SubwayLineEntity(
            id = 18L,
            name = "신분당선",
            regionType = RegionType.METROPOLITAN,
        )
        val subwayLineStation = SubwayLineStationEntity(
            id = 1L,
            stationCode = "4307",
            station = station,
            subwayLine = subwayLine,
        )

        given(subwayLineStationReader.findBySubwayLineIdAndStationId(18L, 557L))
            .willReturn(subwayLineStation)
        given(stationTimesCacheUtils.getStationTimesByCache(any()))
            .willReturn(null)
        given(seoulTrainClient.getStationTimesByApi(any()))
            .willThrow(BusinessException(ResponseCode.FAILED_STATION_TIMES_API))

        // when
        val result = stationService.getStationTimesFull(
            GetStationTimesFullCommand(
                stationId = 557L,
                subwayLineId = 18L,
            )
        )

        // then
        assertThat(result.stationId).isEqualTo(557L)
        assertThat(result.subwayLineId).isEqualTo(18L)
        assertThat(result.weeks).hasSize(3)
        assertThat(result.weeks.flatMap { it.upDownTimetables }).hasSize(6)
        assertThat(result.weeks.flatMap { it.upDownTimetables }.all { it.stationTimes.isEmpty() }).isTrue()
    }

    private fun <T> any(): T {
        Mockito.any<T>()
        return null as T
    }
}
