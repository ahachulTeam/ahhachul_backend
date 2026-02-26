package backend.team.ahachul_backend.api.dailyvote.adapter.`in`

import backend.team.ahachul_backend.api.dailyvote.adapter.`in`.dto.DailyVoteDto
import backend.team.ahachul_backend.api.dailyvote.application.port.`in`.DailyVoteUseCase
import backend.team.ahachul_backend.config.controller.CommonDocsTestConfig
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete
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

@WebMvcTest(DailyVoteController::class)
class DailyVoteControllerDocsTest : CommonDocsTestConfig() {

    @MockBean
    lateinit var dailyVoteUseCase: DailyVoteUseCase

    @Test
    fun getTodayVotes() {
        val response = DailyVoteDto.TodayResponse(
            generatedAt = "2026-02-25T09:10:00+09:00",
            profileHint = "SCHOOL",
            primaryPoll = buildPollCard(pollId = 301, question = "오늘 2호선 등교길 어땠나요?", isPrimary = true),
            secondaryPoll = buildPollCard(pollId = 302, question = "오늘 2호선 출근길 어땠나요?", isPrimary = false),
            stationDiary = DailyVoteDto.StationDiaryCard(
                pollId = 303,
                question = "오늘의 안암역은 어떠셨나요?",
                stationId = 557,
                stationName = "안암",
                visible = true,
                commentCount = 12,
            ),
        )

        given(dailyVoteUseCase.getToday(any())).willReturn(response)

        mockMvc.perform(
            get("/v2/daily-votes/today")
                .header("Authorization", "Bearer <Access Token>")
                .queryParam("timezone", "Asia/Seoul")
                .accept(MediaType.APPLICATION_JSON),
        )
            .andExpect(status().isOk)
            .andDo(
                document(
                    "get-daily-votes-today-v2",
                    getDocsRequest(),
                    getDocsResponse(),
                    requestHeaders(
                        headerWithName("Authorization").description("엑세스 토큰"),
                    ),
                    queryParameters(
                        parameterWithName("timezone").optional().description("시간대(기본 Asia/Seoul)"),
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.generatedAt").type(JsonFieldType.STRING).description("응답 생성 시각"),
                        fieldWithPath("result.profileHint").type(JsonFieldType.STRING).description("개인화 힌트(COMMUTE/SCHOOL/NO_FAVORITE_STATION)"),
                        fieldWithPath("result.primaryPoll.pollId").type(JsonFieldType.NUMBER).description("주 투표 ID").optional(),
                        fieldWithPath("result.primaryPoll.question").type(JsonFieldType.STRING).description("주 투표 질문").optional(),
                        fieldWithPath("result.primaryPoll.pollKind").type(JsonFieldType.STRING).description("주 투표 종류").optional(),
                        fieldWithPath("result.primaryPoll.pollContext").type(JsonFieldType.STRING).description("주 투표 컨텍스트").optional(),
                        fieldWithPath("result.primaryPoll.pollSlot").type(JsonFieldType.STRING).description("주 투표 시간대").optional(),
                        fieldWithPath("result.primaryPoll.stationId").type(JsonFieldType.NUMBER).description("역 ID").optional(),
                        fieldWithPath("result.primaryPoll.stationName").type(JsonFieldType.STRING).description("역 이름").optional(),
                        fieldWithPath("result.primaryPoll.subwayLineId").type(JsonFieldType.NUMBER).description("노선 ID").optional(),
                        fieldWithPath("result.primaryPoll.subwayLineName").type(JsonFieldType.STRING).description("노선명").optional(),
                        fieldWithPath("result.primaryPoll.isPrimary").type(JsonFieldType.BOOLEAN).description("주 투표 여부").optional(),
                        fieldWithPath("result.primaryPoll.voted").type(JsonFieldType.BOOLEAN).description("내 투표 여부").optional(),
                        fieldWithPath("result.primaryPoll.selectedOptionCode").type(JsonFieldType.STRING).description("내 선택 옵션 코드").optional(),
                        fieldWithPath("result.primaryPoll.totalVoteCount").type(JsonFieldType.NUMBER).description("총 투표 수").optional(),
                        fieldWithPath("result.primaryPoll.options").type(JsonFieldType.ARRAY).description("옵션 목록").optional(),
                        fieldWithPath("result.primaryPoll.options[].optionCode").type(JsonFieldType.STRING).description("옵션 코드").optional(),
                        fieldWithPath("result.primaryPoll.options[].label").type(JsonFieldType.STRING).description("옵션 라벨").optional(),
                        fieldWithPath("result.primaryPoll.options[].emoji").type(JsonFieldType.STRING).description("옵션 이모지").optional(),
                        fieldWithPath("result.primaryPoll.options[].voteCount").type(JsonFieldType.NUMBER).description("옵션 투표 수").optional(),
                        fieldWithPath("result.primaryPoll.options[].voteRatePercent").type(JsonFieldType.NUMBER).description("옵션 점유율(%)").optional(),
                        fieldWithPath("result.secondaryPoll.pollId").type(JsonFieldType.NUMBER).description("보조 투표 ID").optional(),
                        fieldWithPath("result.secondaryPoll.question").type(JsonFieldType.STRING).description("보조 투표 질문").optional(),
                        fieldWithPath("result.secondaryPoll.pollKind").type(JsonFieldType.STRING).description("보조 투표 종류").optional(),
                        fieldWithPath("result.secondaryPoll.pollContext").type(JsonFieldType.STRING).description("보조 투표 컨텍스트").optional(),
                        fieldWithPath("result.secondaryPoll.pollSlot").type(JsonFieldType.STRING).description("보조 투표 시간대").optional(),
                        fieldWithPath("result.secondaryPoll.stationId").type(JsonFieldType.NUMBER).description("역 ID").optional(),
                        fieldWithPath("result.secondaryPoll.stationName").type(JsonFieldType.STRING).description("역 이름").optional(),
                        fieldWithPath("result.secondaryPoll.subwayLineId").type(JsonFieldType.NUMBER).description("노선 ID").optional(),
                        fieldWithPath("result.secondaryPoll.subwayLineName").type(JsonFieldType.STRING).description("노선명").optional(),
                        fieldWithPath("result.secondaryPoll.isPrimary").type(JsonFieldType.BOOLEAN).description("주 투표 여부").optional(),
                        fieldWithPath("result.secondaryPoll.voted").type(JsonFieldType.BOOLEAN).description("내 투표 여부").optional(),
                        fieldWithPath("result.secondaryPoll.selectedOptionCode").type(JsonFieldType.STRING).description("내 선택 옵션 코드").optional(),
                        fieldWithPath("result.secondaryPoll.totalVoteCount").type(JsonFieldType.NUMBER).description("총 투표 수").optional(),
                        fieldWithPath("result.secondaryPoll.options").type(JsonFieldType.ARRAY).description("옵션 목록").optional(),
                        fieldWithPath("result.secondaryPoll.options[].optionCode").type(JsonFieldType.STRING).description("옵션 코드").optional(),
                        fieldWithPath("result.secondaryPoll.options[].label").type(JsonFieldType.STRING).description("옵션 라벨").optional(),
                        fieldWithPath("result.secondaryPoll.options[].emoji").type(JsonFieldType.STRING).description("옵션 이모지").optional(),
                        fieldWithPath("result.secondaryPoll.options[].voteCount").type(JsonFieldType.NUMBER).description("옵션 투표 수").optional(),
                        fieldWithPath("result.secondaryPoll.options[].voteRatePercent").type(JsonFieldType.NUMBER).description("옵션 점유율(%)").optional(),
                        fieldWithPath("result.stationDiary.pollId").type(JsonFieldType.NUMBER).description("역 일기 poll ID").optional(),
                        fieldWithPath("result.stationDiary.question").type(JsonFieldType.STRING).description("역 일기 질문").optional(),
                        fieldWithPath("result.stationDiary.stationId").type(JsonFieldType.NUMBER).description("역 ID").optional(),
                        fieldWithPath("result.stationDiary.stationName").type(JsonFieldType.STRING).description("역 이름").optional(),
                        fieldWithPath("result.stationDiary.visible").type(JsonFieldType.BOOLEAN).description("노출 여부").optional(),
                        fieldWithPath("result.stationDiary.commentCount").type(JsonFieldType.NUMBER).description("댓글 수").optional(),
                    ),
                ),
            )
    }

    @Test
    fun votePoll() {
        val response = DailyVoteDto.VoteResponse(
            poll = buildPollCard(
                pollId = 301,
                question = "오늘 2호선 등교길 어땠나요?",
                isPrimary = true,
                voted = true,
                selectedOptionCode = "LIKE",
            ),
        )

        given(dailyVoteUseCase.vote(any())).willReturn(response)

        val request = DailyVoteDto.VoteRequest(optionCode = "LIKE")

        mockMvc.perform(
            post("/v2/daily-votes/{pollId}/votes", 301)
                .header("Authorization", "Bearer <Access Token>")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)),
        )
            .andExpect(status().isOk)
            .andDo(
                document(
                    "post-daily-vote-v2",
                    getDocsRequest(),
                    getDocsResponse(),
                    requestHeaders(
                        headerWithName("Authorization").description("엑세스 토큰"),
                    ),
                    pathParameters(
                        parameterWithName("pollId").description("투표 poll ID"),
                    ),
                    requestFields(
                        fieldWithPath("optionCode").type(JsonFieldType.STRING).description("투표 옵션 코드"),
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.poll.pollId").type(JsonFieldType.NUMBER).description("투표 ID"),
                        fieldWithPath("result.poll.question").type(JsonFieldType.STRING).description("투표 질문"),
                        fieldWithPath("result.poll.pollKind").type(JsonFieldType.STRING).description("투표 종류"),
                        fieldWithPath("result.poll.pollContext").type(JsonFieldType.STRING).description("투표 컨텍스트"),
                        fieldWithPath("result.poll.pollSlot").type(JsonFieldType.STRING).description("투표 시간대"),
                        fieldWithPath("result.poll.stationId").type(JsonFieldType.NUMBER).description("역 ID"),
                        fieldWithPath("result.poll.stationName").type(JsonFieldType.STRING).description("역 이름"),
                        fieldWithPath("result.poll.subwayLineId").type(JsonFieldType.NUMBER).description("노선 ID"),
                        fieldWithPath("result.poll.subwayLineName").type(JsonFieldType.STRING).description("노선명"),
                        fieldWithPath("result.poll.isPrimary").type(JsonFieldType.BOOLEAN).description("주 투표 여부"),
                        fieldWithPath("result.poll.voted").type(JsonFieldType.BOOLEAN).description("투표 완료 여부"),
                        fieldWithPath("result.poll.selectedOptionCode").type(JsonFieldType.STRING).description("내 선택 옵션"),
                        fieldWithPath("result.poll.totalVoteCount").type(JsonFieldType.NUMBER).description("총 투표 수"),
                        fieldWithPath("result.poll.options").type(JsonFieldType.ARRAY).description("옵션 목록"),
                        fieldWithPath("result.poll.options[].optionCode").type(JsonFieldType.STRING).description("옵션 코드"),
                        fieldWithPath("result.poll.options[].label").type(JsonFieldType.STRING).description("옵션 라벨"),
                        fieldWithPath("result.poll.options[].emoji").type(JsonFieldType.STRING).description("옵션 이모지"),
                        fieldWithPath("result.poll.options[].voteCount").type(JsonFieldType.NUMBER).description("옵션 투표 수"),
                        fieldWithPath("result.poll.options[].voteRatePercent").type(JsonFieldType.NUMBER).description("옵션 점유율"),
                    ),
                ),
            )
    }

    @Test
    fun getStationPolls() {
        val response = DailyVoteDto.StationPollsResponse(
            stationId = 557,
            stationName = "안암",
            sort = "popular",
            polls = listOf(
                DailyVoteDto.StationPollSummary(
                    pollId = 901,
                    question = "안암역 환승 동선 괜찮으셨나요?",
                    pollKind = "STATION_BOARD",
                    pollContext = "COMMUTE",
                    pollSlot = "MORNING",
                    stationId = 557,
                    stationName = "안암",
                    subwayLineId = 2,
                    subwayLineName = "2호선",
                    totalVoteCount = 32,
                    commentCount = 7,
                    voted = true,
                    selectedOptionCode = "LIKE",
                    options = listOf(
                        DailyVoteDto.PollOption("LIKE", "좋아요", "👍", 25, 78),
                        DailyVoteDto.PollOption("DISLIKE", "싫어요", "👎", 7, 21),
                    ),
                    mine = true,
                    createdAt = "2026-02-26T10:15:00",
                ),
            ),
        )
        given(dailyVoteUseCase.getStationPolls(any())).willReturn(response)

        mockMvc.perform(
            get("/v2/daily-votes/stations/{stationId}/polls", 557)
                .header("Authorization", "Bearer <Access Token>")
                .queryParam("sort", "popular")
                .queryParam("limit", "30")
                .queryParam("subwayLineId", "2"),
        )
            .andExpect(status().isOk)
            .andDo(
                document(
                    "get-daily-vote-station-polls-v2",
                    getDocsRequest(),
                    getDocsResponse(),
                    requestHeaders(
                        headerWithName("Authorization").description("엑세스 토큰"),
                    ),
                    pathParameters(
                        parameterWithName("stationId").description("역 ID"),
                    ),
                    queryParameters(
                        parameterWithName("sort").optional().description("정렬(latest/popular, 기본 latest)"),
                        parameterWithName("limit").optional().description("조회 개수(기본 30, 최대 100)"),
                        parameterWithName("subwayLineId").optional().description("노선 ID 필터"),
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.stationId").type(JsonFieldType.NUMBER).description("역 ID"),
                        fieldWithPath("result.stationName").type(JsonFieldType.STRING).description("역 이름"),
                        fieldWithPath("result.sort").type(JsonFieldType.STRING).description("정렬 방식"),
                        fieldWithPath("result.polls").type(JsonFieldType.ARRAY).description("역 게시판 투표 목록"),
                        fieldWithPath("result.polls[].pollId").type(JsonFieldType.NUMBER).description("투표 ID"),
                        fieldWithPath("result.polls[].question").type(JsonFieldType.STRING).description("질문"),
                        fieldWithPath("result.polls[].pollKind").type(JsonFieldType.STRING).description("투표 종류"),
                        fieldWithPath("result.polls[].pollContext").type(JsonFieldType.STRING).description("컨텍스트"),
                        fieldWithPath("result.polls[].pollSlot").type(JsonFieldType.STRING).description("슬롯"),
                        fieldWithPath("result.polls[].stationId").type(JsonFieldType.NUMBER).description("역 ID"),
                        fieldWithPath("result.polls[].stationName").type(JsonFieldType.STRING).description("역 이름"),
                        fieldWithPath("result.polls[].subwayLineId").type(JsonFieldType.NUMBER).description("노선 ID"),
                        fieldWithPath("result.polls[].subwayLineName").type(JsonFieldType.STRING).description("노선명"),
                        fieldWithPath("result.polls[].totalVoteCount").type(JsonFieldType.NUMBER).description("총 투표 수"),
                        fieldWithPath("result.polls[].commentCount").type(JsonFieldType.NUMBER).description("댓글 수"),
                        fieldWithPath("result.polls[].voted").type(JsonFieldType.BOOLEAN).description("내 투표 여부"),
                        fieldWithPath("result.polls[].selectedOptionCode").type(JsonFieldType.STRING).description("내 선택 옵션").optional(),
                        fieldWithPath("result.polls[].options").type(JsonFieldType.ARRAY).description("옵션 목록"),
                        fieldWithPath("result.polls[].options[].optionCode").type(JsonFieldType.STRING).description("옵션 코드"),
                        fieldWithPath("result.polls[].options[].label").type(JsonFieldType.STRING).description("옵션 라벨"),
                        fieldWithPath("result.polls[].options[].emoji").type(JsonFieldType.STRING).description("옵션 이모지"),
                        fieldWithPath("result.polls[].options[].voteCount").type(JsonFieldType.NUMBER).description("옵션 투표 수"),
                        fieldWithPath("result.polls[].options[].voteRatePercent").type(JsonFieldType.NUMBER).description("옵션 비율"),
                        fieldWithPath("result.polls[].mine").type(JsonFieldType.BOOLEAN).description("내가 생성한 투표 여부"),
                        fieldWithPath("result.polls[].createdAt").type(JsonFieldType.STRING).description("생성 시각"),
                    ),
                ),
            )
    }

    @Test
    fun createStationPoll() {
        val response = DailyVoteDto.CreateStationPollResponse(pollId = 991)
        given(dailyVoteUseCase.createStationPoll(any())).willReturn(response)

        val request = DailyVoteDto.CreateStationPollRequest(
            question = "안암역 2번 출구 동선 어땠나요?",
            subwayLineId = 2,
        )

        mockMvc.perform(
            post("/v2/daily-votes/stations/{stationId}/polls", 557)
                .header("Authorization", "Bearer <Access Token>")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)),
        )
            .andExpect(status().isOk)
            .andDo(
                document(
                    "post-daily-vote-station-poll-v2",
                    getDocsRequest(),
                    getDocsResponse(),
                    requestHeaders(
                        headerWithName("Authorization").description("엑세스 토큰"),
                    ),
                    pathParameters(
                        parameterWithName("stationId").description("역 ID"),
                    ),
                    requestFields(
                        fieldWithPath("question").type(JsonFieldType.STRING).description("투표 질문"),
                        fieldWithPath("subwayLineId").type(JsonFieldType.NUMBER).optional().description("노선 ID"),
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.pollId").type(JsonFieldType.NUMBER).description("생성된 투표 ID"),
                    ),
                ),
            )
    }

    @Test
    fun deleteStationPoll() {
        val response = DailyVoteDto.DeletePollResponse(
            pollId = 991,
            status = "CLOSED",
        )
        given(dailyVoteUseCase.deletePoll(any())).willReturn(response)

        mockMvc.perform(
            delete("/v2/daily-votes/polls/{pollId}", 991)
                .header("Authorization", "Bearer <Access Token>"),
        )
            .andExpect(status().isOk)
            .andDo(
                document(
                    "delete-daily-vote-poll-v2",
                    getDocsRequest(),
                    getDocsResponse(),
                    requestHeaders(
                        headerWithName("Authorization").description("엑세스 토큰"),
                    ),
                    pathParameters(
                        parameterWithName("pollId").description("투표 ID"),
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.pollId").type(JsonFieldType.NUMBER).description("투표 ID"),
                        fieldWithPath("result.status").type(JsonFieldType.STRING).description("변경된 상태"),
                    ),
                ),
            )
    }

    @Test
    fun getVoteComments() {
        val response = DailyVoteDto.CommentsResponse(
            pollId = 301,
            sort = "popular",
            comments = listOf(
                DailyVoteDto.CommentItem(
                    commentId = 55,
                    writer = "유저A",
                    content = "오늘 진짜 지옥철...",
                    imageUrls = listOf("https://cdn.ahhachul.com/comment/reaction.gif"),
                    likeCount = 33,
                    likedByMe = true,
                    mine = false,
                    createdAt = "2026-02-25T09:23:00",
                ),
            ),
        )
        given(dailyVoteUseCase.getComments(any())).willReturn(response)

        mockMvc.perform(
            get("/v2/daily-votes/{pollId}/comments", 301)
                .header("Authorization", "Bearer <Access Token>")
                .queryParam("sort", "popular"),
        )
            .andExpect(status().isOk)
            .andDo(
                document(
                    "get-daily-vote-comments-v2",
                    getDocsRequest(),
                    getDocsResponse(),
                    requestHeaders(
                        headerWithName("Authorization").description("엑세스 토큰"),
                    ),
                    pathParameters(
                        parameterWithName("pollId").description("투표 poll ID"),
                    ),
                    queryParameters(
                        parameterWithName("sort").optional().description("정렬(latest/popular, 기본 latest)"),
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.pollId").type(JsonFieldType.NUMBER).description("투표 ID"),
                        fieldWithPath("result.sort").type(JsonFieldType.STRING).description("정렬 방식"),
                        fieldWithPath("result.comments").type(JsonFieldType.ARRAY).description("댓글 목록"),
                        fieldWithPath("result.comments[].commentId").type(JsonFieldType.NUMBER).description("댓글 ID"),
                        fieldWithPath("result.comments[].writer").type(JsonFieldType.STRING).description("작성자 닉네임"),
                        fieldWithPath("result.comments[].content").type(JsonFieldType.STRING).description("댓글 내용"),
                        fieldWithPath("result.comments[].imageUrls").type(JsonFieldType.ARRAY).description("이미지/GIF URL 목록"),
                        fieldWithPath("result.comments[].likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                        fieldWithPath("result.comments[].likedByMe").type(JsonFieldType.BOOLEAN).description("내 좋아요 여부"),
                        fieldWithPath("result.comments[].mine").type(JsonFieldType.BOOLEAN).description("내 댓글 여부"),
                        fieldWithPath("result.comments[].createdAt").type(JsonFieldType.STRING).description("작성 시각"),
                    ),
                ),
            )
    }

    @Test
    fun createVoteComment() {
        val response = DailyVoteDto.CreateCommentResponse(commentId = 77)
        given(dailyVoteUseCase.createComment(any())).willReturn(response)

        val request = DailyVoteDto.CreateCommentRequest(
            content = "오늘 환승 진짜 힘들었어요",
            imageUrls = listOf("https://cdn.ahhachul.com/comment/today.gif"),
        )

        mockMvc.perform(
            post("/v2/daily-votes/{pollId}/comments", 301)
                .header("Authorization", "Bearer <Access Token>")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)),
        )
            .andExpect(status().isOk)
            .andDo(
                document(
                    "post-daily-vote-comment-v2",
                    getDocsRequest(),
                    getDocsResponse(),
                    requestHeaders(
                        headerWithName("Authorization").description("엑세스 토큰"),
                    ),
                    pathParameters(
                        parameterWithName("pollId").description("투표 poll ID"),
                    ),
                    requestFields(
                        fieldWithPath("content").type(JsonFieldType.STRING).description("댓글 내용"),
                        fieldWithPath("imageUrls").type(JsonFieldType.ARRAY).optional().description("이미지/GIF URL 목록"),
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.commentId").type(JsonFieldType.NUMBER).description("생성된 댓글 ID"),
                    ),
                ),
            )
    }

    @Test
    fun likeVoteComment() {
        val response = DailyVoteDto.ToggleCommentLikeResponse(commentId = 77, liked = true)
        given(dailyVoteUseCase.likeComment(77)).willReturn(response)

        mockMvc.perform(
            post("/v2/daily-votes/comments/{commentId}/likes", 77)
                .header("Authorization", "Bearer <Access Token>"),
        )
            .andExpect(status().isOk)
            .andDo(
                document(
                    "post-daily-vote-comment-like-v2",
                    getDocsRequest(),
                    getDocsResponse(),
                    requestHeaders(
                        headerWithName("Authorization").description("엑세스 토큰"),
                    ),
                    pathParameters(
                        parameterWithName("commentId").description("댓글 ID"),
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.commentId").type(JsonFieldType.NUMBER).description("댓글 ID"),
                        fieldWithPath("result.liked").type(JsonFieldType.BOOLEAN).description("좋아요 상태"),
                    ),
                ),
            )
    }

    @Test
    fun unlikeVoteComment() {
        val response = DailyVoteDto.ToggleCommentLikeResponse(commentId = 77, liked = false)
        given(dailyVoteUseCase.unlikeComment(77)).willReturn(response)

        mockMvc.perform(
            delete("/v2/daily-votes/comments/{commentId}/likes", 77)
                .header("Authorization", "Bearer <Access Token>"),
        )
            .andExpect(status().isOk)
            .andDo(
                document(
                    "delete-daily-vote-comment-like-v2",
                    getDocsRequest(),
                    getDocsResponse(),
                    requestHeaders(
                        headerWithName("Authorization").description("엑세스 토큰"),
                    ),
                    pathParameters(
                        parameterWithName("commentId").description("댓글 ID"),
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.commentId").type(JsonFieldType.NUMBER).description("댓글 ID"),
                        fieldWithPath("result.liked").type(JsonFieldType.BOOLEAN).description("좋아요 상태"),
                    ),
                ),
            )
    }

    private fun buildPollCard(
        pollId: Long,
        question: String,
        isPrimary: Boolean,
        voted: Boolean = false,
        selectedOptionCode: String? = null,
    ): DailyVoteDto.PollCard {
        return DailyVoteDto.PollCard(
            pollId = pollId,
            question = question,
            pollKind = "MAIN",
            pollContext = "COMMUTE",
            pollSlot = "MORNING",
            stationId = 557,
            stationName = "안암",
            subwayLineId = 2,
            subwayLineName = "2호선",
            isPrimary = isPrimary,
            voted = voted,
            selectedOptionCode = selectedOptionCode,
            totalVoteCount = 120,
            options = listOf(
                DailyVoteDto.PollOption("LIKE", "좋아요", "👍", 72, 60),
                DailyVoteDto.PollOption("DISLIKE", "싫어요", "👎", 48, 40),
            ),
        )
    }
}
