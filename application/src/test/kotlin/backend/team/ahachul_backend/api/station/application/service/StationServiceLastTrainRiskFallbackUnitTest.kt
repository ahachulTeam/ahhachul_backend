package backend.team.ahachul_backend.api.station.application.service

import backend.team.ahachul_backend.api.common.application.port.out.SubwayLineStationReader
import backend.team.ahachul_backend.api.common.domain.entity.StationEntity
import backend.team.ahachul_backend.api.common.domain.entity.SubwayLineStationEntity
import backend.team.ahachul_backend.api.station.adapter.`in`.dto.GetStationTimesDto
import backend.team.ahachul_backend.api.station.adapter.`in`.dto.StationTimeWeekType
import backend.team.ahachul_backend.api.station.application.port.`in`.dto.GetStationLastTrainRiskCommand
import backend.team.ahachul_backend.api.train.domain.model.UpDownType
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

class StationServiceLastTrainRiskFallbackUnitTest {

    private val subwayLineStationReader: SubwayLineStationReader = Mockito.mock(SubwayLineStationReader::class.java)
    private val stationTimesCacheUtils: StationTimesCacheUtils = Mockito.mock(StationTimesCacheUtils::class.java)
    private val seoulTrainClient: SeoulTrainClient = Mockito.mock(SeoulTrainClient::class.java)
    private val stationService = StationService(
        subwayLineStationReader = subwayLineStationReader,
        stationTimesCacheUtils = stationTimesCacheUtils,
        seoulTrainClient = seoulTrainClient,
    )

    @Test
    @DisplayName("역 시간표 외부 API 실패 시 막차 리스크는 예외 대신 위험 응답을 반환한다.")
    fun getLastTrainRiskFallbackOnStationTimesApiFail() {
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

        val command = GetStationLastTrainRiskCommand(
            stationId = 557L,
            subwayLineId = 18L,
            upDownType = UpDownType.UP,
            stationTimeWeekType = StationTimeWeekType.WEEKDAY,
            walkingMinutes = 15,
        )

        given(subwayLineStationReader.findBySubwayLineIdAndStationId(18L, 557L))
            .willReturn(subwayLineStation)
        given(stationTimesCacheUtils.getStationTimesByCache(any()))
            .willReturn(null)
        given(seoulTrainClient.getStationTimesByApi(any()))
            .willThrow(BusinessException(ResponseCode.FAILED_STATION_TIMES_API))

        // when
        val result = stationService.getLastTrainRisk(command)

        // then
        assertThat(result.lastDepartureTime).isNull()
        assertThat(result.minutesToLastTrain).isEqualTo(-1)
        assertThat(result.isLastTrainRisk).isTrue()
        assertThat(result.riskLevel).isEqualTo(GetStationTimesDto.LastTrainRiskLevel.RISK)
        assertThat(result.message).isEqualTo("막차 정보가 없습니다.")
    }

    private fun <T> any(): T {
        Mockito.any<T>()
        return null as T
    }
}
