package backend.team.ahachul_backend.api.station.application.service

import backend.team.ahachul_backend.api.common.adapter.web.out.StationRepository
import backend.team.ahachul_backend.api.common.adapter.web.out.SubwayLineStationRepository
import backend.team.ahachul_backend.api.common.domain.entity.StationEntity
import backend.team.ahachul_backend.api.common.domain.entity.SubwayLineStationEntity
import backend.team.ahachul_backend.api.station.adapter.`in`.dto.GetStationTimesDto
import backend.team.ahachul_backend.api.station.adapter.`in`.dto.StationTimeWeekType
import backend.team.ahachul_backend.api.station.application.port.`in`.StationUseCase
import backend.team.ahachul_backend.api.station.application.port.`in`.dto.GetStationTimesCommand
import backend.team.ahachul_backend.api.train.domain.model.TrainType
import backend.team.ahachul_backend.api.train.domain.model.UpDownType
import backend.team.ahachul_backend.common.client.SeoulTrainClient
import backend.team.ahachul_backend.common.domain.entity.SubwayLineEntity
import backend.team.ahachul_backend.common.domain.model.RegionType
import backend.team.ahachul_backend.common.client.dto.StationTimesDto.*
import backend.team.ahachul_backend.common.exception.AdapterException
import backend.team.ahachul_backend.common.exception.BusinessException
import backend.team.ahachul_backend.common.persistence.SubwayLineRepository
import backend.team.ahachul_backend.common.response.ResponseCode
import backend.team.ahachul_backend.config.controller.CommonServiceTestConfig
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean

class StationServiceTest(
    @Autowired val stationUseCase: StationUseCase,
    @Autowired val subwayLineRepository: SubwayLineRepository,
    @Autowired val stationRepository: StationRepository,
    @Autowired val subwayLineStationRepository: SubwayLineStationRepository,
) : CommonServiceTestConfig() {

    @MockBean
    lateinit var stationTimesCacheUtils: StationTimesCacheUtils

    @MockBean
    lateinit var seoulTrainClient: SeoulTrainClient

    @Test
    @DisplayName("유효한 정류장 ID와 지하철 노선 ID로 요청해야 한다.")
    fun getStationTimesWithValidStationIdAndSubwayLineId() {
        // given
        val command = GetStationTimesCommand(
            stationId = 2000L,
            subwayLineId = 1000L,
            upDownType = UpDownType.UP,
            stationTimeWeekType =StationTimeWeekType.WEEKDAY,
        )

        // when // then
        assertThatThrownBy {
            stationUseCase.getStationTimes(command)
        }
            .isExactlyInstanceOf(AdapterException::class.java)
            .hasMessage(ResponseCode.INVALID_DOMAIN.message)
    }

    @Test
    @DisplayName("공공 API에서 사용하는 역 코드가 존재해야한다.")
    fun getStationTimesWithEmptyStationCode() {
        // given
        val subwayLine = subwayLineRepository.save(
            SubwayLineEntity(
                name = "1호선",
                regionType = RegionType.METROPOLITAN
            )
        )

        val station = stationRepository.save(
            StationEntity(
                name = "종각"
            )
        )

        subwayLineStationRepository.save(
            SubwayLineStationEntity(
                stationCode = null,
                subwayLine = subwayLine,
                station = station
            )
        )

        val command = GetStationTimesCommand(
            stationId = station.id,
            subwayLineId = subwayLine.id,
            upDownType = UpDownType.UP,
            stationTimeWeekType =StationTimeWeekType.WEEKDAY,
        )

        // when // then
        assertThatThrownBy {
            stationUseCase.getStationTimes(command)
        }
            .isExactlyInstanceOf(BusinessException::class.java)
            .hasMessage(ResponseCode.NOT_EXIST_PUBLIC_STATION_CODE.message)
    }

    @Test
    @DisplayName("캐싱된 데이터가 존재하면 API 조회 없이 값을 반환한다.")
    fun getStationTimesReturnCachedDataIfExist() {
        // given
        val subwayLine = subwayLineRepository.save(
            SubwayLineEntity(
                name = "3호선",
                regionType = RegionType.METROPOLITAN
            )
        )

        val station = stationRepository.save(
            StationEntity(
                name = "수서"
            )
        )

        subwayLineStationRepository.save(
            SubwayLineStationEntity(
                stationCode = "0001",
                subwayLine = subwayLine,
                station = station
            )
        )

        val command = GetStationTimesCommand(
            stationId = station.id,
            subwayLineId = subwayLine.id,
            upDownType = UpDownType.UP,
            stationTimeWeekType =StationTimeWeekType.WEEKDAY,
        )

        val value = listOf(
            GetStationTimesDto.StationTimes(
                arrivalTime = "06:30:00",
                departureTime = "00:00:00",
                arrivalStationName = "대화",
                departureStationName = "수서",
                trainType = TrainType.GENERAL
            ),
            GetStationTimesDto.StationTimes(
                arrivalTime = "06:35:00",
                departureTime = "06:35:30",
                arrivalStationName = "구파발",
                departureStationName = "오금",
                trainType = TrainType.GENERAL
            )
        )

        given(stationTimesCacheUtils.getStationTimesByCache(any()))
            .willReturn(value)

        // when
        val result = stationUseCase.getStationTimes(command)

        // then
        assertThat(result.stationTimes.size).isEqualTo(2)
        assertThat(result.stationTimes)
            .extracting("arrivalTime", "departureTime", "arrivalStationName", "departureStationName", "trainType")
            .containsExactly(
                tuple("06:30:00", "00:00:00", "대화", "수서", TrainType.GENERAL),
                tuple("06:35:00", "06:35:30", "구파발", "오금", TrainType.GENERAL),
            )
    }

    @Test
    @DisplayName("역 시간표 API 조회에 실패하면 예외가 발생한다.")
    fun getStationTimesWithApiFail() {
        // given
        val subwayLine = subwayLineRepository.save(
            SubwayLineEntity(
                name = "3호선",
                regionType = RegionType.METROPOLITAN
            )
        )

        val station = stationRepository.save(
            StationEntity(
                name = "수서"
            )
        )

        subwayLineStationRepository.save(
            SubwayLineStationEntity(
                stationCode = "0001",
                subwayLine = subwayLine,
                station = station
            )
        )

        val command = GetStationTimesCommand(
            stationId = station.id,
            subwayLineId = subwayLine.id,
            upDownType = UpDownType.UP,
            stationTimeWeekType =StationTimeWeekType.WEEKDAY,
        )

        given(stationTimesCacheUtils.getStationTimesByCache(any()))
            .willReturn(null)

        given(seoulTrainClient.getStationTimesByApi(any()))
            .willThrow(BusinessException(ResponseCode.FAILED_STATION_TIMES_API))

        // when & then
        assertThatThrownBy {
            stationUseCase.getStationTimes(command)
        }
            .isExactlyInstanceOf(BusinessException::class.java)
            .hasMessage(ResponseCode.FAILED_STATION_TIMES_API.message)
    }

    @Test
    @DisplayName("역 시간표 API 응답값이 올바르지 않으면 예외가 발생한다.")
    fun getStationTimesWithInvalidApiResponse() {
        // given
        val subwayLine = subwayLineRepository.save(
            SubwayLineEntity(
                name = "3호선",
                regionType = RegionType.METROPOLITAN
            )
        )

        val station = stationRepository.save(
            StationEntity(
                name = "수서"
            )
        )

        subwayLineStationRepository.save(
            SubwayLineStationEntity(
                stationCode = "0001",
                subwayLine = subwayLine,
                station = station
            )
        )

        val command = GetStationTimesCommand(
            stationId = station.id,
            subwayLineId = subwayLine.id,
            upDownType = UpDownType.UP,
            stationTimeWeekType =StationTimeWeekType.WEEKDAY,
        )

        given(stationTimesCacheUtils.getStationTimesByCache(any()))
            .willReturn(null)

        given(seoulTrainClient.getStationTimesByApi(any()))
            .willReturn(
                Response(
                    StationTimesTable(
                        totalCount = 0,
                        result = StationTimesResult(
                            code = "INFO-001",
                            message = "실패",
                        ),
                        rows = emptyList()
                    )
                )
            )

        // when & then
        assertThatThrownBy {
            stationUseCase.getStationTimes(command)
        }
            .isExactlyInstanceOf(BusinessException::class.java)
            .hasMessage(ResponseCode.INVALID_STATION_TIMES_API_RESPONSE.message)
    }

    @Test
    @DisplayName("역 시간표 조회 성공")
    fun getStationTimes() {
        // given
        val subwayLine = subwayLineRepository.save(
            SubwayLineEntity(
                name = "3호선",
                regionType = RegionType.METROPOLITAN
            )
        )

        val station = stationRepository.save(
            StationEntity(
                name = "수서"
            )
        )

        subwayLineStationRepository.save(
            SubwayLineStationEntity(
                stationCode = "0001",
                subwayLine = subwayLine,
                station = station
            )
        )

        val command = GetStationTimesCommand(
            stationId = station.id,
            subwayLineId = subwayLine.id,
            upDownType = UpDownType.UP,
            stationTimeWeekType =StationTimeWeekType.WEEKDAY,
        )

        given(stationTimesCacheUtils.getStationTimesByCache(any()))
            .willReturn(null)

        given(seoulTrainClient.getStationTimesByApi(any()))
            .willReturn(
                Response(
                    StationTimesTable(
                        totalCount = 191,
                        result = StationTimesResult(
                            code = "INFO-000",
                            message = "정상 처리되었습니다",
                        ),
                        rows = listOf(
                            StationTimeRow(
                                lineNum = "03호선",
                                frCode = "349",
                                stationCd = "0339",
                                stationNm = "수서",
                                trainNo = "3022",
                                arrivetime = "00:00:00",
                                lefttime = "05:30:00",
                                originstation = "0339",
                                deststation = "1958",
                                subwaysname = "수서",
                                subwayename = "대화",
                                weekTag = "1",
                                inoutTag = "1",
                                flFlag = "",
                                deststation2 = "",
                                expressYn = "G",
                                branchLine = "",
                            )
                        )
                    )
                )
            )

        // when
        val result = stationUseCase.getStationTimes(command)

        // then
        assertThat(result.stationTimes.size).isEqualTo(1)
        assertThat(result.stationTimes)
            .extracting("arrivalTime", "departureTime", "arrivalStationName", "departureStationName", "trainType")
            .containsExactly(
                tuple("00:00:00", "05:30:00", "대화", "수서", TrainType.GENERAL),
            )
    }

    private fun <T> any(): T {
        Mockito.any<T>()
        return null as T
    }
}