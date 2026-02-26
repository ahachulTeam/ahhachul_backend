package backend.team.ahachul_backend.api.member.adapter.web.`in`

import backend.team.ahachul_backend.api.member.adapter.web.`in`.dto.*
import backend.team.ahachul_backend.api.member.adapter.web.`in`.dto.BookmarkStationDto.BookmarkStation
import backend.team.ahachul_backend.api.member.application.port.`in`.MemberUseCase
import backend.team.ahachul_backend.api.member.domain.model.GenderType
import backend.team.ahachul_backend.config.controller.CommonDocsTestConfig
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito
import org.mockito.BDDMockito.given
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.queryParameters
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(MemberController::class)
class MemberControllerDocsTest : CommonDocsTestConfig() {

    @MockBean
    lateinit var memberUseCase: MemberUseCase

    @Test
    fun getMemberTest() {
        // given
        val response = GetMemberDto.Response(
            memberId = 1,
            nickname = "nickname",
            email = "email",
            maskedEmail = "em***@mail.com",
            gender = GenderType.MALE,
            ageRange = "20",
            profilePublic = true,
            emailPublic = false,
            genderAgePublic = false,
            postsPublic = true,
            commentsPublic = true,
        )

        given(memberUseCase.getMember())
            .willReturn(response)

        // when
        val result = mockMvc.perform(
            get("/v1/members")
                .header("Authorization", "Bearer <Access Token>")
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
            .andDo(
                document(
                    "get-member",
                    getDocsRequest(),
                    getDocsResponse(),
                    requestHeaders(
                        headerWithName("Authorization").description("엑세스 토큰")
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.memberId").type(JsonFieldType.NUMBER).description("사용자 Identification Key"),
                        fieldWithPath("result.nickname").type(JsonFieldType.STRING).description("사용자 닉네임").optional(),
                        fieldWithPath("result.email").type(JsonFieldType.STRING).description("사용자 이메일").optional(),
                        fieldWithPath("result.maskedEmail").type(JsonFieldType.STRING).description("마스킹된 사용자 이메일").optional(),
                        fieldWithPath("result.gender").type("GenderType").description("사용자 성별").attributes(getFormatAttribute("MALE, FEMALE")).optional(),
                        fieldWithPath("result.ageRange").type(JsonFieldType.STRING).description("사용자 연령대").attributes(getFormatAttribute("1 : 1세 이상 10세 미만 ${getNewLine()} 10 : 10세 이상 20세 미만 ${getNewLine()} 20 : 20세 이상 30세 미만 ${getNewLine()} ...")).optional(),
                        fieldWithPath("result.profilePublic").type(JsonFieldType.BOOLEAN).description("프로필 전체 공개 여부"),
                        fieldWithPath("result.emailPublic").type(JsonFieldType.BOOLEAN).description("이메일 공개 여부"),
                        fieldWithPath("result.genderAgePublic").type(JsonFieldType.BOOLEAN).description("성별/연령대 공개 여부"),
                        fieldWithPath("result.postsPublic").type(JsonFieldType.BOOLEAN).description("작성 글 공개 여부"),
                        fieldWithPath("result.commentsPublic").type(JsonFieldType.BOOLEAN).description("작성 댓글 공개 여부"),
                    )
                )
            )
    }

    @Test
    fun updateMemberTest() {
        // given
        val response = UpdateMemberDto.Response(
            nickname = "nickname",
            gender = GenderType.MALE,
            ageRange = "20",
            profilePublic = true,
            emailPublic = false,
            genderAgePublic = false,
            postsPublic = true,
            commentsPublic = true,
        )

        given(memberUseCase.updateMember(any()))
            .willReturn(response)

        val request = UpdateMemberDto.Request(
            nickname = "nickname",
            gender = GenderType.MALE,
            ageRange = "20",
            profilePublic = true,
            emailPublic = false,
            genderAgePublic = false,
            postsPublic = true,
            commentsPublic = true,
        )

        // when
        val result = mockMvc.perform(
            patch("/v1/members")
                .header("Authorization", "Bearer <Access Token>")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON)
        )


        // then
        result.andExpect(status().isOk)
            .andDo(
                document(
                    "update-member",
                    getDocsRequest(),
                    getDocsResponse(),
                    requestHeaders(
                        headerWithName("Authorization").description("엑세스 토큰")
                    ),
                    requestFields(
                        fieldWithPath("nickname").type(JsonFieldType.STRING).description("사용자 닉네임").optional(),
                        fieldWithPath("gender").type("GenderType").description("사용자 성별").attributes(getFormatAttribute("MALE, FEMALE")).optional(),
                        fieldWithPath("ageRange").type(JsonFieldType.STRING).description("사용자 연령대").attributes(getFormatAttribute("1 : 1세 이상 10세 미만 ${getNewLine()} 10 : 10세 이상 20세 미만 ${getNewLine()} 20 : 20세 이상 30세 미만 ${getNewLine()} ...")).optional(),
                        fieldWithPath("profilePublic").type(JsonFieldType.BOOLEAN).description("프로필 전체 공개 여부").optional(),
                        fieldWithPath("emailPublic").type(JsonFieldType.BOOLEAN).description("이메일 공개 여부").optional(),
                        fieldWithPath("genderAgePublic").type(JsonFieldType.BOOLEAN).description("성별/연령대 공개 여부").optional(),
                        fieldWithPath("postsPublic").type(JsonFieldType.BOOLEAN).description("작성 글 공개 여부").optional(),
                        fieldWithPath("commentsPublic").type(JsonFieldType.BOOLEAN).description("작성 댓글 공개 여부").optional(),

                        ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.nickname").type(JsonFieldType.STRING).description("사용자 닉네임").optional(),
                        fieldWithPath("result.gender").type("GenderType").description("사용자 성별").attributes(getFormatAttribute("MALE, FEMALE")).optional(),
                        fieldWithPath("result.ageRange").type(JsonFieldType.STRING).description("사용자 연령대").attributes(getFormatAttribute("1 : 1세 이상 10세 미만 ${getNewLine()} 10 : 10세 이상 20세 미만 ${getNewLine()} 20 : 20세 이상 30세 미만 ${getNewLine()} ...")).optional(),
                        fieldWithPath("result.profilePublic").type(JsonFieldType.BOOLEAN).description("프로필 전체 공개 여부"),
                        fieldWithPath("result.emailPublic").type(JsonFieldType.BOOLEAN).description("이메일 공개 여부"),
                        fieldWithPath("result.genderAgePublic").type(JsonFieldType.BOOLEAN).description("성별/연령대 공개 여부"),
                        fieldWithPath("result.postsPublic").type(JsonFieldType.BOOLEAN).description("작성 글 공개 여부"),
                        fieldWithPath("result.commentsPublic").type(JsonFieldType.BOOLEAN).description("작성 댓글 공개 여부"),
                    )
                )
            )
    }

    @Test
    fun deleteMemberTest() {
        // when
        val result = mockMvc.perform(
            delete("/v1/members")
                .header("Authorization", "Bearer <Access Token>")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
            .andDo(
                document(
                    "delete-member",
                    getDocsRequest(),
                    getDocsResponse(),
                    requestHeaders(
                        headerWithName("Authorization").description("엑세스 토큰")
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result").optional().description("X")
                    )
                )
            )
    }

    @Test
    fun checkNicknameTest() {
        // given
        val response = CheckNicknameDto.Response(
            available = true
        )

        given(memberUseCase.checkNickname(any()))
            .willReturn(response)

        val request = CheckNicknameDto.Request(
            nickname = "nickname"
        )

        // when
        val result = mockMvc.perform(
            post("/v1/members/check-nickname")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
            .andDo(
                document(
                    "check-nickname",
                    getDocsRequest(),
                    getDocsResponse(),
                    requestFields(
                        fieldWithPath("nickname").type(JsonFieldType.STRING).description("사용자 닉네임"),
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.available").type(JsonFieldType.BOOLEAN).description("닉네임 사용 가능 여부"),
                    )
                )
            )
    }

    @Test
    fun bookmarkStationTest() {
        // given
        val response = GetBookmarkStationDto.Response(
            stationInfoList = listOf(
                GetBookmarkStationDto.StationInfo(
                    stationId = 1L,
                    stationName = "발산역",
                    label = "집",
                    subwayLineInfoList = listOf(
                        GetBookmarkStationDto.SubwayLineInfo(
                            subwayLineId = 1L,
                            subwayLineName = "1호선"
                        )
                    )
                ),
                GetBookmarkStationDto.StationInfo(
                    stationId = 2L,
                    stationName = "우장산역",
                    label = "학교",
                    subwayLineInfoList = listOf(
                        GetBookmarkStationDto.SubwayLineInfo(
                            subwayLineId = 5L,
                            subwayLineName = "5호선"
                        )
                    )
                ),
                GetBookmarkStationDto.StationInfo(
                    stationId = 3L,
                    stationName = "화곡역",
                    label = "즐겨찾는 장소",
                    subwayLineInfoList = listOf(
                        GetBookmarkStationDto.SubwayLineInfo(
                            subwayLineId = 1L,
                            subwayLineName = "1호선"
                        )
                    )
                )
            )
        )

        given(memberUseCase.bookmarkStation(any()))
                .willReturn(response)

        val request = BookmarkStationDto.Request(listOf(
            BookmarkStation("발산역", "집"),
            BookmarkStation("우장산역", "학교"),
            BookmarkStation("화곡역", "즐겨찾는 장소"),
        ))

        // when
        val result = mockMvc.perform(
                post("/v1/members/bookmarks/stations")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
            .andDo(
                document(
                    "bookmark-station",
                    getDocsRequest(),
                    getDocsResponse(),
                    requestFields(
                        fieldWithPath("stations").type(JsonFieldType.ARRAY).description("즐겨찾는 역 이름 및 별명 리스트"),
                        fieldWithPath("stations[].stationName").type(JsonFieldType.STRING).description("즐겨찾는 역 이름"),
                        fieldWithPath("stations[].label").type(JsonFieldType.STRING).description("즐겨찾는 역 별명").optional(),
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.stationInfoList").type(JsonFieldType.ARRAY).description("즐겨찾기 한 역 정보 리스트"),
                        fieldWithPath("result.stationInfoList[].stationId").type(JsonFieldType.NUMBER).description("역 고유 ID"),
                        fieldWithPath("result.stationInfoList[].stationName").type(JsonFieldType.STRING).description("역 이름"),
                        fieldWithPath("result.stationInfoList[].label").type(JsonFieldType.STRING).description("역 별명").optional(),
                        fieldWithPath("result.stationInfoList[].subwayLineInfoList").type(JsonFieldType.ARRAY).description("해당 역이 존재하는 노선 리스트"),
                        fieldWithPath("result.stationInfoList[].subwayLineInfoList[].subwayLineId").type(JsonFieldType.NUMBER).description("노선 고유 ID"),
                        fieldWithPath("result.stationInfoList[].subwayLineInfoList[].subwayLineName").type(JsonFieldType.STRING).description("노선 이름"),
                    )
                )
            )
    }

    @Test
    fun getBookmarkStationTest() {
        // given
        val response = GetBookmarkStationDto.Response(
            stationInfoList = listOf(
                GetBookmarkStationDto.StationInfo(
                    stationId = 1L,
                    stationName = "시청역",
                    label = "집",
                    subwayLineInfoList = listOf(
                        GetBookmarkStationDto.SubwayLineInfo(
                            subwayLineId = 1L,
                            subwayLineName = "1호선"
                        )
                    )

                )
            )
        )

        given(memberUseCase.getBookmarkStation()).willReturn(response)

        // when
        val result = mockMvc.perform(
                get("/v1/members/bookmarks/stations")
                    .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
            .andDo(
                document(
                    "get-bookmark-station",
                    getDocsRequest(),
                    getDocsResponse(),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.stationInfoList").type(JsonFieldType.ARRAY).description("즐겨찾기 한 역 정보 리스트"),
                        fieldWithPath("result.stationInfoList[].stationId").type(JsonFieldType.NUMBER).description("역 고유 ID"),
                        fieldWithPath("result.stationInfoList[].stationName").type(JsonFieldType.STRING).description("역 이름"),
                        fieldWithPath("result.stationInfoList[].label").type(JsonFieldType.STRING).description("역 별명").optional(),
                        fieldWithPath("result.stationInfoList[].subwayLineInfoList").type(JsonFieldType.ARRAY).description("해당 역이 존재하는 노선 리스트"),
                        fieldWithPath("result.stationInfoList[].subwayLineInfoList[].subwayLineId").type(JsonFieldType.NUMBER).description("노선 고유 ID"),
                        fieldWithPath("result.stationInfoList[].subwayLineInfoList[].subwayLineName").type(JsonFieldType.STRING).description("노선 이름"),
                    )
                )
            )
    }

    @Test
    fun getFavoriteRouteRecommendationsTest() {
        // given
        val response = FavoriteRouteDto.GraphResponse(
            routes = listOf(
                FavoriteRouteDto.Route(
                    routeId = null,
                    routeType = FavoriteRouteDto.RouteType.RECOMMENDED,
                    title = null,
                    sourceStationId = 1L,
                    sourceStationName = "안암",
                    destinationStationId = 2L,
                    destinationStationName = "성수",
                    nodes = listOf(
                        FavoriteRouteDto.Node(1L, "안암", 0, true),
                        FavoriteRouteDto.Node(2L, "성수", 1, true),
                    ),
                    edges = listOf(
                        FavoriteRouteDto.Edge(1L, 2L, 6L, "6호선"),
                    ),
                    summary = FavoriteRouteDto.Summary(
                        totalStops = 1,
                        transferCount = 0,
                        estimatedMinutes = 2,
                    ),
                )
            )
        )

        given(memberUseCase.getFavoriteRouteRecommendations(3)).willReturn(response)

        // when
        val result = mockMvc.perform(
            get("/v2/members/bookmarks/routes/recommendations")
                .queryParam("limit", "3")
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
            .andDo(
                document(
                    "get-favorite-route-recommendations",
                    getDocsRequest(),
                    getDocsResponse(),
                    queryParameters(
                        parameterWithName("limit").description("추천 경로 최대 개수").optional(),
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.routes").type(JsonFieldType.ARRAY).description("추천 경로 리스트"),
                        fieldWithPath("result.routes[].routeId").type(JsonFieldType.NULL).description("추천 경로는 routeId 없음").optional(),
                        fieldWithPath("result.routes[].routeType").type(JsonFieldType.STRING).description("경로 타입"),
                        fieldWithPath("result.routes[].title").type(JsonFieldType.NULL).description("경로 제목(추천 경로는 없음)").optional(),
                        fieldWithPath("result.routes[].sourceStationId").type(JsonFieldType.NUMBER).description("출발역 ID"),
                        fieldWithPath("result.routes[].sourceStationName").type(JsonFieldType.STRING).description("출발역 이름"),
                        fieldWithPath("result.routes[].destinationStationId").type(JsonFieldType.NUMBER).description("도착역 ID"),
                        fieldWithPath("result.routes[].destinationStationName").type(JsonFieldType.STRING).description("도착역 이름"),
                        fieldWithPath("result.routes[].nodes").type(JsonFieldType.ARRAY).description("그래프 노드"),
                        fieldWithPath("result.routes[].nodes[].stationId").type(JsonFieldType.NUMBER).description("노드 역 ID"),
                        fieldWithPath("result.routes[].nodes[].stationName").type(JsonFieldType.STRING).description("노드 역 이름"),
                        fieldWithPath("result.routes[].nodes[].order").type(JsonFieldType.NUMBER).description("노드 순서"),
                        fieldWithPath("result.routes[].nodes[].favorite").type(JsonFieldType.BOOLEAN).description("즐겨찾기 포함 여부"),
                        fieldWithPath("result.routes[].edges").type(JsonFieldType.ARRAY).description("그래프 엣지"),
                        fieldWithPath("result.routes[].edges[].fromStationId").type(JsonFieldType.NUMBER).description("엣지 출발역 ID"),
                        fieldWithPath("result.routes[].edges[].toStationId").type(JsonFieldType.NUMBER).description("엣지 도착역 ID"),
                        fieldWithPath("result.routes[].edges[].subwayLineId").type(JsonFieldType.NUMBER).description("엣지 노선 ID"),
                        fieldWithPath("result.routes[].edges[].subwayLineName").type(JsonFieldType.STRING).description("엣지 노선 이름"),
                        fieldWithPath("result.routes[].summary.totalStops").type(JsonFieldType.NUMBER).description("정거장 수"),
                        fieldWithPath("result.routes[].summary.transferCount").type(JsonFieldType.NUMBER).description("환승 횟수"),
                        fieldWithPath("result.routes[].summary.estimatedMinutes").type(JsonFieldType.NUMBER).description("예상 소요(분)"),
                    )
                )
            )
    }

    @Test
    fun createFavoriteRouteTest() {
        // given
        val request = FavoriteRouteDto.CreateRequest(
            sourceStationId = 1L,
            destinationStationId = 2L,
            title = "출근 루트",
        )

        val response = FavoriteRouteDto.Route(
            routeId = 100L,
            routeType = FavoriteRouteDto.RouteType.CUSTOM,
            title = "출근 루트",
            sourceStationId = 1L,
            sourceStationName = "안암",
            destinationStationId = 2L,
            destinationStationName = "성수",
            nodes = listOf(
                FavoriteRouteDto.Node(1L, "안암", 0, true),
                FavoriteRouteDto.Node(2L, "성수", 1, true),
            ),
            edges = listOf(
                FavoriteRouteDto.Edge(1L, 2L, 6L, "6호선"),
            ),
            summary = FavoriteRouteDto.Summary(
                totalStops = 1,
                transferCount = 0,
                estimatedMinutes = 2,
            ),
        )

        given(memberUseCase.createFavoriteRoute(any())).willReturn(response)

        // when
        val result = mockMvc.perform(
            post("/v2/members/bookmarks/routes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
            .andDo(
                document(
                    "create-favorite-route",
                    getDocsRequest(),
                    getDocsResponse(),
                    requestFields(
                        fieldWithPath("sourceStationId").type(JsonFieldType.NUMBER).description("출발역 ID"),
                        fieldWithPath("destinationStationId").type(JsonFieldType.NUMBER).description("도착역 ID"),
                        fieldWithPath("title").type(JsonFieldType.STRING).description("경로 제목").optional(),
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.routeId").type(JsonFieldType.NUMBER).description("생성된 즐겨찾기 경로 ID"),
                        fieldWithPath("result.routeType").type(JsonFieldType.STRING).description("경로 타입"),
                        fieldWithPath("result.title").type(JsonFieldType.STRING).description("경로 제목").optional(),
                        fieldWithPath("result.sourceStationId").type(JsonFieldType.NUMBER).description("출발역 ID"),
                        fieldWithPath("result.sourceStationName").type(JsonFieldType.STRING).description("출발역 이름"),
                        fieldWithPath("result.destinationStationId").type(JsonFieldType.NUMBER).description("도착역 ID"),
                        fieldWithPath("result.destinationStationName").type(JsonFieldType.STRING).description("도착역 이름"),
                        fieldWithPath("result.nodes").type(JsonFieldType.ARRAY).description("그래프 노드"),
                        fieldWithPath("result.nodes[].stationId").type(JsonFieldType.NUMBER).description("노드 역 ID"),
                        fieldWithPath("result.nodes[].stationName").type(JsonFieldType.STRING).description("노드 역 이름"),
                        fieldWithPath("result.nodes[].order").type(JsonFieldType.NUMBER).description("노드 순서"),
                        fieldWithPath("result.nodes[].favorite").type(JsonFieldType.BOOLEAN).description("즐겨찾기 포함 여부"),
                        fieldWithPath("result.edges").type(JsonFieldType.ARRAY).description("그래프 엣지"),
                        fieldWithPath("result.edges[].fromStationId").type(JsonFieldType.NUMBER).description("엣지 출발역 ID"),
                        fieldWithPath("result.edges[].toStationId").type(JsonFieldType.NUMBER).description("엣지 도착역 ID"),
                        fieldWithPath("result.edges[].subwayLineId").type(JsonFieldType.NUMBER).description("엣지 노선 ID"),
                        fieldWithPath("result.edges[].subwayLineName").type(JsonFieldType.STRING).description("엣지 노선 이름"),
                        fieldWithPath("result.summary.totalStops").type(JsonFieldType.NUMBER).description("정거장 수"),
                        fieldWithPath("result.summary.transferCount").type(JsonFieldType.NUMBER).description("환승 횟수"),
                        fieldWithPath("result.summary.estimatedMinutes").type(JsonFieldType.NUMBER).description("예상 소요(분)"),
                    )
                )
            )
    }

    @Test
    fun getFavoriteRoutesTest() {
        // given
        val response = FavoriteRouteDto.GraphResponse(
            routes = listOf(
                FavoriteRouteDto.Route(
                    routeId = 100L,
                    routeType = FavoriteRouteDto.RouteType.CUSTOM,
                    title = "출근 루트",
                    sourceStationId = 1L,
                    sourceStationName = "안암",
                    destinationStationId = 2L,
                    destinationStationName = "성수",
                    nodes = listOf(
                        FavoriteRouteDto.Node(1L, "안암", 0, true),
                        FavoriteRouteDto.Node(2L, "성수", 1, true),
                    ),
                    edges = listOf(
                        FavoriteRouteDto.Edge(1L, 2L, 6L, "6호선"),
                    ),
                    summary = FavoriteRouteDto.Summary(
                        totalStops = 1,
                        transferCount = 0,
                        estimatedMinutes = 2,
                    ),
                )
            )
        )

        given(memberUseCase.getFavoriteRoutes()).willReturn(response)

        // when
        val result = mockMvc.perform(
            get("/v2/members/bookmarks/routes")
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
            .andDo(
                document(
                    "get-favorite-routes",
                    getDocsRequest(),
                    getDocsResponse(),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.routes").type(JsonFieldType.ARRAY).description("사용자 지정 경로 리스트"),
                        fieldWithPath("result.routes[].routeId").type(JsonFieldType.NUMBER).description("경로 ID"),
                        fieldWithPath("result.routes[].routeType").type(JsonFieldType.STRING).description("경로 타입"),
                        fieldWithPath("result.routes[].title").type(JsonFieldType.STRING).description("경로 제목").optional(),
                        fieldWithPath("result.routes[].sourceStationId").type(JsonFieldType.NUMBER).description("출발역 ID"),
                        fieldWithPath("result.routes[].sourceStationName").type(JsonFieldType.STRING).description("출발역 이름"),
                        fieldWithPath("result.routes[].destinationStationId").type(JsonFieldType.NUMBER).description("도착역 ID"),
                        fieldWithPath("result.routes[].destinationStationName").type(JsonFieldType.STRING).description("도착역 이름"),
                        fieldWithPath("result.routes[].nodes").type(JsonFieldType.ARRAY).description("그래프 노드"),
                        fieldWithPath("result.routes[].nodes[].stationId").type(JsonFieldType.NUMBER).description("노드 역 ID"),
                        fieldWithPath("result.routes[].nodes[].stationName").type(JsonFieldType.STRING).description("노드 역 이름"),
                        fieldWithPath("result.routes[].nodes[].order").type(JsonFieldType.NUMBER).description("노드 순서"),
                        fieldWithPath("result.routes[].nodes[].favorite").type(JsonFieldType.BOOLEAN).description("즐겨찾기 포함 여부"),
                        fieldWithPath("result.routes[].edges").type(JsonFieldType.ARRAY).description("그래프 엣지"),
                        fieldWithPath("result.routes[].edges[].fromStationId").type(JsonFieldType.NUMBER).description("엣지 출발역 ID"),
                        fieldWithPath("result.routes[].edges[].toStationId").type(JsonFieldType.NUMBER).description("엣지 도착역 ID"),
                        fieldWithPath("result.routes[].edges[].subwayLineId").type(JsonFieldType.NUMBER).description("엣지 노선 ID"),
                        fieldWithPath("result.routes[].edges[].subwayLineName").type(JsonFieldType.STRING).description("엣지 노선 이름"),
                        fieldWithPath("result.routes[].summary.totalStops").type(JsonFieldType.NUMBER).description("정거장 수"),
                        fieldWithPath("result.routes[].summary.transferCount").type(JsonFieldType.NUMBER).description("환승 횟수"),
                        fieldWithPath("result.routes[].summary.estimatedMinutes").type(JsonFieldType.NUMBER).description("예상 소요(분)"),
                    )
                )
            )
    }

    @Test
    fun getTodayCommuteCoachTest() {
        // given
        val route = FavoriteRouteDto.Route(
            routeId = null,
            routeType = FavoriteRouteDto.RouteType.RECOMMENDED,
            title = null,
            sourceStationId = 1L,
            sourceStationName = "안암",
            destinationStationId = 2L,
            destinationStationName = "성수",
            nodes = listOf(
                FavoriteRouteDto.Node(1L, "안암", 0, true),
                FavoriteRouteDto.Node(2L, "성수", 1, true),
            ),
            edges = listOf(
                FavoriteRouteDto.Edge(1L, 2L, 6L, "6호선"),
            ),
            summary = FavoriteRouteDto.Summary(
                totalStops = 1,
                transferCount = 0,
                estimatedMinutes = 2,
            ),
        )

        val response = CommuteCoachDto.Response(
            generatedAt = "2026-02-25T09:00:00+09:00",
            targetArrivalAt = "09:00",
            safeDepartureAt = "08:52",
            departureInMinutes = 17,
            riskLevel = CommuteCoachDto.RiskLevel.LOW,
            riskReasons = listOf("현재 조건에서 도착 여유 시간이 충분합니다."),
            primaryRoute = route,
            alternativeRoutes = listOf(route.copy(destinationStationId = 3L, destinationStationName = "왕십리")),
            guidanceMessage = "권장 출발 시각에 맞춰 이동하면 안정적으로 도착할 가능성이 높아요.",
        )

        given(memberUseCase.getTodayCommuteCoach("09:00", "Asia/Seoul")).willReturn(response)

        // when
        val result = mockMvc.perform(
            get("/v2/members/commute-coach/today")
                .queryParam("targetArrivalAt", "09:00")
                .queryParam("timezone", "Asia/Seoul")
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
            .andDo(
                document(
                    "get-today-commute-coach",
                    getDocsRequest(),
                    getDocsResponse(),
                    queryParameters(
                        parameterWithName("targetArrivalAt").description("목표 도착 시각(HH:mm)").optional(),
                        parameterWithName("timezone").description("타임존(예: Asia/Seoul)").optional(),
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.generatedAt").type(JsonFieldType.STRING).description("응답 생성 시각"),
                        fieldWithPath("result.targetArrivalAt").type(JsonFieldType.STRING).description("목표 도착 시각(HH:mm)"),
                        fieldWithPath("result.safeDepartureAt").type(JsonFieldType.STRING).description("권장 출발 시각(HH:mm)").optional(),
                        fieldWithPath("result.departureInMinutes").type(JsonFieldType.NUMBER).description("지금 기준 권장 출발까지 남은 분").optional(),
                        fieldWithPath("result.riskLevel").type(JsonFieldType.STRING).description("출근 위험도(LOW|MEDIUM|HIGH)"),
                        fieldWithPath("result.riskReasons").type(JsonFieldType.ARRAY).description("위험도 판단 사유"),
                        fieldWithPath("result.primaryRoute").type(JsonFieldType.OBJECT).description("기본 추천 경로").optional(),
                        fieldWithPath("result.primaryRoute.routeId").type(JsonFieldType.NULL).description("경로 ID(추천 경로는 null)").optional(),
                        fieldWithPath("result.primaryRoute.routeType").type(JsonFieldType.STRING).description("경로 타입").optional(),
                        fieldWithPath("result.primaryRoute.title").type(JsonFieldType.NULL).description("경로 제목").optional(),
                        fieldWithPath("result.primaryRoute.sourceStationId").type(JsonFieldType.NUMBER).description("출발역 ID").optional(),
                        fieldWithPath("result.primaryRoute.sourceStationName").type(JsonFieldType.STRING).description("출발역 이름").optional(),
                        fieldWithPath("result.primaryRoute.destinationStationId").type(JsonFieldType.NUMBER).description("도착역 ID").optional(),
                        fieldWithPath("result.primaryRoute.destinationStationName").type(JsonFieldType.STRING).description("도착역 이름").optional(),
                        fieldWithPath("result.primaryRoute.nodes").type(JsonFieldType.ARRAY).description("기본 경로 노드").optional(),
                        fieldWithPath("result.primaryRoute.nodes[].stationId").type(JsonFieldType.NUMBER).description("노드 역 ID").optional(),
                        fieldWithPath("result.primaryRoute.nodes[].stationName").type(JsonFieldType.STRING).description("노드 역 이름").optional(),
                        fieldWithPath("result.primaryRoute.nodes[].order").type(JsonFieldType.NUMBER).description("노드 순서").optional(),
                        fieldWithPath("result.primaryRoute.nodes[].favorite").type(JsonFieldType.BOOLEAN).description("즐겨찾기 포함 여부").optional(),
                        fieldWithPath("result.primaryRoute.edges").type(JsonFieldType.ARRAY).description("기본 경로 엣지").optional(),
                        fieldWithPath("result.primaryRoute.edges[].fromStationId").type(JsonFieldType.NUMBER).description("엣지 출발역 ID").optional(),
                        fieldWithPath("result.primaryRoute.edges[].toStationId").type(JsonFieldType.NUMBER).description("엣지 도착역 ID").optional(),
                        fieldWithPath("result.primaryRoute.edges[].subwayLineId").type(JsonFieldType.NUMBER).description("엣지 노선 ID").optional(),
                        fieldWithPath("result.primaryRoute.edges[].subwayLineName").type(JsonFieldType.STRING).description("엣지 노선 이름").optional(),
                        fieldWithPath("result.primaryRoute.summary.totalStops").type(JsonFieldType.NUMBER).description("정거장 수").optional(),
                        fieldWithPath("result.primaryRoute.summary.transferCount").type(JsonFieldType.NUMBER).description("환승 횟수").optional(),
                        fieldWithPath("result.primaryRoute.summary.estimatedMinutes").type(JsonFieldType.NUMBER).description("예상 소요(분)").optional(),
                        fieldWithPath("result.alternativeRoutes").type(JsonFieldType.ARRAY).description("대체 경로 리스트"),
                        fieldWithPath("result.alternativeRoutes[].routeId").type(JsonFieldType.NULL).description("대체 경로 ID").optional(),
                        fieldWithPath("result.alternativeRoutes[].routeType").type(JsonFieldType.STRING).description("대체 경로 타입"),
                        fieldWithPath("result.alternativeRoutes[].title").type(JsonFieldType.NULL).description("대체 경로 제목").optional(),
                        fieldWithPath("result.alternativeRoutes[].sourceStationId").type(JsonFieldType.NUMBER).description("대체 경로 출발역 ID"),
                        fieldWithPath("result.alternativeRoutes[].sourceStationName").type(JsonFieldType.STRING).description("대체 경로 출발역 이름"),
                        fieldWithPath("result.alternativeRoutes[].destinationStationId").type(JsonFieldType.NUMBER).description("대체 경로 도착역 ID"),
                        fieldWithPath("result.alternativeRoutes[].destinationStationName").type(JsonFieldType.STRING).description("대체 경로 도착역 이름"),
                        fieldWithPath("result.alternativeRoutes[].nodes").type(JsonFieldType.ARRAY).description("대체 경로 노드"),
                        fieldWithPath("result.alternativeRoutes[].nodes[].stationId").type(JsonFieldType.NUMBER).description("대체 경로 노드 역 ID"),
                        fieldWithPath("result.alternativeRoutes[].nodes[].stationName").type(JsonFieldType.STRING).description("대체 경로 노드 역 이름"),
                        fieldWithPath("result.alternativeRoutes[].nodes[].order").type(JsonFieldType.NUMBER).description("대체 경로 노드 순서"),
                        fieldWithPath("result.alternativeRoutes[].nodes[].favorite").type(JsonFieldType.BOOLEAN).description("대체 경로 즐겨찾기 포함 여부"),
                        fieldWithPath("result.alternativeRoutes[].edges").type(JsonFieldType.ARRAY).description("대체 경로 엣지"),
                        fieldWithPath("result.alternativeRoutes[].edges[].fromStationId").type(JsonFieldType.NUMBER).description("대체 경로 엣지 출발역 ID"),
                        fieldWithPath("result.alternativeRoutes[].edges[].toStationId").type(JsonFieldType.NUMBER).description("대체 경로 엣지 도착역 ID"),
                        fieldWithPath("result.alternativeRoutes[].edges[].subwayLineId").type(JsonFieldType.NUMBER).description("대체 경로 엣지 노선 ID"),
                        fieldWithPath("result.alternativeRoutes[].edges[].subwayLineName").type(JsonFieldType.STRING).description("대체 경로 엣지 노선 이름"),
                        fieldWithPath("result.alternativeRoutes[].summary.totalStops").type(JsonFieldType.NUMBER).description("대체 경로 정거장 수"),
                        fieldWithPath("result.alternativeRoutes[].summary.transferCount").type(JsonFieldType.NUMBER).description("대체 경로 환승 횟수"),
                        fieldWithPath("result.alternativeRoutes[].summary.estimatedMinutes").type(JsonFieldType.NUMBER).description("대체 경로 예상 소요(분)"),
                        fieldWithPath("result.guidanceMessage").type(JsonFieldType.STRING).description("사용자 안내 문구"),
                    )
                )
            )
    }

    @Test
    fun getRouteConnectionRecommendationsTest() {
        // given
        val response = RouteConnectionDto.Response(
            generatedAt = "2026-02-26T10:00:00+09:00",
            matchingPolicy = RouteConnectionDto.MatchingPolicy(
                sourceMaxDistance = 1,
                destinationMaxDistance = 1,
                totalMaxDistance = 2,
            ),
            anchorRoute = RouteConnectionDto.AnchorRoute(
                sourceStationId = 1L,
                sourceStationName = "안암",
                destinationStationId = 2L,
                destinationStationName = "성수",
            ),
            recommendations = listOf(
                RouteConnectionDto.MemberRecommendation(
                    memberId = 10L,
                    nickname = "routeMate",
                    routeId = 100L,
                    title = "출근 루트",
                    sourceStationId = 11L,
                    sourceStationName = "보문",
                    destinationStationId = 3L,
                    destinationStationName = "뚝섬",
                    sourceDistance = 1,
                    destinationDistance = 1,
                    totalDistance = 2,
                    matchScore = 60,
                    estimatedMinutes = 4,
                    reason = "출발역 1정거장 차이 · 도착역 1정거장 차이",
                )
            ),
            groups = listOf(
                RouteConnectionDto.RouteGroup(
                    groupId = "11-3",
                    sourceStationId = 11L,
                    sourceStationName = "보문",
                    destinationStationId = 3L,
                    destinationStationName = "뚝섬",
                    memberCount = 1,
                    members = listOf(
                        RouteConnectionDto.RouteGroupMember(
                            memberId = 10L,
                            nickname = "routeMate",
                            matchScore = 60,
                            totalDistance = 2,
                        )
                    ),
                )
            ),
            graph = RouteConnectionDto.SocialGraph(
                nodes = listOf(
                    RouteConnectionDto.SocialGraphNode(memberId = 1L, nickname = "me", me = true),
                    RouteConnectionDto.SocialGraphNode(memberId = 10L, nickname = "routeMate", me = false),
                ),
                edges = listOf(
                    RouteConnectionDto.SocialGraphEdge(
                        fromMemberId = 1L,
                        toMemberId = 10L,
                        score = 60,
                        label = "1/1",
                    )
                ),
            ),
        )

        given(memberUseCase.getRouteConnectionRecommendations(12, 6)).willReturn(response)

        // when
        val result = mockMvc.perform(
            get("/v2/members/route-connections/recommendations")
                .queryParam("limit", "12")
                .queryParam("groupLimit", "6")
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
            .andDo(
                document(
                    "get-route-connection-recommendations",
                    getDocsRequest(),
                    getDocsResponse(),
                    queryParameters(
                        parameterWithName("limit").description("추천 사용자 최대 개수").optional(),
                        parameterWithName("groupLimit").description("경로 그룹 최대 개수").optional(),
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.generatedAt").type(JsonFieldType.STRING).description("응답 생성 시각"),
                        fieldWithPath("result.matchingPolicy.sourceMaxDistance").type(JsonFieldType.NUMBER).description("출발역 허용 정거장 차이"),
                        fieldWithPath("result.matchingPolicy.destinationMaxDistance").type(JsonFieldType.NUMBER).description("도착역 허용 정거장 차이"),
                        fieldWithPath("result.matchingPolicy.totalMaxDistance").type(JsonFieldType.NUMBER).description("전체 허용 정거장 차이"),
                        fieldWithPath("result.anchorRoute").type(JsonFieldType.OBJECT).description("내 기준 경로").optional(),
                        fieldWithPath("result.anchorRoute.sourceStationId").type(JsonFieldType.NUMBER).description("기준 출발역 ID").optional(),
                        fieldWithPath("result.anchorRoute.sourceStationName").type(JsonFieldType.STRING).description("기준 출발역 이름").optional(),
                        fieldWithPath("result.anchorRoute.destinationStationId").type(JsonFieldType.NUMBER).description("기준 도착역 ID").optional(),
                        fieldWithPath("result.anchorRoute.destinationStationName").type(JsonFieldType.STRING).description("기준 도착역 이름").optional(),
                        fieldWithPath("result.recommendations").type(JsonFieldType.ARRAY).description("경로 기반 추천 사용자 목록"),
                        fieldWithPath("result.recommendations[].memberId").type(JsonFieldType.NUMBER).description("추천 사용자 ID"),
                        fieldWithPath("result.recommendations[].nickname").type(JsonFieldType.STRING).description("추천 사용자 닉네임"),
                        fieldWithPath("result.recommendations[].routeId").type(JsonFieldType.NUMBER).description("추천 사용자 경로 ID").optional(),
                        fieldWithPath("result.recommendations[].title").type(JsonFieldType.STRING).description("추천 사용자 경로 별칭").optional(),
                        fieldWithPath("result.recommendations[].sourceStationId").type(JsonFieldType.NUMBER).description("추천 사용자 출발역 ID"),
                        fieldWithPath("result.recommendations[].sourceStationName").type(JsonFieldType.STRING).description("추천 사용자 출발역 이름"),
                        fieldWithPath("result.recommendations[].destinationStationId").type(JsonFieldType.NUMBER).description("추천 사용자 도착역 ID"),
                        fieldWithPath("result.recommendations[].destinationStationName").type(JsonFieldType.STRING).description("추천 사용자 도착역 이름"),
                        fieldWithPath("result.recommendations[].sourceDistance").type(JsonFieldType.NUMBER).description("출발역 정거장 차이"),
                        fieldWithPath("result.recommendations[].destinationDistance").type(JsonFieldType.NUMBER).description("도착역 정거장 차이"),
                        fieldWithPath("result.recommendations[].totalDistance").type(JsonFieldType.NUMBER).description("전체 정거장 차이"),
                        fieldWithPath("result.recommendations[].matchScore").type(JsonFieldType.NUMBER).description("매칭 점수"),
                        fieldWithPath("result.recommendations[].estimatedMinutes").type(JsonFieldType.NUMBER).description("정거장 차이 기반 추정 이동 시간"),
                        fieldWithPath("result.recommendations[].reason").type(JsonFieldType.STRING).description("추천 근거 요약"),
                        fieldWithPath("result.groups").type(JsonFieldType.ARRAY).description("유사 경로 그룹"),
                        fieldWithPath("result.groups[].groupId").type(JsonFieldType.STRING).description("그룹 식별자"),
                        fieldWithPath("result.groups[].sourceStationId").type(JsonFieldType.NUMBER).description("그룹 출발역 ID"),
                        fieldWithPath("result.groups[].sourceStationName").type(JsonFieldType.STRING).description("그룹 출발역 이름"),
                        fieldWithPath("result.groups[].destinationStationId").type(JsonFieldType.NUMBER).description("그룹 도착역 ID"),
                        fieldWithPath("result.groups[].destinationStationName").type(JsonFieldType.STRING).description("그룹 도착역 이름"),
                        fieldWithPath("result.groups[].memberCount").type(JsonFieldType.NUMBER).description("그룹 인원"),
                        fieldWithPath("result.groups[].members").type(JsonFieldType.ARRAY).description("그룹 구성원"),
                        fieldWithPath("result.groups[].members[].memberId").type(JsonFieldType.NUMBER).description("구성원 ID"),
                        fieldWithPath("result.groups[].members[].nickname").type(JsonFieldType.STRING).description("구성원 닉네임"),
                        fieldWithPath("result.groups[].members[].matchScore").type(JsonFieldType.NUMBER).description("구성원 매칭 점수"),
                        fieldWithPath("result.groups[].members[].totalDistance").type(JsonFieldType.NUMBER).description("구성원 정거장 차이 합계"),
                        fieldWithPath("result.graph.nodes").type(JsonFieldType.ARRAY).description("인맥 그래프 노드"),
                        fieldWithPath("result.graph.nodes[].memberId").type(JsonFieldType.NUMBER).description("노드 회원 ID"),
                        fieldWithPath("result.graph.nodes[].nickname").type(JsonFieldType.STRING).description("노드 닉네임"),
                        fieldWithPath("result.graph.nodes[].me").type(JsonFieldType.BOOLEAN).description("내 계정 여부"),
                        fieldWithPath("result.graph.edges").type(JsonFieldType.ARRAY).description("인맥 그래프 엣지"),
                        fieldWithPath("result.graph.edges[].fromMemberId").type(JsonFieldType.NUMBER).description("엣지 시작 회원 ID"),
                        fieldWithPath("result.graph.edges[].toMemberId").type(JsonFieldType.NUMBER).description("엣지 도착 회원 ID"),
                        fieldWithPath("result.graph.edges[].score").type(JsonFieldType.NUMBER).description("엣지 점수"),
                        fieldWithPath("result.graph.edges[].label").type(JsonFieldType.STRING).description("엣지 설명 라벨"),
                    )
                )
            )
    }

    @Test
    fun deleteFavoriteRouteTest() {
        // given
        val response = FavoriteRouteDto.DeleteResponse(routeId = 100L)
        given(memberUseCase.deleteFavoriteRoute(100L)).willReturn(response)

        // when
        val result = mockMvc.perform(
            delete("/v2/members/bookmarks/routes/{routeId}", 100L)
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
            .andDo(
                document(
                    "delete-favorite-route",
                    getDocsRequest(),
                    getDocsResponse(),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.routeId").type(JsonFieldType.NUMBER).description("삭제된 경로 ID"),
                    )
                )
            )
    }

    @Test
    fun searchMembersTest() {
        //given
        val response = SearchMemberDto.Response(
            members = listOf(
                SearchMemberDto.SearchMemberResponse(
                    id = 1L,
                    nickname = "nickname",
                )
            )
        )

        given(memberUseCase.searchMembers(any())).willReturn(response)

        // when
        val result = mockMvc.perform(
            get("/v1/members/search")
                .queryParam("nickname", "닉네임")
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
            .andDo(document("search-members",
                getDocsRequest(),
                getDocsResponse(),
                queryParameters(
                    parameterWithName("nickname").description("닉네임 입력"),
                ),
                responseFields(
                    *commonResponseFields(),
                    fieldWithPath("result.members[].id").type(JsonFieldType.NUMBER).description("회원 아이디"),
                    fieldWithPath("result.members[].nickname").type(JsonFieldType.STRING).description("회원 닉네임"),
                )
            ))
    }

    @Test
    fun updateTokenTest() {
        // given
        BDDMockito.willDoNothing().given(memberUseCase).updateFcmToken(any())

        val request = UpdateFcmTokenDto.Request(
            fcmToken = "token"
        )

        // when
        val result = mockMvc.perform(
            patch("/v1/members/fcm-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
            .andDo(
                document(
                    "Update FCM Token",
                    getDocsRequest(),
                    getDocsResponse(),
                    requestFields(
                        fieldWithPath("fcmToken").type(JsonFieldType.STRING).description("FCM Token"),
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result").optional().description("X")
                    )
                ),
            )
    }

    @Test
    fun getMemberProfileTest() {
        // given
        val response = GetMemberProfileDto.Response(
            memberId = 1,
            nickname = "nickname",
            email = "email@mail.com",
            maskedEmail = "em***@mail.com",
            gender = GenderType.MALE,
            ageRange = "20",
            isMine = false,
            visibility = GetMemberProfileDto.Visibility(
                profilePublic = true,
                emailPublic = true,
                genderAgePublic = true,
                postsPublic = true,
                commentsPublic = true,
                profileVisible = true,
                postsVisible = true,
                commentsVisible = true,
            ),
            activities = GetMemberProfileDto.Activities(
                posts = listOf(
                    GetMemberProfileDto.PostActivity(
                        articleType = backend.team.ahachul_backend.api.article.domain.model.ArticleType.COMMUNITY,
                        articleId = 11,
                        title = "제목",
                        contentPreview = "본문",
                        writer = "nickname",
                        subwayLineId = 2,
                        stationId = 201,
                        createdAt = "2026-02-24 10:00:00.000",
                    )
                ),
                comments = listOf(
                    GetMemberProfileDto.CommentActivity(
                        commentId = 51,
                        articleType = backend.team.ahachul_backend.api.article.domain.model.ArticleType.COMMUNITY,
                        articleId = 11,
                        contentPreview = "댓글",
                        writer = "nickname",
                        createdAt = "2026-02-24 10:30:00.000",
                    )
                ),
            )
        )

        given(memberUseCase.getMemberProfile("nickname", true, 20)).willReturn(response)

        // when
        val result = mockMvc.perform(
            get("/v1/members/{nickname}/profile", "nickname")
                .queryParam("asPublic", "true")
                .queryParam("limit", "20")
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(status().isOk)
            .andDo(
                document(
                    "get-member-profile",
                    getDocsRequest(),
                    getDocsResponse(),
                    queryParameters(
                        parameterWithName("asPublic").description("타인 시점 강제 렌더링 여부").optional(),
                        parameterWithName("limit").description("활동 목록 최대 개수").optional(),
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.memberId").type(JsonFieldType.NUMBER).description("회원 아이디"),
                        fieldWithPath("result.nickname").type(JsonFieldType.STRING).description("닉네임").optional(),
                        fieldWithPath("result.email").type(JsonFieldType.STRING).description("이메일").optional(),
                        fieldWithPath("result.maskedEmail").type(JsonFieldType.STRING).description("마스킹 이메일").optional(),
                        fieldWithPath("result.gender").type(JsonFieldType.STRING).description("성별").optional(),
                        fieldWithPath("result.ageRange").type(JsonFieldType.STRING).description("연령대").optional(),
                        fieldWithPath("result.isMine").type(JsonFieldType.BOOLEAN).description("본인 프로필 여부"),
                        fieldWithPath("result.visibility.profilePublic").type(JsonFieldType.BOOLEAN).description("프로필 전체 공개 설정"),
                        fieldWithPath("result.visibility.emailPublic").type(JsonFieldType.BOOLEAN).description("이메일 공개 설정"),
                        fieldWithPath("result.visibility.genderAgePublic").type(JsonFieldType.BOOLEAN).description("성별/연령대 공개 설정"),
                        fieldWithPath("result.visibility.postsPublic").type(JsonFieldType.BOOLEAN).description("작성 글 공개 설정"),
                        fieldWithPath("result.visibility.commentsPublic").type(JsonFieldType.BOOLEAN).description("작성 댓글 공개 설정"),
                        fieldWithPath("result.visibility.profileVisible").type(JsonFieldType.BOOLEAN).description("현재 조회 기준 프로필 노출 여부"),
                        fieldWithPath("result.visibility.postsVisible").type(JsonFieldType.BOOLEAN).description("현재 조회 기준 작성 글 노출 여부"),
                        fieldWithPath("result.visibility.commentsVisible").type(JsonFieldType.BOOLEAN).description("현재 조회 기준 작성 댓글 노출 여부"),
                        fieldWithPath("result.activities.posts").type(JsonFieldType.ARRAY).description("작성 글 목록"),
                        fieldWithPath("result.activities.posts[].articleType").type(JsonFieldType.STRING).description("게시글 타입").optional(),
                        fieldWithPath("result.activities.posts[].articleId").type(JsonFieldType.NUMBER).description("게시글 아이디").optional(),
                        fieldWithPath("result.activities.posts[].title").type(JsonFieldType.STRING).description("게시글 제목").optional(),
                        fieldWithPath("result.activities.posts[].contentPreview").type(JsonFieldType.STRING).description("게시글 미리보기").optional(),
                        fieldWithPath("result.activities.posts[].writer").type(JsonFieldType.STRING).description("작성자").optional(),
                        fieldWithPath("result.activities.posts[].subwayLineId").type(JsonFieldType.NUMBER).description("호선 아이디").optional(),
                        fieldWithPath("result.activities.posts[].stationId").type(JsonFieldType.NUMBER).description("역 아이디").optional(),
                        fieldWithPath("result.activities.posts[].createdAt").type(JsonFieldType.STRING).description("작성 시각").optional(),
                        fieldWithPath("result.activities.comments").type(JsonFieldType.ARRAY).description("작성 댓글 목록"),
                        fieldWithPath("result.activities.comments[].commentId").type(JsonFieldType.NUMBER).description("댓글 아이디").optional(),
                        fieldWithPath("result.activities.comments[].articleType").type(JsonFieldType.STRING).description("원글 타입").optional(),
                        fieldWithPath("result.activities.comments[].articleId").type(JsonFieldType.NUMBER).description("원글 아이디").optional(),
                        fieldWithPath("result.activities.comments[].contentPreview").type(JsonFieldType.STRING).description("댓글 미리보기").optional(),
                        fieldWithPath("result.activities.comments[].writer").type(JsonFieldType.STRING).description("작성자").optional(),
                        fieldWithPath("result.activities.comments[].createdAt").type(JsonFieldType.STRING).description("작성 시각").optional(),
                    )
                )
            )
    }
}
