package backend.team.ahachul_backend.api.station.adapter.`in`

import backend.team.ahachul_backend.api.station.adapter.`in`.dto.GetStationTimesDto
import backend.team.ahachul_backend.api.station.adapter.`in`.dto.SearchSubwayRouteDto
import backend.team.ahachul_backend.api.station.adapter.`in`.dto.SearchSubwayRouteQualityV3Dto
import backend.team.ahachul_backend.api.station.adapter.`in`.dto.StationTimeWeekType
import backend.team.ahachul_backend.api.station.application.port.`in`.StationUseCase
import backend.team.ahachul_backend.api.train.domain.model.TrainType
import backend.team.ahachul_backend.api.train.domain.model.UpDownType
import backend.team.ahachul_backend.config.controller.CommonDocsTestConfig
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.queryParameters
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(StationController::class)
class StationControllerDocsTest : CommonDocsTestConfig() {

    @MockBean
    lateinit var stationUseCase: StationUseCase

    @Test
    fun getStationTimes() {
        //given
        val response = GetStationTimesDto.Response(
            listOf(
                GetStationTimesDto.StationTimes(
                    arrivalTime = "00:00:00",
                    departureTime = "06:30:00",
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
        )

        given(stationUseCase.getStationTimes(any()))
            .willReturn(response)

        //when
        val result = mockMvc.perform(
            get("/v1/stations/times")
                .queryParam("stationId", 622.toString())
                .queryParam("subwayLineId", 3.toString())
                .queryParam("upDownType", UpDownType.UP.name)
                .queryParam("stationTimeWeekType", StationTimeWeekType.WEEKDAY.name)
                .accept(MediaType.APPLICATION_JSON)
        )

        //then
        result.andExpect(status().isOk)
            .andDo(
                document(
                    "get-station-times",
                    getDocsRequest(),
                    getDocsResponse(),
                    queryParameters(
                        parameterWithName("stationId").description("정류장 ID"),
                        parameterWithName("subwayLineId").description("지하철 노선 ID"),
                        parameterWithName("upDownType").description("상행(UP), 하행(DOWN)"),
                        parameterWithName("stationTimeWeekType").optional().description("평일(WEEKDAY), 토요일(SATURDAY), 공휴일(HOLIDAY)"),
                        parameterWithName("weekTag").optional().description("레거시 요일 코드(1=평일, 2=토요일, 3=공휴일)")
                    ),
                    PayloadDocumentation.responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.stationTimes[].arrivalTime").type(JsonFieldType.STRING).description("도착시간 - hh:mm:ss"),
                        fieldWithPath("result.stationTimes[].departureTime").type(JsonFieldType.STRING).description("출발시간 - hh:mm:ss"),
                        fieldWithPath("result.stationTimes[].arrivalStationName").type(JsonFieldType.STRING).description("도착역명"),
                        fieldWithPath("result.stationTimes[].departureStationName").type(JsonFieldType.STRING).description("출발역명"),
                        fieldWithPath("result.stationTimes[].trainType").type(JsonFieldType.STRING).description("급행(EXPRESS), 일반(GENERAL)"),
                    )
                )
            )
    }

    @Test
    fun getStationTimesSummary() {
        // given
        val response = GetStationTimesDto.SummaryResponse(
            stationTimeWeekType = StationTimeWeekType.WEEKDAY,
            summaries = listOf(
                GetStationTimesDto.UpDownSummary(
                    upDownType = UpDownType.UP,
                    firstDepartureTime = "05:31:00",
                    lastDepartureTime = "23:58:00",
                    firstDestinationStationName = "대화",
                    lastDestinationStationName = "오금",
                ),
                GetStationTimesDto.UpDownSummary(
                    upDownType = UpDownType.DOWN,
                    firstDepartureTime = "05:36:00",
                    lastDepartureTime = "23:54:00",
                    firstDestinationStationName = "수서",
                    lastDestinationStationName = "구파발",
                )
            ),
            meta = GetStationTimesDto.SummaryMeta(
                generatedAt = "2026-02-25T12:10:00+09:00",
                availabilityStatus = GetStationTimesDto.StationSummaryAvailabilityStatus.AVAILABLE,
                coveragePercent = 100,
                guidanceMessage = "첫차/막차 정보를 정상적으로 제공 중입니다.",
                sourceDetails = listOf(
                    GetStationTimesDto.SummarySourceDetail(
                        upDownType = UpDownType.UP,
                        dataSource = GetStationTimesDto.StationSummaryDataSource.API,
                        stationTimesCount = 120,
                        fallbackReasonCode = null,
                    ),
                    GetStationTimesDto.SummarySourceDetail(
                        upDownType = UpDownType.DOWN,
                        dataSource = GetStationTimesDto.StationSummaryDataSource.CACHE,
                        stationTimesCount = 118,
                        fallbackReasonCode = null,
                    ),
                ),
            ),
        )

        given(stationUseCase.getStationTimesSummary(any()))
            .willReturn(response)

        // when
        val result = mockMvc.perform(
            get("/v2/stations/times/summary")
                .queryParam("stationId", 622.toString())
                .queryParam("subwayLineId", 3.toString())
                .queryParam("stationTimeWeekType", StationTimeWeekType.WEEKDAY.name)
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
            .andDo(
                document(
                    "get-station-times-summary",
                    getDocsRequest(),
                    getDocsResponse(),
                    queryParameters(
                        parameterWithName("stationId").description("정류장 ID"),
                        parameterWithName("subwayLineId").description("지하철 노선 ID"),
                        parameterWithName("stationTimeWeekType").description("평일(WEEKDAY), 토요일(SATURDAY), 공휴일(HOLIDAY)")
                    ),
                    PayloadDocumentation.responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.stationTimeWeekType").type(JsonFieldType.STRING).description("요일 구분"),
                        fieldWithPath("result.summaries[].upDownType").type(JsonFieldType.STRING).description("상행(UP), 하행(DOWN)"),
                        fieldWithPath("result.summaries[].firstDepartureTime").type(JsonFieldType.STRING).optional().description("첫차 출발시간 - hh:mm:ss"),
                        fieldWithPath("result.summaries[].lastDepartureTime").type(JsonFieldType.STRING).optional().description("막차 출발시간 - hh:mm:ss"),
                        fieldWithPath("result.summaries[].firstDestinationStationName").type(JsonFieldType.STRING).optional().description("첫차 종착역명"),
                        fieldWithPath("result.summaries[].lastDestinationStationName").type(JsonFieldType.STRING).optional().description("막차 종착역명"),
                        fieldWithPath("result.meta.generatedAt").type(JsonFieldType.STRING).description("요약 생성 시각"),
                        fieldWithPath("result.meta.availabilityStatus").type(JsonFieldType.STRING).description("요약 가용성 상태(AVAILABLE/PARTIAL/EMPTY)"),
                        fieldWithPath("result.meta.coveragePercent").type(JsonFieldType.NUMBER).description("방향별 커버리지 비율(%)"),
                        fieldWithPath("result.meta.guidanceMessage").type(JsonFieldType.STRING).description("사용자 안내 문구"),
                        fieldWithPath("result.meta.sourceDetails[].upDownType").type(JsonFieldType.STRING).description("상행/하행"),
                        fieldWithPath("result.meta.sourceDetails[].dataSource").type(JsonFieldType.STRING).description("데이터 소스(CACHE/API/FALLBACK_EMPTY)"),
                        fieldWithPath("result.meta.sourceDetails[].stationTimesCount").type(JsonFieldType.NUMBER).description("해당 방향 시간표 개수"),
                        fieldWithPath("result.meta.sourceDetails[].fallbackReasonCode").type(JsonFieldType.STRING).optional().description("fallback 발생 코드"),
                    )
                )
            )
    }

    @Test
    fun getStationTimesQualityReport() {
        val response = GetStationTimesDto.QualityReportResponse(
            generatedAt = "2026-02-25T12:15:00+09:00",
            stationTimeWeekType = StationTimeWeekType.WEEKDAY,
            totalLineCount = 2,
            totalSampledStations = 20,
            totalNoDataStations = 7,
            overallNoDataRatioPercent = 35,
            lines = listOf(
                GetStationTimesDto.LineQualityReport(
                    subwayLineId = 3,
                    subwayLineName = "3호선",
                    sampledStations = 10,
                    noDataStations = 1,
                    noDataRatioPercent = 10,
                    fallbackStations = 0,
                    missingStationCodeStations = 0,
                    qualityLevel = GetStationTimesDto.StationTimeQualityLevel.GOOD,
                ),
                GetStationTimesDto.LineQualityReport(
                    subwayLineId = 18,
                    subwayLineName = "신분당선",
                    sampledStations = 10,
                    noDataStations = 6,
                    noDataRatioPercent = 60,
                    fallbackStations = 2,
                    missingStationCodeStations = 1,
                    qualityLevel = GetStationTimesDto.StationTimeQualityLevel.WARN,
                ),
            ),
        )

        given(stationUseCase.getStationTimesQualityReport(any()))
            .willReturn(response)

        val result = mockMvc.perform(
            get("/v2/stations/times/quality-report")
                .queryParam("stationTimeWeekType", StationTimeWeekType.WEEKDAY.name)
                .queryParam("samplePerLine", "10")
                .accept(MediaType.APPLICATION_JSON)
        )

        result.andExpect(status().isOk)
            .andDo(
                document(
                    "get-station-times-quality-report",
                    getDocsRequest(),
                    getDocsResponse(),
                    queryParameters(
                        parameterWithName("stationTimeWeekType").optional().description("평일(WEEKDAY), 토요일(SATURDAY), 공휴일(HOLIDAY)"),
                        parameterWithName("samplePerLine").optional().description("노선별 샘플링 역 개수(1~50)")
                    ),
                    PayloadDocumentation.responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.generatedAt").type(JsonFieldType.STRING).description("리포트 생성 시각"),
                        fieldWithPath("result.stationTimeWeekType").type(JsonFieldType.STRING).description("요일 구분"),
                        fieldWithPath("result.totalLineCount").type(JsonFieldType.NUMBER).description("리포트 대상 노선 수"),
                        fieldWithPath("result.totalSampledStations").type(JsonFieldType.NUMBER).description("전체 샘플링 역 수"),
                        fieldWithPath("result.totalNoDataStations").type(JsonFieldType.NUMBER).description("전체 no-data 역 수"),
                        fieldWithPath("result.overallNoDataRatioPercent").type(JsonFieldType.NUMBER).description("전체 no-data 비율(%)"),
                        fieldWithPath("result.lines[].subwayLineId").type(JsonFieldType.NUMBER).description("노선 ID"),
                        fieldWithPath("result.lines[].subwayLineName").type(JsonFieldType.STRING).description("노선명"),
                        fieldWithPath("result.lines[].sampledStations").type(JsonFieldType.NUMBER).description("샘플링 역 수"),
                        fieldWithPath("result.lines[].noDataStations").type(JsonFieldType.NUMBER).description("no-data 역 수"),
                        fieldWithPath("result.lines[].noDataRatioPercent").type(JsonFieldType.NUMBER).description("no-data 비율(%)"),
                        fieldWithPath("result.lines[].fallbackStations").type(JsonFieldType.NUMBER).description("fallback 발생 역 수"),
                        fieldWithPath("result.lines[].missingStationCodeStations").type(JsonFieldType.NUMBER).description("역 코드 누락 역 수"),
                        fieldWithPath("result.lines[].qualityLevel").type(JsonFieldType.STRING).description("품질 등급(GOOD/WARN/CRITICAL)")
                    )
                )
            )
    }

    @Test
    fun getLastTrainRisk() {
        val response = GetStationTimesDto.LastTrainRiskResponse(
            stationTimeWeekType = StationTimeWeekType.WEEKDAY,
            upDownType = UpDownType.DOWN,
            walkingMinutes = 15,
            nowAt = "2026-02-23T23:10:00+09:00",
            lastDepartureTime = "23:58:00",
            minutesToLastTrain = 48,
            isLastTrainRisk = false,
            riskLevel = GetStationTimesDto.LastTrainRiskLevel.SAFE,
            message = "현재 기준 막차 여유가 있습니다.",
        )

        given(stationUseCase.getLastTrainRisk(any()))
            .willReturn(response)

        val result = mockMvc.perform(
            get("/v2/stations/times/last-train-risk")
                .queryParam("stationId", 622.toString())
                .queryParam("subwayLineId", 3.toString())
                .queryParam("upDownType", UpDownType.DOWN.name)
                .queryParam("stationTimeWeekType", StationTimeWeekType.WEEKDAY.name)
                .queryParam("walkingMinutes", "15")
                .accept(MediaType.APPLICATION_JSON)
        )

        result.andExpect(status().isOk)
            .andDo(
                document(
                    "get-station-last-train-risk",
                    getDocsRequest(),
                    getDocsResponse(),
                    queryParameters(
                        parameterWithName("stationId").description("정류장 ID"),
                        parameterWithName("subwayLineId").description("지하철 노선 ID"),
                        parameterWithName("upDownType").description("상행(UP), 하행(DOWN)"),
                        parameterWithName("stationTimeWeekType").description("평일(WEEKDAY), 토요일(SATURDAY), 공휴일(HOLIDAY)"),
                        parameterWithName("walkingMinutes").description("도보 이동 시간(분)")
                    ),
                    PayloadDocumentation.responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.stationTimeWeekType").type(JsonFieldType.STRING).description("요일 구분"),
                        fieldWithPath("result.upDownType").type(JsonFieldType.STRING).description("상행/하행"),
                        fieldWithPath("result.walkingMinutes").type(JsonFieldType.NUMBER).description("도보 이동 시간(분)"),
                        fieldWithPath("result.nowAt").type(JsonFieldType.STRING).description("계산 기준 시각"),
                        fieldWithPath("result.lastDepartureTime").type(JsonFieldType.STRING).optional().description("막차 출발시각 - hh:mm:ss"),
                        fieldWithPath("result.minutesToLastTrain").type(JsonFieldType.NUMBER).description("막차까지 남은 분"),
                        fieldWithPath("result.isLastTrainRisk").type(JsonFieldType.BOOLEAN).description("막차 위험 여부"),
                        fieldWithPath("result.riskLevel").type(JsonFieldType.STRING).description("위험도(SAFE/WARN/RISK)"),
                        fieldWithPath("result.message").type(JsonFieldType.STRING).description("안내 문구"),
                    )
                )
            )
    }

    @Test
    fun getQuickExits() {
        val response = GetStationTimesDto.QuickExitResponse(
            stationId = 622,
            subwayLineId = 3,
            upDownType = UpDownType.DOWN,
            recommendations = listOf(
                GetStationTimesDto.QuickExitRecommendation(
                    carNo = "5-2",
                    exitNo = "3",
                    directionHint = "환승 통로 우측",
                    walkingBenefitMinutes = 2,
                    confidenceLevel = GetStationTimesDto.QuickExitConfidenceLevel.MEDIUM,
                )
            ),
        )

        given(stationUseCase.getQuickExits(any()))
            .willReturn(response)

        val result = mockMvc.perform(
            get("/v2/stations/quick-exits")
                .queryParam("stationId", 622.toString())
                .queryParam("subwayLineId", 3.toString())
                .queryParam("upDownType", UpDownType.DOWN.name)
                .accept(MediaType.APPLICATION_JSON)
        )

        result.andExpect(status().isOk)
            .andDo(
                document(
                    "get-station-quick-exits",
                    getDocsRequest(),
                    getDocsResponse(),
                    queryParameters(
                        parameterWithName("stationId").description("정류장 ID"),
                        parameterWithName("subwayLineId").description("지하철 노선 ID"),
                        parameterWithName("upDownType").description("상행(UP), 하행(DOWN)"),
                    ),
                    PayloadDocumentation.responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.stationId").type(JsonFieldType.NUMBER).description("정류장 ID"),
                        fieldWithPath("result.subwayLineId").type(JsonFieldType.NUMBER).description("지하철 노선 ID"),
                        fieldWithPath("result.upDownType").type(JsonFieldType.STRING).description("상행/하행"),
                        fieldWithPath("result.recommendations[].carNo").type(JsonFieldType.STRING).description("추천 탑승 칸"),
                        fieldWithPath("result.recommendations[].exitNo").type(JsonFieldType.STRING).description("추천 출구 번호"),
                        fieldWithPath("result.recommendations[].directionHint").type(JsonFieldType.STRING).description("동선 힌트"),
                        fieldWithPath("result.recommendations[].walkingBenefitMinutes").type(JsonFieldType.NUMBER).description("예상 단축 시간(분)"),
                        fieldWithPath("result.recommendations[].confidenceLevel").type(JsonFieldType.STRING).description("신뢰도(HIGH/MEDIUM/LOW)"),
                    )
                )
            )
    }

    @Test
    fun searchSubwayRoutesV3() {
        val response = SearchSubwayRouteQualityV3Dto.Response(
            modelVersion = "ROUTE_QUALITY_V3",
            generatedAt = "2026-02-25T23:55:00+09:00",
            sourceStationId = 622L,
            destinationStationId = 101L,
            strategy = SearchSubwayRouteDto.RouteSearchStrategy.BALANCED,
            walkingPreference = SearchSubwayRouteQualityV3Dto.RouteWalkingPreference.LESS_STAIRS,
            stationTimeWeekType = StationTimeWeekType.WEEKDAY,
            routes = listOf(
                SearchSubwayRouteQualityV3Dto.Route(
                    rank = 1,
                    nodes = listOf(
                        SearchSubwayRouteQualityV3Dto.Node(
                            stationId = 622,
                            stationName = "교대",
                            order = 0,
                            isTransfer = false,
                        ),
                        SearchSubwayRouteQualityV3Dto.Node(
                            stationId = 101,
                            stationName = "강남",
                            order = 1,
                            isTransfer = false,
                        )
                    ),
                    edges = listOf(
                        SearchSubwayRouteQualityV3Dto.Edge(
                            fromStationId = 622,
                            toStationId = 101,
                            subwayLineId = 2,
                            subwayLineName = "2호선",
                        )
                    ),
                    summary = SearchSubwayRouteQualityV3Dto.Summary(
                        totalStops = 1,
                        transferCount = 0,
                        estimatedMinutes = 4,
                    ),
                    quality = SearchSubwayRouteQualityV3Dto.Quality(
                        totalScore = 88,
                        transferRiskScore = 100,
                        walkingScore = 92,
                        lastTrainSafetyScore = 80,
                        delayResilienceScore = 76,
                        delayProbabilityPercent = 24,
                        confidenceLevel = SearchSubwayRouteQualityV3Dto.RouteQualityConfidenceLevel.HIGH,
                        badges = listOf(SearchSubwayRouteQualityV3Dto.RouteQualityBadge.BEST_RECOMMENDED),
                        reasons = listOf("환승 0회로 비교적 안정적인 환승 동선입니다."),
                    ),
                )
            ),
        )

        given(stationUseCase.searchSubwayRoutesV3(any()))
            .willReturn(response)

        val result = mockMvc.perform(
            get("/v3/subway/routes/search")
                .queryParam("sourceStationId", "622")
                .queryParam("destinationStationId", "101")
                .queryParam("strategy", SearchSubwayRouteDto.RouteSearchStrategy.BALANCED.name)
                .queryParam("alternatives", "3")
                .queryParam("walkingPreference", SearchSubwayRouteQualityV3Dto.RouteWalkingPreference.LESS_STAIRS.name)
                .queryParam("stationTimeWeekType", StationTimeWeekType.WEEKDAY.name)
                .accept(MediaType.APPLICATION_JSON)
        )

        result.andExpect(status().isOk)
            .andDo(
                document(
                    "search-subway-routes-v3",
                    getDocsRequest(),
                    getDocsResponse(),
                    queryParameters(
                        parameterWithName("sourceStationId").description("출발역 ID"),
                        parameterWithName("destinationStationId").description("도착역 ID"),
                        parameterWithName("strategy").optional().description("경로 탐색 전략(BALANCED/MIN_TRANSFER/MIN_STOP)"),
                        parameterWithName("alternatives").optional().description("대체 경로 수(1~4)"),
                        parameterWithName("walkingPreference").optional().description("보행 선호(FAST/LESS_STAIRS)"),
                        parameterWithName("stationTimeWeekType").optional().description("평일(WEEKDAY), 토요일(SATURDAY), 공휴일(HOLIDAY)"),
                    ),
                    PayloadDocumentation.responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.modelVersion").type(JsonFieldType.STRING).description("경로 품질 모델 버전"),
                        fieldWithPath("result.generatedAt").type(JsonFieldType.STRING).description("응답 생성 시각"),
                        fieldWithPath("result.sourceStationId").type(JsonFieldType.NUMBER).description("출발역 ID"),
                        fieldWithPath("result.destinationStationId").type(JsonFieldType.NUMBER).description("도착역 ID"),
                        fieldWithPath("result.strategy").type(JsonFieldType.STRING).description("경로 탐색 전략"),
                        fieldWithPath("result.walkingPreference").type(JsonFieldType.STRING).description("보행 선호"),
                        fieldWithPath("result.stationTimeWeekType").type(JsonFieldType.STRING).description("요일 구분"),
                        fieldWithPath("result.routes[].rank").type(JsonFieldType.NUMBER).description("추천 순위"),
                        fieldWithPath("result.routes[].nodes[].stationId").type(JsonFieldType.NUMBER).description("역 ID"),
                        fieldWithPath("result.routes[].nodes[].stationName").type(JsonFieldType.STRING).description("역 이름"),
                        fieldWithPath("result.routes[].nodes[].order").type(JsonFieldType.NUMBER).description("경로 상 순서"),
                        fieldWithPath("result.routes[].nodes[].isTransfer").type(JsonFieldType.BOOLEAN).description("환승 여부"),
                        fieldWithPath("result.routes[].edges[].fromStationId").type(JsonFieldType.NUMBER).description("출발역 ID"),
                        fieldWithPath("result.routes[].edges[].toStationId").type(JsonFieldType.NUMBER).description("도착역 ID"),
                        fieldWithPath("result.routes[].edges[].subwayLineId").type(JsonFieldType.NUMBER).description("노선 ID"),
                        fieldWithPath("result.routes[].edges[].subwayLineName").type(JsonFieldType.STRING).description("노선명"),
                        fieldWithPath("result.routes[].summary.totalStops").type(JsonFieldType.NUMBER).description("정차 수"),
                        fieldWithPath("result.routes[].summary.transferCount").type(JsonFieldType.NUMBER).description("환승 수"),
                        fieldWithPath("result.routes[].summary.estimatedMinutes").type(JsonFieldType.NUMBER).description("예상 소요 시간(분)"),
                        fieldWithPath("result.routes[].quality.totalScore").type(JsonFieldType.NUMBER).description("종합 품질 점수(0~100)"),
                        fieldWithPath("result.routes[].quality.transferRiskScore").type(JsonFieldType.NUMBER).description("환승 리스크 점수"),
                        fieldWithPath("result.routes[].quality.walkingScore").type(JsonFieldType.NUMBER).description("보행 부담 점수"),
                        fieldWithPath("result.routes[].quality.lastTrainSafetyScore").type(JsonFieldType.NUMBER).description("막차 안전도 점수"),
                        fieldWithPath("result.routes[].quality.delayResilienceScore").type(JsonFieldType.NUMBER).description("지연 복원력 점수"),
                        fieldWithPath("result.routes[].quality.delayProbabilityPercent").type(JsonFieldType.NUMBER).description("지연 확률(%)"),
                        fieldWithPath("result.routes[].quality.confidenceLevel").type(JsonFieldType.STRING).description("품질 점수 신뢰도(HIGH/MEDIUM/LOW)"),
                        fieldWithPath("result.routes[].quality.badges").type(JsonFieldType.ARRAY).description("리스크/추천 배지"),
                        fieldWithPath("result.routes[].quality.reasons").type(JsonFieldType.ARRAY).description("추천 사유"),
                    )
                )
            )
    }
}
