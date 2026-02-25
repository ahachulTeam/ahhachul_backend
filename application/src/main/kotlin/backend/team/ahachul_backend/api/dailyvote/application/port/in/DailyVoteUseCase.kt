package backend.team.ahachul_backend.api.dailyvote.application.port.`in`

import backend.team.ahachul_backend.api.dailyvote.adapter.`in`.dto.DailyVoteDto
import backend.team.ahachul_backend.api.dailyvote.application.port.`in`.dto.CreateDailyVoteCommentCommand
import backend.team.ahachul_backend.api.dailyvote.application.port.`in`.dto.GetDailyVoteCommentsCommand
import backend.team.ahachul_backend.api.dailyvote.application.port.`in`.dto.GetTodayDailyVoteCommand
import backend.team.ahachul_backend.api.dailyvote.application.port.`in`.dto.VoteDailyPollCommand

interface DailyVoteUseCase {

    fun getToday(command: GetTodayDailyVoteCommand): DailyVoteDto.TodayResponse

    fun vote(command: VoteDailyPollCommand): DailyVoteDto.VoteResponse

    fun getComments(command: GetDailyVoteCommentsCommand): DailyVoteDto.CommentsResponse

    fun createComment(command: CreateDailyVoteCommentCommand): DailyVoteDto.CreateCommentResponse

    fun likeComment(commentId: Long): DailyVoteDto.ToggleCommentLikeResponse

    fun unlikeComment(commentId: Long): DailyVoteDto.ToggleCommentLikeResponse
}
