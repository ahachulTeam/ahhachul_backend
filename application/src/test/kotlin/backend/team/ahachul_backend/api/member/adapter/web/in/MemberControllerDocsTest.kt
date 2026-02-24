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
