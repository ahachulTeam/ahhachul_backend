package backend.team.ahachul_backend.api.dailyvote.adapter.`in`

import backend.team.ahachul_backend.api.dailyvote.adapter.`in`.dto.DailyVoteDto
import backend.team.ahachul_backend.api.dailyvote.application.port.`in`.DailyVoteUseCase
import backend.team.ahachul_backend.api.dailyvote.application.port.`in`.dto.DeleteDailyVotePollCommand
import backend.team.ahachul_backend.common.annotation.Authentication
import backend.team.ahachul_backend.common.response.CommonResponse
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v2/daily-votes")
class DailyVoteController(
    private val dailyVoteUseCase: DailyVoteUseCase,
) {

    @Authentication
    @GetMapping("/today")
    fun getToday(request: DailyVoteDto.TodayRequest): CommonResponse<DailyVoteDto.TodayResponse> {
        return CommonResponse.success(dailyVoteUseCase.getToday(request.toCommand()))
    }

    @Authentication
    @PostMapping("/{pollId}/votes")
    fun vote(
        @PathVariable pollId: Long,
        @RequestBody request: DailyVoteDto.VoteRequest,
    ): CommonResponse<DailyVoteDto.VoteResponse> {
        return CommonResponse.success(dailyVoteUseCase.vote(request.toCommand(pollId)))
    }

    @Authentication
    @GetMapping("/stations/{stationId}/polls")
    fun getStationPolls(
        @PathVariable stationId: Long,
        request: DailyVoteDto.StationPollsRequest,
    ): CommonResponse<DailyVoteDto.StationPollsResponse> {
        return CommonResponse.success(dailyVoteUseCase.getStationPolls(request.toCommand(stationId)))
    }

    @Authentication
    @PostMapping("/stations/{stationId}/polls")
    fun createStationPoll(
        @PathVariable stationId: Long,
        @RequestBody request: DailyVoteDto.CreateStationPollRequest,
    ): CommonResponse<DailyVoteDto.CreateStationPollResponse> {
        return CommonResponse.success(dailyVoteUseCase.createStationPoll(request.toCommand(stationId)))
    }

    @Authentication
    @DeleteMapping("/polls/{pollId}")
    fun deletePoll(@PathVariable pollId: Long): CommonResponse<DailyVoteDto.DeletePollResponse> {
        return CommonResponse.success(
            dailyVoteUseCase.deletePoll(DeleteDailyVotePollCommand(pollId = pollId)),
        )
    }

    @Authentication
    @GetMapping("/{pollId}/comments")
    fun getComments(
        @PathVariable pollId: Long,
        request: DailyVoteDto.GetCommentsRequest,
    ): CommonResponse<DailyVoteDto.CommentsResponse> {
        return CommonResponse.success(dailyVoteUseCase.getComments(request.toCommand(pollId)))
    }

    @Authentication
    @PostMapping("/{pollId}/comments")
    fun createComment(
        @PathVariable pollId: Long,
        @RequestBody request: DailyVoteDto.CreateCommentRequest,
    ): CommonResponse<DailyVoteDto.CreateCommentResponse> {
        return CommonResponse.success(dailyVoteUseCase.createComment(request.toCommand(pollId)))
    }

    @Authentication
    @PostMapping("/comments/{commentId}/likes")
    fun likeComment(@PathVariable commentId: Long): CommonResponse<DailyVoteDto.ToggleCommentLikeResponse> {
        return CommonResponse.success(dailyVoteUseCase.likeComment(commentId))
    }

    @Authentication
    @DeleteMapping("/comments/{commentId}/likes")
    fun unlikeComment(@PathVariable commentId: Long): CommonResponse<DailyVoteDto.ToggleCommentLikeResponse> {
        return CommonResponse.success(dailyVoteUseCase.unlikeComment(commentId))
    }
}
