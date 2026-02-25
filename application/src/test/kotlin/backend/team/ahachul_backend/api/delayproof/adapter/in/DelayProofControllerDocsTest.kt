package backend.team.ahachul_backend.api.delayproof.adapter.`in`

import backend.team.ahachul_backend.api.delayproof.adapter.`in`.dto.DelayProofDto
import backend.team.ahachul_backend.api.delayproof.application.port.`in`.DelayProofUseCase
import backend.team.ahachul_backend.api.train.domain.model.UpDownType
import backend.team.ahachul_backend.config.controller.CommonDocsTestConfig
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.restdocs.request.RequestDocumentation.queryParameters
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(DelayProofController::class)
class DelayProofControllerDocsTest : CommonDocsTestConfig() {

    @MockBean
    lateinit var delayProofUseCase: DelayProofUseCase

    @Test
    fun createDelayProof() {
        val response = buildCreateResponse()

        given(delayProofUseCase.createDelayProof(any()))
            .willReturn(response)

        val request = DelayProofDto.CreateRequest(
            stationId = 201,
            subwayLineId = 2,
            upDownType = UpDownType.UP,
            expectedArrivalAt = "2026-02-24T10:35:00+09:00",
            customMessage = "최대한 빨리 가겠습니다.",
        )

        mockMvc.perform(
            post("/v2/delay-proofs")
                .header("Authorization", "Bearer <Access Token>")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andDo(
                document(
                    "create-delay-proof-v2",
                    getDocsRequest(),
                    getDocsResponse(),
                    requestHeaders(
                        headerWithName("Authorization").description("엑세스 토큰"),
                    ),
                    requestFields(
                        fieldWithPath("stationId").type(JsonFieldType.NUMBER).description("정류장 ID"),
                        fieldWithPath("subwayLineId").type(JsonFieldType.NUMBER).description("지하철 노선 ID"),
                        fieldWithPath("upDownType").type(JsonFieldType.STRING).optional().description("상하행(UP/DOWN)"),
                        fieldWithPath("expectedArrivalAt").type(JsonFieldType.STRING).optional().description("예상 도착 시각(ISO8601)"),
                        fieldWithPath("customMessage").type(JsonFieldType.STRING).optional().description("사용자 커스텀 문구"),
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.proofId").type(JsonFieldType.STRING).description("증빙 ID"),
                        fieldWithPath("result.issuedAt").type(JsonFieldType.STRING).description("발급 시각"),
                        fieldWithPath("result.expiresAt").type(JsonFieldType.STRING).description("만료 시각"),
                        fieldWithPath("result.grade").type(JsonFieldType.STRING).description("증빙 등급(A/B/C)"),
                        fieldWithPath("result.confidenceLevel").type(JsonFieldType.STRING).description("신뢰도(HIGH/MEDIUM/LOW)"),
                        fieldWithPath("result.evidenceSummary.official.matched").type(JsonFieldType.BOOLEAN).description("공식 공지 매칭 여부"),
                        fieldWithPath("result.evidenceSummary.official.eventCount").type(JsonFieldType.NUMBER).description("공식 공지 개수"),
                        fieldWithPath("result.evidenceSummary.official.dataSource").type(JsonFieldType.STRING).description("공식 공지 소스"),
                        fieldWithPath("result.evidenceSummary.official.incidents[]").type(JsonFieldType.ARRAY).description("공식 공지 목록"),
                        fieldWithPath("result.evidenceSummary.official.incidents[].eventId").type(JsonFieldType.STRING).description("이벤트 ID"),
                        fieldWithPath("result.evidenceSummary.official.incidents[].occurredAt").type(JsonFieldType.STRING).description("발생 시각"),
                        fieldWithPath("result.evidenceSummary.official.incidents[].resolvedAt").type(JsonFieldType.STRING).optional().description("해제 시각"),
                        fieldWithPath("result.evidenceSummary.official.incidents[].severity").type(JsonFieldType.STRING).description("심각도"),
                        fieldWithPath("result.evidenceSummary.official.incidents[].title").type(JsonFieldType.STRING).description("제목"),
                        fieldWithPath("result.evidenceSummary.official.incidents[].description").type(JsonFieldType.STRING).description("설명"),
                        fieldWithPath("result.evidenceSummary.official.incidents[].source").type(JsonFieldType.STRING).description("출처"),
                        fieldWithPath("result.evidenceSummary.official.incidents[].sourceUrl").type(JsonFieldType.STRING).optional().description("출처 URL"),
                        fieldWithPath("result.evidenceSummary.community.signalCount").type(JsonFieldType.NUMBER).description("커뮤니티 시그널 수"),
                        fieldWithPath("result.evidenceSummary.community.distinctAuthors").type(JsonFieldType.NUMBER).description("작성자 수"),
                        fieldWithPath("result.evidenceSummary.community.medianReportedDelayMin").type(JsonFieldType.NUMBER).optional().description("지연 분 중앙값"),
                        fieldWithPath("result.evidenceSummary.community.confidenceLevel").type(JsonFieldType.STRING).description("커뮤니티 신뢰도"),
                        fieldWithPath("result.evidenceSummary.community.signals[]").type(JsonFieldType.ARRAY).description("시그널 샘플"),
                        fieldWithPath("result.evidenceSummary.community.signals[].postId").type(JsonFieldType.NUMBER).description("게시글 ID"),
                        fieldWithPath("result.evidenceSummary.community.signals[].createdAt").type(JsonFieldType.STRING).description("작성 시각"),
                        fieldWithPath("result.evidenceSummary.community.signals[].writer").type(JsonFieldType.STRING).description("작성자"),
                        fieldWithPath("result.evidenceSummary.community.signals[].matchedKeyword").type(JsonFieldType.STRING).description("매칭 키워드"),
                        fieldWithPath("result.evidenceSummary.community.signals[].reportedDelayMin").type(JsonFieldType.NUMBER).optional().description("추정 지연 분"),
                        fieldWithPath("result.evidenceSummary.community.signals[].snippet").type(JsonFieldType.STRING).description("본문 요약"),
                        fieldWithPath("result.evidenceSummary.realtime.isStale").type(JsonFieldType.BOOLEAN).description("실시간 stale 여부"),
                        fieldWithPath("result.evidenceSummary.realtime.freshnessSec").type(JsonFieldType.NUMBER).description("실시간 신선도(초)"),
                        fieldWithPath("result.evidenceSummary.realtime.confidenceLevel").type(JsonFieldType.STRING).description("실시간 신뢰도"),
                        fieldWithPath("result.evidenceSummary.realtime.generatedAt").type(JsonFieldType.STRING).description("실시간 생성 시각"),
                        fieldWithPath("result.text").type(JsonFieldType.STRING).description("공유 텍스트"),
                        fieldWithPath("result.shareUrl").type(JsonFieldType.STRING).description("검증 링크"),
                        fieldWithPath("result.signature").type(JsonFieldType.STRING).description("서명"),
                    ),
                ),
            )
    }

    @Test
    fun getDelayProof() {
        val response = DelayProofDto.GetResponse(
            proofId = "dpv2_01abc",
            issuedAt = "2026-02-24T01:20:00Z",
            expiresAt = "2026-02-25T01:20:00Z",
            grade = "B",
            confidenceLevel = "MEDIUM",
            evidenceSummary = DelayProofDto.DelayProofEvidenceSummary(
                official = DelayProofDto.OfficialEvidence(
                    matched = false,
                    eventCount = 0,
                    dataSource = "OFFICIAL_FEED_NOT_CONFIGURED",
                    incidents = emptyList(),
                ),
                community = DelayProofDto.CommunityEvidence(
                    signalCount = 0,
                    distinctAuthors = 0,
                    medianReportedDelayMin = null,
                    confidenceLevel = "LOW",
                    signals = emptyList(),
                ),
                realtime = DelayProofDto.RealtimeEvidence(
                    isStale = true,
                    freshnessSec = 140,
                    confidenceLevel = "LOW",
                    generatedAt = "2026-02-24T01:19:36Z",
                ),
            ),
            text = "지하철 지연으로 10:35 도착예정입니다.",
            shareUrl = "https://ahhachul.com/proofs/dpv2_01abc",
            signature = "c2lnbmF0dXJl",
        )

        given(delayProofUseCase.getDelayProof("dpv2_01abc"))
            .willReturn(response)

        mockMvc.perform(
            get("/v2/delay-proofs/{proofId}", "dpv2_01abc")
                .accept(MediaType.APPLICATION_JSON),
        )
            .andExpect(status().isOk)
            .andDo(
                document(
                    "get-delay-proof-v2",
                    getDocsRequest(),
                    getDocsResponse(),
                    pathParameters(
                        parameterWithName("proofId").description("증빙 ID"),
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.proofId").type(JsonFieldType.STRING).description("증빙 ID"),
                        fieldWithPath("result.issuedAt").type(JsonFieldType.STRING).description("발급 시각"),
                        fieldWithPath("result.expiresAt").type(JsonFieldType.STRING).description("만료 시각"),
                        fieldWithPath("result.grade").type(JsonFieldType.STRING).description("증빙 등급"),
                        fieldWithPath("result.confidenceLevel").type(JsonFieldType.STRING).description("신뢰도"),
                        fieldWithPath("result.evidenceSummary.official.matched").type(JsonFieldType.BOOLEAN).description("공식 공지 매칭 여부"),
                        fieldWithPath("result.evidenceSummary.official.eventCount").type(JsonFieldType.NUMBER).description("공식 공지 개수"),
                        fieldWithPath("result.evidenceSummary.official.dataSource").type(JsonFieldType.STRING).description("공식 공지 소스"),
                        fieldWithPath("result.evidenceSummary.official.incidents[]").type(JsonFieldType.ARRAY).description("공식 공지 목록"),
                        fieldWithPath("result.evidenceSummary.community.signalCount").type(JsonFieldType.NUMBER).description("커뮤니티 시그널 수"),
                        fieldWithPath("result.evidenceSummary.community.distinctAuthors").type(JsonFieldType.NUMBER).description("작성자 수"),
                        fieldWithPath("result.evidenceSummary.community.medianReportedDelayMin").type(JsonFieldType.NUMBER).optional().description("지연 분 중앙값"),
                        fieldWithPath("result.evidenceSummary.community.confidenceLevel").type(JsonFieldType.STRING).description("커뮤니티 신뢰도"),
                        fieldWithPath("result.evidenceSummary.community.signals[]").type(JsonFieldType.ARRAY).description("시그널 샘플"),
                        fieldWithPath("result.evidenceSummary.realtime.isStale").type(JsonFieldType.BOOLEAN).description("실시간 stale 여부"),
                        fieldWithPath("result.evidenceSummary.realtime.freshnessSec").type(JsonFieldType.NUMBER).description("실시간 신선도(초)"),
                        fieldWithPath("result.evidenceSummary.realtime.confidenceLevel").type(JsonFieldType.STRING).description("실시간 신뢰도"),
                        fieldWithPath("result.evidenceSummary.realtime.generatedAt").type(JsonFieldType.STRING).description("실시간 생성 시각"),
                        fieldWithPath("result.text").type(JsonFieldType.STRING).description("공유 텍스트"),
                        fieldWithPath("result.shareUrl").type(JsonFieldType.STRING).description("검증 링크"),
                        fieldWithPath("result.signature").type(JsonFieldType.STRING).description("서명"),
                    ),
                ),
            )
    }

    @Test
    fun getSubwayIncidents() {
        val response = DelayProofDto.GetSubwayIncidentsResponse(
            generatedAt = "2026-02-24T01:20:00Z",
            dataSource = "OFFICIAL_FEED",
            incidents = listOf(
                DelayProofDto.OfficialIncident(
                    eventId = "incident-1",
                    occurredAt = "2026-02-24T01:00:00Z",
                    resolvedAt = null,
                    severity = "DELAY",
                    title = "2호선 지연",
                    description = "신호 이상으로 지연",
                    source = "SEOUL_METRO",
                    sourceUrl = "https://example.com/incident-1",
                ),
            ),
        )

        given(delayProofUseCase.getSubwayIncidents(any()))
            .willReturn(response)

        mockMvc.perform(
            get("/v2/subway/incidents")
                .queryParam("subwayLineId", "2")
                .queryParam("stationId", "201")
                .queryParam("limit", "10"),
        )
            .andExpect(status().isOk)
            .andDo(
                document(
                    "get-subway-incidents-v2",
                    getDocsRequest(),
                    getDocsResponse(),
                    queryParameters(
                        parameterWithName("subwayLineId").description("지하철 노선 ID"),
                        parameterWithName("stationId").optional().description("정류장 ID"),
                        parameterWithName("limit").optional().description("최대 반환 개수(기본 10)"),
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.generatedAt").type(JsonFieldType.STRING).description("응답 생성 시각"),
                        fieldWithPath("result.dataSource").type(JsonFieldType.STRING).description("공식 공지 소스"),
                        fieldWithPath("result.incidents[]").type(JsonFieldType.ARRAY).description("공지 목록"),
                        fieldWithPath("result.incidents[].eventId").type(JsonFieldType.STRING).description("이벤트 ID"),
                        fieldWithPath("result.incidents[].occurredAt").type(JsonFieldType.STRING).description("발생 시각"),
                        fieldWithPath("result.incidents[].resolvedAt").type(JsonFieldType.STRING).optional().description("해제 시각"),
                        fieldWithPath("result.incidents[].severity").type(JsonFieldType.STRING).description("심각도"),
                        fieldWithPath("result.incidents[].title").type(JsonFieldType.STRING).description("제목"),
                        fieldWithPath("result.incidents[].description").type(JsonFieldType.STRING).description("설명"),
                        fieldWithPath("result.incidents[].source").type(JsonFieldType.STRING).description("출처"),
                        fieldWithPath("result.incidents[].sourceUrl").type(JsonFieldType.STRING).optional().description("출처 URL"),
                    ),
                ),
            )
    }

    @Test
    fun getCommunityDelaySignals() {
        val response = DelayProofDto.GetCommunityDelaySignalsResponse(
            generatedAt = "2026-02-24T01:20:00Z",
            subwayLineId = 2,
            windowMinutes = 30,
            signalCount = 18,
            distinctAuthors = 11,
            medianReportedDelayMin = 8,
            confidenceLevel = "HIGH",
            signals = listOf(
                DelayProofDto.CommunityDelaySignal(
                    postId = 1001,
                    createdAt = "2026-02-24T01:10:00Z",
                    writer = "출근러1",
                    matchedKeyword = "지연",
                    reportedDelayMin = 10,
                    snippet = "2호선 지연 심하네요",
                ),
            ),
        )

        given(delayProofUseCase.getCommunityDelaySignals(any()))
            .willReturn(response)

        mockMvc.perform(
            get("/v2/community/delay-signals")
                .queryParam("subwayLineId", "2")
                .queryParam("windowMinutes", "30")
                .queryParam("limit", "20"),
        )
            .andExpect(status().isOk)
            .andDo(
                document(
                    "get-community-delay-signals-v2",
                    getDocsRequest(),
                    getDocsResponse(),
                    queryParameters(
                        parameterWithName("subwayLineId").description("지하철 노선 ID"),
                        parameterWithName("windowMinutes").optional().description("집계 윈도우(분)"),
                        parameterWithName("limit").optional().description("조회 개수"),
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.generatedAt").type(JsonFieldType.STRING).description("응답 생성 시각"),
                        fieldWithPath("result.subwayLineId").type(JsonFieldType.NUMBER).description("지하철 노선 ID"),
                        fieldWithPath("result.windowMinutes").type(JsonFieldType.NUMBER).description("집계 윈도우(분)"),
                        fieldWithPath("result.signalCount").type(JsonFieldType.NUMBER).description("시그널 수"),
                        fieldWithPath("result.distinctAuthors").type(JsonFieldType.NUMBER).description("작성자 수"),
                        fieldWithPath("result.medianReportedDelayMin").type(JsonFieldType.NUMBER).optional().description("지연 분 중앙값"),
                        fieldWithPath("result.confidenceLevel").type(JsonFieldType.STRING).description("신뢰도"),
                        fieldWithPath("result.signals[]").type(JsonFieldType.ARRAY).description("시그널 목록"),
                        fieldWithPath("result.signals[].postId").type(JsonFieldType.NUMBER).description("게시글 ID"),
                        fieldWithPath("result.signals[].createdAt").type(JsonFieldType.STRING).description("작성 시각"),
                        fieldWithPath("result.signals[].writer").type(JsonFieldType.STRING).description("작성자"),
                        fieldWithPath("result.signals[].matchedKeyword").type(JsonFieldType.STRING).description("매칭 키워드"),
                        fieldWithPath("result.signals[].reportedDelayMin").type(JsonFieldType.NUMBER).optional().description("추정 지연 분"),
                        fieldWithPath("result.signals[].snippet").type(JsonFieldType.STRING).description("본문 요약"),
                    ),
                ),
            )
    }

    @Test
    fun getDelayCenterOverview() {
        val response = DelayProofDto.GetDelayCenterOverviewResponse(
            generatedAt = "2026-02-24T01:20:00Z",
            stationId = 201,
            subwayLineId = 2,
            upDownType = UpDownType.UP,
            realtime = DelayProofDto.DelayCenterRealtime(
                generatedAt = "2026-02-24T01:19:36Z",
                dataSource = "API",
                isStale = false,
                freshnessSec = 24,
                confidenceLevel = "HIGH",
                etaSec = 180,
                etaMinDisplay = 3,
                destinationStationDirection = "성수행",
                nextStationDirection = "잠실방면",
            ),
            official = DelayProofDto.DelayCenterOfficial(
                dataSource = "OFFICIAL_FEED",
                eventCount = 2,
                activeEventCount = 1,
                incidents = buildEvidenceSummary().official.incidents,
            ),
            community = buildEvidenceSummary().community,
            recommendation = DelayProofDto.DelayCenterRecommendation(
                gradePreview = "A",
                confidenceLevel = "HIGH",
                estimatedDelayMin = 12,
                recommendedExpectedArrivalAt = "2026-02-24T01:32:00Z",
                recommendedMessage = "지하철 지연으로 10:35 도착예정입니다. 공식공지 2건/동일 호선 커뮤니티 18건 확인(10:21 생성). 최대한 빨리 가겠습니다.",
            ),
        )

        given(delayProofUseCase.getDelayCenterOverview(any()))
            .willReturn(response)

        mockMvc.perform(
            get("/v2/delay-centers/overview")
                .queryParam("stationId", "201")
                .queryParam("subwayLineId", "2")
                .queryParam("upDownType", "UP")
                .queryParam("windowMinutes", "30")
                .queryParam("incidentLimit", "10")
                .queryParam("signalLimit", "50"),
        )
            .andExpect(status().isOk)
            .andDo(
                document(
                    "get-delay-center-overview-v2",
                    getDocsRequest(),
                    getDocsResponse(),
                    queryParameters(
                        parameterWithName("stationId").description("정류장 ID"),
                        parameterWithName("subwayLineId").description("지하철 노선 ID"),
                        parameterWithName("upDownType").optional().description("상하행(UP/DOWN)"),
                        parameterWithName("windowMinutes").optional().description("커뮤니티 집계 윈도우(분)"),
                        parameterWithName("incidentLimit").optional().description("공식 공지 최대 조회 건수"),
                        parameterWithName("signalLimit").optional().description("커뮤니티 시그널 최대 조회 건수"),
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.generatedAt").type(JsonFieldType.STRING).description("응답 생성 시각"),
                        fieldWithPath("result.stationId").type(JsonFieldType.NUMBER).description("정류장 ID"),
                        fieldWithPath("result.subwayLineId").type(JsonFieldType.NUMBER).description("지하철 노선 ID"),
                        fieldWithPath("result.upDownType").type(JsonFieldType.STRING).optional().description("상하행"),
                        fieldWithPath("result.realtime.generatedAt").type(JsonFieldType.STRING).description("실시간 생성 시각"),
                        fieldWithPath("result.realtime.dataSource").type(JsonFieldType.STRING).description("실시간 데이터 소스"),
                        fieldWithPath("result.realtime.isStale").type(JsonFieldType.BOOLEAN).description("실시간 stale 여부"),
                        fieldWithPath("result.realtime.freshnessSec").type(JsonFieldType.NUMBER).description("실시간 신선도(초)"),
                        fieldWithPath("result.realtime.confidenceLevel").type(JsonFieldType.STRING).description("실시간 신뢰도"),
                        fieldWithPath("result.realtime.etaSec").type(JsonFieldType.NUMBER).optional().description("대표 열차 ETA(초)"),
                        fieldWithPath("result.realtime.etaMinDisplay").type(JsonFieldType.NUMBER).optional().description("대표 열차 ETA(분 표기)"),
                        fieldWithPath("result.realtime.destinationStationDirection").type(JsonFieldType.STRING).optional().description("대표 열차 종착 방향"),
                        fieldWithPath("result.realtime.nextStationDirection").type(JsonFieldType.STRING).optional().description("대표 열차 다음역 방향"),
                        fieldWithPath("result.official.dataSource").type(JsonFieldType.STRING).description("공식 데이터 소스"),
                        fieldWithPath("result.official.eventCount").type(JsonFieldType.NUMBER).description("공식 공지 총 건수"),
                        fieldWithPath("result.official.activeEventCount").type(JsonFieldType.NUMBER).description("해제되지 않은 활성 공지 건수"),
                        fieldWithPath("result.official.incidents[]").type(JsonFieldType.ARRAY).description("공식 공지 목록"),
                        fieldWithPath("result.official.incidents[].eventId").type(JsonFieldType.STRING).description("이벤트 ID"),
                        fieldWithPath("result.official.incidents[].occurredAt").type(JsonFieldType.STRING).description("발생 시각"),
                        fieldWithPath("result.official.incidents[].resolvedAt").type(JsonFieldType.STRING).optional().description("해제 시각"),
                        fieldWithPath("result.official.incidents[].severity").type(JsonFieldType.STRING).description("심각도"),
                        fieldWithPath("result.official.incidents[].title").type(JsonFieldType.STRING).description("제목"),
                        fieldWithPath("result.official.incidents[].description").type(JsonFieldType.STRING).description("설명"),
                        fieldWithPath("result.official.incidents[].source").type(JsonFieldType.STRING).description("출처"),
                        fieldWithPath("result.official.incidents[].sourceUrl").type(JsonFieldType.STRING).optional().description("출처 URL"),
                        fieldWithPath("result.community.signalCount").type(JsonFieldType.NUMBER).description("커뮤니티 시그널 수"),
                        fieldWithPath("result.community.distinctAuthors").type(JsonFieldType.NUMBER).description("작성자 수"),
                        fieldWithPath("result.community.medianReportedDelayMin").type(JsonFieldType.NUMBER).optional().description("지연 분 중앙값"),
                        fieldWithPath("result.community.confidenceLevel").type(JsonFieldType.STRING).description("커뮤니티 신뢰도"),
                        fieldWithPath("result.community.signals[]").type(JsonFieldType.ARRAY).description("커뮤니티 시그널 샘플"),
                        fieldWithPath("result.community.signals[].postId").type(JsonFieldType.NUMBER).description("게시글 ID"),
                        fieldWithPath("result.community.signals[].createdAt").type(JsonFieldType.STRING).description("작성 시각"),
                        fieldWithPath("result.community.signals[].writer").type(JsonFieldType.STRING).description("작성자"),
                        fieldWithPath("result.community.signals[].matchedKeyword").type(JsonFieldType.STRING).description("매칭 키워드"),
                        fieldWithPath("result.community.signals[].reportedDelayMin").type(JsonFieldType.NUMBER).optional().description("추정 지연 분"),
                        fieldWithPath("result.community.signals[].snippet").type(JsonFieldType.STRING).description("본문 요약"),
                        fieldWithPath("result.recommendation.gradePreview").type(JsonFieldType.STRING).description("증빙 등급 preview"),
                        fieldWithPath("result.recommendation.confidenceLevel").type(JsonFieldType.STRING).description("추천 신뢰도"),
                        fieldWithPath("result.recommendation.estimatedDelayMin").type(JsonFieldType.NUMBER).description("예상 지연 분"),
                        fieldWithPath("result.recommendation.recommendedExpectedArrivalAt").type(JsonFieldType.STRING).description("추천 도착 예정 시각"),
                        fieldWithPath("result.recommendation.recommendedMessage").type(JsonFieldType.STRING).description("추천 증빙 문구"),
                    ),
                ),
            )
    }

    private fun buildCreateResponse(): DelayProofDto.CreateResponse {
        return DelayProofDto.CreateResponse(
            proofId = "dpv2_01abc",
            issuedAt = "2026-02-24T01:20:00Z",
            expiresAt = "2026-02-25T01:20:00Z",
            grade = "B",
            confidenceLevel = "MEDIUM",
            evidenceSummary = buildEvidenceSummary(),
            text = "지하철 지연으로 10:35 도착예정입니다.",
            shareUrl = "https://ahhachul.com/proofs/dpv2_01abc",
            signature = "c2lnbmF0dXJl",
        )
    }

    private fun buildEvidenceSummary(): DelayProofDto.DelayProofEvidenceSummary {
        return DelayProofDto.DelayProofEvidenceSummary(
            official = DelayProofDto.OfficialEvidence(
                matched = true,
                eventCount = 1,
                dataSource = "OFFICIAL_FEED",
                incidents = listOf(
                    DelayProofDto.OfficialIncident(
                        eventId = "incident-1",
                        occurredAt = "2026-02-24T01:00:00Z",
                        resolvedAt = null,
                        severity = "DELAY",
                        title = "2호선 지연",
                        description = "신호 이상으로 지연",
                        source = "SEOUL_METRO",
                        sourceUrl = "https://example.com/incident-1",
                    ),
                ),
            ),
            community = DelayProofDto.CommunityEvidence(
                signalCount = 18,
                distinctAuthors = 11,
                medianReportedDelayMin = 8,
                confidenceLevel = "HIGH",
                signals = listOf(
                    DelayProofDto.CommunityDelaySignal(
                        postId = 1001,
                        createdAt = "2026-02-24T01:10:00Z",
                        writer = "출근러1",
                        matchedKeyword = "지연",
                        reportedDelayMin = 10,
                        snippet = "2호선 지연 심하네요",
                    ),
                ),
            ),
            realtime = DelayProofDto.RealtimeEvidence(
                isStale = false,
                freshnessSec = 24,
                confidenceLevel = "MEDIUM",
                generatedAt = "2026-02-24T01:19:36Z",
            ),
        )
    }
}
