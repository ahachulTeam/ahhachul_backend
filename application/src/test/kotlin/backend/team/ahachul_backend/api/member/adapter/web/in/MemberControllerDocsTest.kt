package backend.team.ahachul_backend.api.member.adapter.web.`in`

import backend.team.ahachul_backend.api.member.adapter.web.`in`.dto.*
import backend.team.ahachul_backend.api.member.adapter.web.`in`.dto.BookmarkStationDto.BookmarkStation
import backend.team.ahachul_backend.api.member.application.port.`in`.MemberUseCase
import backend.team.ahachul_backend.api.member.domain.model.GenderType
import backend.team.ahachul_backend.config.controller.CommonDocsTestConfig
import org.junit.jupiter.api.Test
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
            gender = GenderType.MALE,
            ageRange = "20"
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
                        fieldWithPath("result.memberId").type(JsonFieldType.NUMBER)
                            .description("사용자 Identification Key"),
                        fieldWithPath("result.nickname").type(JsonFieldType.STRING).description("사용자 닉네임").optional(),
                        fieldWithPath("result.email").type(JsonFieldType.STRING).description("사용자 이메일").optional(),
                        fieldWithPath("result.gender").type("GenderType").description("사용자 성별")
                            .attributes(getFormatAttribute("MALE, FEMALE")).optional(),
                        fieldWithPath("result.ageRange").type(JsonFieldType.STRING).description("사용자 연령대")
                            .attributes(getFormatAttribute("1 : 1세 이상 10세 미만 ${getNewLine()} 10 : 10세 이상 20세 미만 ${getNewLine()} 20 : 20세 이상 30세 미만 ${getNewLine()} ..."))
                            .optional(),
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
            ageRange = "20"
        )

        given(memberUseCase.updateMember(any()))
            .willReturn(response)

        val request = UpdateMemberDto.Request(
            nickname = "nickname",
            gender = GenderType.MALE,
            ageRange = "20"
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
                        fieldWithPath("gender").type("GenderType").description("사용자 성별")
                            .attributes(getFormatAttribute("MALE, FEMALE")).optional(),
                        fieldWithPath("ageRange").type(JsonFieldType.STRING).description("사용자 연령대")
                            .attributes(getFormatAttribute("1 : 1세 이상 10세 미만 ${getNewLine()} 10 : 10세 이상 20세 미만 ${getNewLine()} 20 : 20세 이상 30세 미만 ${getNewLine()} ..."))
                            .optional(),

                        ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.nickname").type(JsonFieldType.STRING).description("사용자 닉네임").optional(),
                        fieldWithPath("result.gender").type("GenderType").description("사용자 성별")
                            .attributes(getFormatAttribute("MALE, FEMALE")).optional(),
                        fieldWithPath("result.ageRange").type(JsonFieldType.STRING).description("사용자 연령대")
                            .attributes(getFormatAttribute("1 : 1세 이상 10세 미만 ${getNewLine()} 10 : 10세 이상 20세 미만 ${getNewLine()} 20 : 20세 이상 30세 미만 ${getNewLine()} ..."))
                            .optional(),
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

        val request = BookmarkStationDto.Request(
            listOf(
                BookmarkStation("발산역", "집"),
                BookmarkStation("우장산역", "학교"),
                BookmarkStation("화곡역", "즐겨찾는 장소"),
            )
        )

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
                        fieldWithPath("stations[].label").type(JsonFieldType.STRING).description("즐겨찾는 역 별명")
                            .optional(),
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.stationInfoList").type(JsonFieldType.ARRAY)
                            .description("즐겨찾기 한 역 정보 리스트"),
                        fieldWithPath("result.stationInfoList[].stationId").type(JsonFieldType.NUMBER)
                            .description("역 고유 ID"),
                        fieldWithPath("result.stationInfoList[].stationName").type(JsonFieldType.STRING)
                            .description("역 이름"),
                        fieldWithPath("result.stationInfoList[].label").type(JsonFieldType.STRING).description("역 별명")
                            .optional(),
                        fieldWithPath("result.stationInfoList[].subwayLineInfoList").type(JsonFieldType.ARRAY)
                            .description("해당 역이 존재하는 노선 리스트"),
                        fieldWithPath("result.stationInfoList[].subwayLineInfoList[].subwayLineId").type(JsonFieldType.NUMBER)
                            .description("노선 고유 ID"),
                        fieldWithPath("result.stationInfoList[].subwayLineInfoList[].subwayLineName").type(JsonFieldType.STRING)
                            .description("노선 이름"),
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
                        fieldWithPath("result.stationInfoList").type(JsonFieldType.ARRAY)
                            .description("즐겨찾기 한 역 정보 리스트"),
                        fieldWithPath("result.stationInfoList[].stationId").type(JsonFieldType.NUMBER)
                            .description("역 고유 ID"),
                        fieldWithPath("result.stationInfoList[].stationName").type(JsonFieldType.STRING)
                            .description("역 이름"),
                        fieldWithPath("result.stationInfoList[].label").type(JsonFieldType.STRING).description("역 별명")
                            .optional(),
                        fieldWithPath("result.stationInfoList[].subwayLineInfoList").type(JsonFieldType.ARRAY)
                            .description("해당 역이 존재하는 노선 리스트"),
                        fieldWithPath("result.stationInfoList[].subwayLineInfoList[].subwayLineId").type(JsonFieldType.NUMBER)
                            .description("노선 고유 ID"),
                        fieldWithPath("result.stationInfoList[].subwayLineInfoList[].subwayLineName").type(JsonFieldType.STRING)
                            .description("노선 이름"),
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
            .andDo(
                document(
                    "search-members",
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
            )

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
            put("/v1/members/fcm-token")
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
}
