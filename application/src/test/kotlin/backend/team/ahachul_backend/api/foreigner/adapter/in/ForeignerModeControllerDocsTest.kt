package backend.team.ahachul_backend.api.foreigner.adapter.`in`

import backend.team.ahachul_backend.api.foreigner.adapter.`in`.dto.ForeignerModeDto
import backend.team.ahachul_backend.api.foreigner.adapter.`in`.dto.ForeignerStationSocialDto
import backend.team.ahachul_backend.api.foreigner.application.port.`in`.ForeignerModeUseCase
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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.restdocs.request.RequestDocumentation.queryParameters
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(ForeignerModeController::class)
class ForeignerModeControllerDocsTest : CommonDocsTestConfig() {

    @MockBean
    lateinit var foreignerModeUseCase: ForeignerModeUseCase

    @Test
    fun getStationGuide() {
        val response = ForeignerModeDto.StationGuideResponse(
            generatedAt = "2026-02-25T23:10:00+09:00",
            station = ForeignerModeDto.StationDescriptor(
                stationId = 557,
                subwayLineId = 18,
                nameKo = "안암",
                nameLocalized = "Anam Station",
                romanizedName = "Anam",
                pronunciation = "a-nam",
                subwayLineNameKo = "우이신설경전철",
                subwayLineNameLocalized = "Ui-Sinseol Light Rail",
                locale = "en",
            ),
            templates = ForeignerModeDto.TemplateBundle(
                complaintTitleTemplate = "[Ui-Sinseol Light Rail] Service issue at Anam Station",
                complaintBodyTemplate = "Hello, I would like to report an issue.",
                lostTitleTemplate = "Lost item report at Anam Station",
                lostBodyTemplate = "Hello, I am looking for a lost item.",
            ),
            cultureGuide = ForeignerModeDto.CultureGuide(
                lastTrainTip = "Try to arrive at the platform at least 15 minutes before the last train.",
                transferEtiquetteTip = "Keep to one side in transfer corridors and let passengers exit first.",
                safetyTip = "During crowding, wait behind the safety line and avoid forcing your way in.",
                emergencyPhrase = "In emergencies, contact station staff or call 112 immediately.",
            ),
            supportedLocales = listOf("ko", "en", "th", "cn"),
        )

        given(foreignerModeUseCase.getStationGuide(any())).willReturn(response)

        val result = mockMvc.perform(
            get("/v2/foreigner/stations/guide")
                .queryParam("stationId", "557")
                .queryParam("subwayLineId", "18")
                .queryParam("locale", "en")
                .accept(MediaType.APPLICATION_JSON),
        )

        result.andExpect(status().isOk)
            .andDo(
                document(
                    "get-foreigner-station-guide",
                    getDocsRequest(),
                    getDocsResponse(),
                    queryParameters(
                        parameterWithName("stationId").description("역 ID"),
                        parameterWithName("subwayLineId").description("지하철 노선 ID"),
                        parameterWithName("locale").optional().description("언어 코드(ko/en/th/cn), 미입력 시 en"),
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.generatedAt").type(JsonFieldType.STRING).description("응답 생성 시각"),
                        fieldWithPath("result.station.stationId").type(JsonFieldType.NUMBER).description("역 ID"),
                        fieldWithPath("result.station.subwayLineId").type(JsonFieldType.NUMBER).description("노선 ID"),
                        fieldWithPath("result.station.nameKo").type(JsonFieldType.STRING).description("역명(한국어)"),
                        fieldWithPath("result.station.nameLocalized").type(JsonFieldType.STRING).description("역명(선택 locale)"),
                        fieldWithPath("result.station.romanizedName").type(JsonFieldType.STRING).description("역명 로마자"),
                        fieldWithPath("result.station.pronunciation").type(JsonFieldType.STRING).description("발음 가이드"),
                        fieldWithPath("result.station.subwayLineNameKo").type(JsonFieldType.STRING).description("노선명(한국어)"),
                        fieldWithPath("result.station.subwayLineNameLocalized").type(JsonFieldType.STRING).description("노선명(선택 locale)"),
                        fieldWithPath("result.station.locale").type(JsonFieldType.STRING).description("반영 locale"),
                        fieldWithPath("result.templates.complaintTitleTemplate").type(JsonFieldType.STRING).description("민원 제목 템플릿"),
                        fieldWithPath("result.templates.complaintBodyTemplate").type(JsonFieldType.STRING).description("민원 본문 템플릿"),
                        fieldWithPath("result.templates.lostTitleTemplate").type(JsonFieldType.STRING).description("유실물 제목 템플릿"),
                        fieldWithPath("result.templates.lostBodyTemplate").type(JsonFieldType.STRING).description("유실물 본문 템플릿"),
                        fieldWithPath("result.cultureGuide.lastTrainTip").type(JsonFieldType.STRING).description("막차 안내"),
                        fieldWithPath("result.cultureGuide.transferEtiquetteTip").type(JsonFieldType.STRING).description("환승 예절 안내"),
                        fieldWithPath("result.cultureGuide.safetyTip").type(JsonFieldType.STRING).description("안전 안내"),
                        fieldWithPath("result.cultureGuide.emergencyPhrase").type(JsonFieldType.STRING).description("긴급 문구"),
                        fieldWithPath("result.supportedLocales").type(JsonFieldType.ARRAY).description("지원 locale 목록"),
                    ),
                ),
            )
    }

    @Test
    fun translateCommunityPost() {
        val response = ForeignerModeDto.CommunityPostTranslationResponse(
            postId = 101,
            sourceLocale = "ko",
            targetLocale = "en",
            originalTitle = "2호선 지연 안내",
            originalContent = "지하철이 10분 이상 지연되고 있습니다.",
            translatedTitle = "Line 2 delay notice",
            translatedContent = "The subway is delayed for more than 10 minutes.",
            isFallback = false,
            notice = "Auto-translation is for reference and may differ from the original meaning.",
        )

        given(foreignerModeUseCase.translateCommunityPost(any())).willReturn(response)

        val result = mockMvc.perform(
            get("/v2/foreigner/community-posts/{postId}/translation", 101)
                .queryParam("targetLocale", "en")
                .accept(MediaType.APPLICATION_JSON),
        )

        result.andExpect(status().isOk)
            .andDo(
                document(
                    "get-foreigner-community-post-translation",
                    getDocsRequest(),
                    getDocsResponse(),
                    pathParameters(
                        parameterWithName("postId").description("커뮤니티 게시글 ID"),
                    ),
                    queryParameters(
                        parameterWithName("targetLocale").optional().description("번역 대상 locale(ko/en/th/cn), 미입력 시 en"),
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.postId").type(JsonFieldType.NUMBER).description("게시글 ID"),
                        fieldWithPath("result.sourceLocale").type(JsonFieldType.STRING).description("원문 언어 추정"),
                        fieldWithPath("result.targetLocale").type(JsonFieldType.STRING).description("번역 대상 언어"),
                        fieldWithPath("result.originalTitle").type(JsonFieldType.STRING).description("원문 제목"),
                        fieldWithPath("result.originalContent").type(JsonFieldType.STRING).description("원문 본문"),
                        fieldWithPath("result.translatedTitle").type(JsonFieldType.STRING).description("번역 제목"),
                        fieldWithPath("result.translatedContent").type(JsonFieldType.STRING).description("번역 본문"),
                        fieldWithPath("result.isFallback").type(JsonFieldType.BOOLEAN).description("fallback 번역 여부"),
                        fieldWithPath("result.notice").type(JsonFieldType.STRING).description("번역 안내 문구"),
                    ),
                ),
            )
    }

    @Test
    fun getStationSocialHotspots() {
        val response = ForeignerStationSocialDto.HotspotsResponse(
            generatedAt = "2026-02-25T23:20:00+09:00",
            locale = "en",
            hotspots = listOf(
                ForeignerStationSocialDto.HotspotStation(
                    stationId = 1301,
                    subwayLineId = 2,
                    stationNameKo = "홍대입구",
                    stationNameLocalized = "Hongdaeipgu Station",
                    lineNameLocalized = "Line 2",
                    romanizedName = "Hongdaeipgu",
                    districtLabel = "Hongdae culture and nightlife",
                    summary = "Youth-driven area known for street performances and nightlife",
                    contentTags = listOf("Nightlife", "Street 공연"),
                    upcomingMeetupCount = 12,
                    reviewCount = 3,
                ),
            ),
        )
        given(foreignerModeUseCase.getStationSocialHotspots(any())).willReturn(response)

        val result = mockMvc.perform(
            get("/v2/foreigner/station-social/hotspots")
                .queryParam("locale", "en")
                .accept(MediaType.APPLICATION_JSON),
        )

        result.andExpect(status().isOk)
            .andDo(
                document(
                    "get-foreigner-station-social-hotspots",
                    getDocsRequest(),
                    getDocsResponse(),
                    queryParameters(
                        parameterWithName("locale").optional().description("언어 코드(ko/en/th/cn), 미입력 시 en"),
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.generatedAt").type(JsonFieldType.STRING).description("응답 생성 시각"),
                        fieldWithPath("result.locale").type(JsonFieldType.STRING).description("반영 locale"),
                        fieldWithPath("result.hotspots").type(JsonFieldType.ARRAY).description("핫스팟 역 목록"),
                        fieldWithPath("result.hotspots[].stationId").type(JsonFieldType.NUMBER).description("역 ID"),
                        fieldWithPath("result.hotspots[].subwayLineId").type(JsonFieldType.NUMBER).description("대표 노선 ID"),
                        fieldWithPath("result.hotspots[].stationNameKo").type(JsonFieldType.STRING).description("역명(한국어)"),
                        fieldWithPath("result.hotspots[].stationNameLocalized").type(JsonFieldType.STRING).description("역명(현지화)"),
                        fieldWithPath("result.hotspots[].lineNameLocalized").type(JsonFieldType.STRING).description("노선명(현지화)"),
                        fieldWithPath("result.hotspots[].romanizedName").type(JsonFieldType.STRING).description("로마자 표기"),
                        fieldWithPath("result.hotspots[].districtLabel").type(JsonFieldType.STRING).description("권역 라벨"),
                        fieldWithPath("result.hotspots[].summary").type(JsonFieldType.STRING).description("핵심 요약"),
                        fieldWithPath("result.hotspots[].contentTags").type(JsonFieldType.ARRAY).description("추천 태그"),
                        fieldWithPath("result.hotspots[].upcomingMeetupCount").type(JsonFieldType.NUMBER).description("예정 모임 수"),
                        fieldWithPath("result.hotspots[].reviewCount").type(JsonFieldType.NUMBER).description("리뷰 수"),
                    ),
                ),
            )
    }

    @Test
    fun getStationSocialOverview() {
        val response = ForeignerStationSocialDto.OverviewResponse(
            generatedAt = "2026-02-25T23:21:00+09:00",
            locale = "en",
            station = ForeignerStationSocialDto.StationInfo(
                stationId = 1301,
                subwayLineId = 2,
                stationNameKo = "홍대입구",
                stationNameLocalized = "Hongdaeipgu Station",
                lineNameKo = "2호선",
                lineNameLocalized = "Line 2",
                romanizedName = "Hongdaeipgu",
                pronunciation = "hong-dae-ip-gu",
                cultureTips = listOf("Tip A", "Tip B"),
            ),
            sameNationalityOnly = false,
            nationalityCode = null,
            calendar = listOf(ForeignerStationSocialDto.CalendarItem(date = "2026-02-26", meetupCount = 5)),
            meetups = listOf(
                ForeignerStationSocialDto.MeetupItem(
                    meetupId = 99,
                    title = "명동 같이 가요",
                    description = "올리브영, 약국 같이 둘러봐요",
                    meetupAt = "2026-02-26 19:00:00",
                    maxParticipants = 200,
                    acceptedCount = 57,
                    hostMemberId = 10,
                    hostNickname = "traveler10",
                    nationalityCode = "CN",
                    sameNationalityOnly = false,
                    status = "OPEN",
                    mine = false,
                    participants = listOf(
                        ForeignerStationSocialDto.ParticipantItem(
                            participantId = 1001,
                            memberId = 11,
                            nickname = "alice",
                            nationalityCode = "CN",
                            status = "ACCEPTED",
                            mine = false,
                        ),
                    ),
                ),
            ),
            reviewPosts = listOf(
                ForeignerStationSocialDto.ReviewPostItem(
                    postId = 501,
                    title = "홍대 야간 추천",
                    preview = "밤 9시 이후가 한산했어요.",
                    writer = "tester",
                    createdAt = "2026-02-25 10:00:00",
                ),
            ),
        )
        given(foreignerModeUseCase.getStationSocialOverview(any())).willReturn(response)

        val result = mockMvc.perform(
            get("/v2/foreigner/station-social/overview")
                .queryParam("stationId", "1301")
                .queryParam("subwayLineId", "2")
                .queryParam("locale", "en")
                .queryParam("sameNationalityOnly", "false")
                .queryParam("limit", "20")
                .accept(MediaType.APPLICATION_JSON),
        )

        result.andExpect(status().isOk)
            .andDo(
                document(
                    "get-foreigner-station-social-overview",
                    getDocsRequest(),
                    getDocsResponse(),
                    queryParameters(
                        parameterWithName("stationId").description("역 ID"),
                        parameterWithName("subwayLineId").optional().description("노선 ID"),
                        parameterWithName("locale").optional().description("언어 코드(ko/en/th/cn), 미입력 시 en"),
                        parameterWithName("sameNationalityOnly").optional().description("같은 국적만 보기"),
                        parameterWithName("nationalityCode").optional().description("조회자 국적 코드(예: KR/CN/JP)"),
                        parameterWithName("limit").optional().description("모임 노출 최대 개수(기본 20, 최대 100)"),
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.generatedAt").type(JsonFieldType.STRING).description("응답 생성 시각"),
                        fieldWithPath("result.locale").type(JsonFieldType.STRING).description("반영 locale"),
                        fieldWithPath("result.station.stationId").type(JsonFieldType.NUMBER).description("역 ID"),
                        fieldWithPath("result.station.subwayLineId").type(JsonFieldType.NUMBER).description("노선 ID"),
                        fieldWithPath("result.station.stationNameKo").type(JsonFieldType.STRING).description("역명(한국어)"),
                        fieldWithPath("result.station.stationNameLocalized").type(JsonFieldType.STRING).description("역명(현지화)"),
                        fieldWithPath("result.station.lineNameKo").type(JsonFieldType.STRING).description("노선명(한국어)"),
                        fieldWithPath("result.station.lineNameLocalized").type(JsonFieldType.STRING).description("노선명(현지화)"),
                        fieldWithPath("result.station.romanizedName").type(JsonFieldType.STRING).description("로마자"),
                        fieldWithPath("result.station.pronunciation").type(JsonFieldType.STRING).description("발음 가이드"),
                        fieldWithPath("result.station.cultureTips").type(JsonFieldType.ARRAY).description("문화 안내"),
                        fieldWithPath("result.sameNationalityOnly").type(JsonFieldType.BOOLEAN).description("국적 필터 ON 여부"),
                        fieldWithPath("result.nationalityCode").type(JsonFieldType.STRING).description("조회 기준 국적").optional(),
                        fieldWithPath("result.calendar").type(JsonFieldType.ARRAY).description("날짜별 모임 수"),
                        fieldWithPath("result.calendar[].date").type(JsonFieldType.STRING).description("날짜(yyyy-MM-dd)"),
                        fieldWithPath("result.calendar[].meetupCount").type(JsonFieldType.NUMBER).description("해당 날짜 모임 수"),
                        fieldWithPath("result.meetups").type(JsonFieldType.ARRAY).description("모임 목록"),
                        fieldWithPath("result.meetups[].meetupId").type(JsonFieldType.NUMBER).description("모임 ID"),
                        fieldWithPath("result.meetups[].title").type(JsonFieldType.STRING).description("제목"),
                        fieldWithPath("result.meetups[].description").type(JsonFieldType.STRING).description("설명"),
                        fieldWithPath("result.meetups[].meetupAt").type(JsonFieldType.STRING).description("모임 일시"),
                        fieldWithPath("result.meetups[].maxParticipants").type(JsonFieldType.NUMBER).description("최대 인원"),
                        fieldWithPath("result.meetups[].acceptedCount").type(JsonFieldType.NUMBER).description("승인 인원"),
                        fieldWithPath("result.meetups[].hostMemberId").type(JsonFieldType.NUMBER).description("호스트 멤버 ID"),
                        fieldWithPath("result.meetups[].hostNickname").type(JsonFieldType.STRING).description("호스트 닉네임"),
                        fieldWithPath("result.meetups[].nationalityCode").type(JsonFieldType.STRING).description("모임 국적 코드").optional(),
                        fieldWithPath("result.meetups[].sameNationalityOnly").type(JsonFieldType.BOOLEAN).description("같은 국적 제한 여부"),
                        fieldWithPath("result.meetups[].status").type(JsonFieldType.STRING).description("모임 상태"),
                        fieldWithPath("result.meetups[].mine").type(JsonFieldType.BOOLEAN).description("내가 주최자인지 여부"),
                        fieldWithPath("result.meetups[].participants").type(JsonFieldType.ARRAY).description("참여자 목록"),
                        fieldWithPath("result.meetups[].participants[].participantId").type(JsonFieldType.NUMBER).description("참여자 엔트리 ID"),
                        fieldWithPath("result.meetups[].participants[].memberId").type(JsonFieldType.NUMBER).description("멤버 ID"),
                        fieldWithPath("result.meetups[].participants[].nickname").type(JsonFieldType.STRING).description("닉네임"),
                        fieldWithPath("result.meetups[].participants[].nationalityCode").type(JsonFieldType.STRING).description("국적 코드").optional(),
                        fieldWithPath("result.meetups[].participants[].status").type(JsonFieldType.STRING).description("참여 상태"),
                        fieldWithPath("result.meetups[].participants[].mine").type(JsonFieldType.BOOLEAN).description("내 참여 여부"),
                        fieldWithPath("result.reviewPosts").type(JsonFieldType.ARRAY).description("역 리뷰 게시글 요약"),
                        fieldWithPath("result.reviewPosts[].postId").type(JsonFieldType.NUMBER).description("게시글 ID"),
                        fieldWithPath("result.reviewPosts[].title").type(JsonFieldType.STRING).description("게시글 제목"),
                        fieldWithPath("result.reviewPosts[].preview").type(JsonFieldType.STRING).description("미리보기"),
                        fieldWithPath("result.reviewPosts[].writer").type(JsonFieldType.STRING).description("작성자"),
                        fieldWithPath("result.reviewPosts[].createdAt").type(JsonFieldType.STRING).description("작성 시각"),
                    ),
                ),
            )
    }

    @Test
    fun createStationSocialMeetup() {
        val request = ForeignerStationSocialDto.CreateMeetupRequest(
            stationId = 1301,
            subwayLineId = 2,
            title = "명동 같이 가요",
            description = "화장품/약국 코스 같이 가요",
            meetupAt = "2026-02-26T19:00:00",
            maxParticipants = 200,
            nationalityCode = "CN",
            sameNationalityOnly = false,
        )
        val response = ForeignerStationSocialDto.CreateMeetupResponse(
            meetupId = 777,
            createdAt = "2026-02-25 22:00:00",
        )
        given(foreignerModeUseCase.createStationSocialMeetup(any())).willReturn(response)

        val result = mockMvc.perform(
            post("/v2/foreigner/station-social/meetups")
                .header("Authorization", "Bearer <Access Token>")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON),
        )

        result.andExpect(status().isOk)
            .andDo(
                document(
                    "create-foreigner-station-social-meetup",
                    getDocsRequest(),
                    getDocsResponse(),
                    requestHeaders(
                        headerWithName("Authorization").description("엑세스 토큰"),
                    ),
                    requestFields(
                        fieldWithPath("stationId").type(JsonFieldType.NUMBER).description("역 ID"),
                        fieldWithPath("subwayLineId").type(JsonFieldType.NUMBER).description("노선 ID"),
                        fieldWithPath("title").type(JsonFieldType.STRING).description("모임 제목"),
                        fieldWithPath("description").type(JsonFieldType.STRING).description("모임 설명"),
                        fieldWithPath("meetupAt").type(JsonFieldType.STRING).description("모임 일시(ISO LocalDateTime)"),
                        fieldWithPath("maxParticipants").type(JsonFieldType.NUMBER).description("최대 인원(2~500)"),
                        fieldWithPath("nationalityCode").type(JsonFieldType.STRING).description("모임 대표 국적 코드").optional(),
                        fieldWithPath("sameNationalityOnly").type(JsonFieldType.BOOLEAN).description("같은 국적 제한 여부"),
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.meetupId").type(JsonFieldType.NUMBER).description("생성된 모임 ID"),
                        fieldWithPath("result.createdAt").type(JsonFieldType.STRING).description("생성 시각"),
                    ),
                ),
            )
    }

    @Test
    fun joinStationSocialMeetup() {
        val request = ForeignerStationSocialDto.JoinMeetupRequest(
            introductionMessage = "중국에서 왔어요. 같이 가요!",
            nationalityCode = "CN",
        )
        val response = ForeignerStationSocialDto.JoinMeetupResponse(
            meetupId = 777,
            participantId = 9001,
            status = "REQUESTED",
        )
        given(foreignerModeUseCase.joinStationSocialMeetup(any())).willReturn(response)

        val result = mockMvc.perform(
            post("/v2/foreigner/station-social/meetups/{meetupId}/join", 777)
                .header("Authorization", "Bearer <Access Token>")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON),
        )

        result.andExpect(status().isOk)
            .andDo(
                document(
                    "join-foreigner-station-social-meetup",
                    getDocsRequest(),
                    getDocsResponse(),
                    requestHeaders(
                        headerWithName("Authorization").description("엑세스 토큰"),
                    ),
                    pathParameters(
                        parameterWithName("meetupId").description("모임 ID"),
                    ),
                    requestFields(
                        fieldWithPath("introductionMessage").type(JsonFieldType.STRING).description("참여 소개문").optional(),
                        fieldWithPath("nationalityCode").type(JsonFieldType.STRING).description("참여자 국적 코드").optional(),
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.meetupId").type(JsonFieldType.NUMBER).description("모임 ID"),
                        fieldWithPath("result.participantId").type(JsonFieldType.NUMBER).description("참여 엔트리 ID"),
                        fieldWithPath("result.status").type(JsonFieldType.STRING).description("참여 상태"),
                    ),
                ),
            )
    }

    @Test
    fun reviewStationSocialParticipant() {
        val request = ForeignerStationSocialDto.ReviewParticipantRequest(approve = true)
        val response = ForeignerStationSocialDto.ReviewParticipantResponse(
            meetupId = 777,
            participantId = 9001,
            status = "ACCEPTED",
        )
        given(foreignerModeUseCase.reviewStationSocialParticipant(any())).willReturn(response)

        val result = mockMvc.perform(
            patch("/v2/foreigner/station-social/meetups/{meetupId}/participants/{participantId}", 777, 9001)
                .header("Authorization", "Bearer <Access Token>")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON),
        )

        result.andExpect(status().isOk)
            .andDo(
                document(
                    "review-foreigner-station-social-participant",
                    getDocsRequest(),
                    getDocsResponse(),
                    requestHeaders(
                        headerWithName("Authorization").description("엑세스 토큰"),
                    ),
                    pathParameters(
                        parameterWithName("meetupId").description("모임 ID"),
                        parameterWithName("participantId").description("참여 엔트리 ID"),
                    ),
                    requestFields(
                        fieldWithPath("approve").type(JsonFieldType.BOOLEAN).description("승인(true) 또는 거절(false)"),
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.meetupId").type(JsonFieldType.NUMBER).description("모임 ID"),
                        fieldWithPath("result.participantId").type(JsonFieldType.NUMBER).description("참여 엔트리 ID"),
                        fieldWithPath("result.status").type(JsonFieldType.STRING).description("변경된 상태"),
                    ),
                ),
            )
    }

    @Test
    fun openStationSocialMatch() {
        val request = ForeignerStationSocialDto.OpenMatchRequest(
            targetMemberId = 22,
            openingMessage = "같이 명동 가요!",
        )
        val response = ForeignerStationSocialDto.OpenMatchResponse(
            meetupId = 777,
            targetMemberId = 22,
            roomId = 30,
            messageId = 1003,
        )
        given(foreignerModeUseCase.openStationSocialMatch(any())).willReturn(response)

        val result = mockMvc.perform(
            post("/v2/foreigner/station-social/meetups/{meetupId}/match", 777)
                .header("Authorization", "Bearer <Access Token>")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON),
        )

        result.andExpect(status().isOk)
            .andDo(
                document(
                    "open-foreigner-station-social-match",
                    getDocsRequest(),
                    getDocsResponse(),
                    requestHeaders(
                        headerWithName("Authorization").description("엑세스 토큰"),
                    ),
                    pathParameters(
                        parameterWithName("meetupId").description("모임 ID"),
                    ),
                    requestFields(
                        fieldWithPath("targetMemberId").type(JsonFieldType.NUMBER).description("매칭 대상 멤버 ID"),
                        fieldWithPath("openingMessage").type(JsonFieldType.STRING).description("첫 인사 메시지").optional(),
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.meetupId").type(JsonFieldType.NUMBER).description("모임 ID"),
                        fieldWithPath("result.targetMemberId").type(JsonFieldType.NUMBER).description("대상 멤버 ID"),
                        fieldWithPath("result.roomId").type(JsonFieldType.NUMBER).description("쪽지방 ID"),
                        fieldWithPath("result.messageId").type(JsonFieldType.NUMBER).description("생성된 첫 메시지 ID"),
                    ),
                ),
            )
    }
}
