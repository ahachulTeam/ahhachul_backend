package backend.team.ahachul_backend.api.member.adapter.web.`in`

import backend.team.ahachul_backend.api.member.adapter.web.`in`.dto.*
import backend.team.ahachul_backend.api.member.application.port.`in`.MemberUseCase
import backend.team.ahachul_backend.api.member.domain.model.GenderType
import backend.team.ahachul_backend.config.controller.CommonDocsTestConfig
import org.junit.jupiter.api.Assertions.*
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
                        fieldWithPath("result.memberId").type(JsonFieldType.NUMBER).description("사용자 Identification Key"),
                        fieldWithPath("result.nickname").type(JsonFieldType.STRING).description("사용자 닉네임").optional(),
                        fieldWithPath("result.email").type(JsonFieldType.STRING).description("사용자 이메일").optional(),
                        fieldWithPath("result.gender").type("GenderType").description("사용자 성별").attributes(getFormatAttribute("MALE, FEMALE")).optional(),
                        fieldWithPath("result.ageRange").type(JsonFieldType.STRING).description("사용자 연령대").attributes(getFormatAttribute("1 : 1세 이상 10세 미만 ${getNewLine()} 10 : 10세 이상 20세 미만 ${getNewLine()} 20 : 20세 이상 30세 미만 ${getNewLine()} ...")).optional(),
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
                        fieldWithPath("gender").type("GenderType").description("사용자 성별").attributes(getFormatAttribute("MALE, FEMALE")).optional(),
                        fieldWithPath("ageRange").type(JsonFieldType.STRING).description("사용자 연령대").attributes(getFormatAttribute("1 : 1세 이상 10세 미만 ${getNewLine()} 10 : 10세 이상 20세 미만 ${getNewLine()} 20 : 20세 이상 30세 미만 ${getNewLine()} ...")).optional(),

                        ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.nickname").type(JsonFieldType.STRING).description("사용자 닉네임").optional(),
                        fieldWithPath("result.gender").type("GenderType").description("사용자 성별").attributes(getFormatAttribute("MALE, FEMALE")).optional(),
                        fieldWithPath("result.ageRange").type(JsonFieldType.STRING).description("사용자 연령대").attributes(getFormatAttribute("1 : 1세 이상 10세 미만 ${getNewLine()} 10 : 10세 이상 20세 미만 ${getNewLine()} 20 : 20세 이상 30세 미만 ${getNewLine()} ...")).optional(),
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
        val response = BookmarkStationDto.Response(listOf(1L, 2L, 3L))
        given(memberUseCase.bookmarkStation(any()))
                .willReturn(response)

        val request = BookmarkStationDto.Request(listOf("발산역", "우장산역", "화곡역"))

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
                        fieldWithPath("stationNames").type(JsonFieldType.ARRAY).description("즐겨찾는 역 이름 리스트"),
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.memberStationIds").type(JsonFieldType.ARRAY).description("즐겨찾는 역 ID 리스트"),
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
                        fieldWithPath("result.stationInfoList[].subwayLineInfoList").type(JsonFieldType.ARRAY).description("해당 역이 존재하는 노선 리스트"),
                        fieldWithPath("result.stationInfoList[].subwayLineInfoList[].subwayLineId").type(JsonFieldType.NUMBER).description("노선 고유 ID"),
                        fieldWithPath("result.stationInfoList[].subwayLineInfoList[].subwayLineName").type(JsonFieldType.STRING).description("노선 이름"),
                    )
                )
            )
    }
}
