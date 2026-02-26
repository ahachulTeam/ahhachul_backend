package backend.team.ahachul_backend.api.dailyvote.adapter.`in`.dto

import backend.team.ahachul_backend.api.dailyvote.application.port.`in`.dto.CreateDailyVoteCommentCommand
import backend.team.ahachul_backend.api.dailyvote.application.port.`in`.dto.CreateStationDailyVotePollCommand
import backend.team.ahachul_backend.api.dailyvote.application.port.`in`.dto.GetDailyVoteCommentsCommand
import backend.team.ahachul_backend.api.dailyvote.application.port.`in`.dto.GetStationDailyVotePollsCommand
import backend.team.ahachul_backend.api.dailyvote.application.port.`in`.dto.GetTodayDailyVoteCommand
import backend.team.ahachul_backend.api.dailyvote.application.port.`in`.dto.VoteDailyPollCommand

class DailyVoteDto {

    data class TodayResponse(
        val generatedAt: String,
        val profileHint: String,
        val primaryPoll: PollCard?,
        val secondaryPoll: PollCard?,
        val stationDiary: StationDiaryCard?,
    )

    data class PollCard(
        val pollId: Long,
        val question: String,
        val pollKind: String,
        val pollContext: String,
        val pollSlot: String,
        val stationId: Long,
        val stationName: String,
        val subwayLineId: Long,
        val subwayLineName: String,
        val isPrimary: Boolean,
        val voted: Boolean,
        val selectedOptionCode: String?,
        val totalVoteCount: Long,
        val options: List<PollOption>,
    )

    data class StationPollsRequest(
        val sort: String? = null,
        val limit: Int? = null,
        val subwayLineId: Long? = null,
    ) {
        fun toCommand(stationId: Long): GetStationDailyVotePollsCommand {
            return GetStationDailyVotePollsCommand(
                stationId = stationId,
                sort = sort,
                limit = limit,
                subwayLineId = subwayLineId,
            )
        }
    }

    data class StationPollsResponse(
        val stationId: Long,
        val stationName: String,
        val sort: String,
        val polls: List<StationPollSummary>,
    )

    data class StationPollSummary(
        val pollId: Long,
        val question: String,
        val pollKind: String,
        val pollContext: String,
        val pollSlot: String,
        val stationId: Long,
        val stationName: String,
        val subwayLineId: Long,
        val subwayLineName: String,
        val totalVoteCount: Long,
        val commentCount: Long,
        val voted: Boolean,
        val selectedOptionCode: String?,
        val options: List<PollOption>,
        val mine: Boolean,
        val createdAt: String,
    )

    data class PollOption(
        val optionCode: String,
        val label: String,
        val emoji: String,
        val voteCount: Long,
        val voteRatePercent: Int,
    )

    data class StationDiaryCard(
        val pollId: Long,
        val question: String,
        val stationId: Long,
        val stationName: String,
        val visible: Boolean,
        val commentCount: Long,
    )

    data class VoteRequest(
        val optionCode: String,
    ) {
        fun toCommand(pollId: Long): VoteDailyPollCommand {
            return VoteDailyPollCommand(
                pollId = pollId,
                optionCode = optionCode,
            )
        }
    }

    data class VoteResponse(
        val poll: PollCard,
    )

    data class CreateStationPollRequest(
        val question: String,
        val subwayLineId: Long? = null,
    ) {
        fun toCommand(stationId: Long): CreateStationDailyVotePollCommand {
            return CreateStationDailyVotePollCommand(
                stationId = stationId,
                question = question,
                subwayLineId = subwayLineId,
            )
        }
    }

    data class CreateStationPollResponse(
        val pollId: Long,
    )

    data class DeletePollResponse(
        val pollId: Long,
        val status: String,
    )

    data class GetCommentsRequest(
        val sort: String? = null,
    ) {
        fun toCommand(pollId: Long): GetDailyVoteCommentsCommand {
            return GetDailyVoteCommentsCommand(
                pollId = pollId,
                sort = sort,
            )
        }
    }

    data class CommentsResponse(
        val pollId: Long,
        val sort: String,
        val comments: List<CommentItem>,
    )

    data class CommentItem(
        val commentId: Long,
        val writer: String,
        val content: String,
        val imageUrls: List<String>,
        val likeCount: Long,
        val likedByMe: Boolean,
        val mine: Boolean,
        val createdAt: String,
    )

    data class CreateCommentRequest(
        val content: String,
        val imageUrls: List<String>? = null,
    ) {
        fun toCommand(pollId: Long): CreateDailyVoteCommentCommand {
            return CreateDailyVoteCommentCommand(
                pollId = pollId,
                content = content,
                imageUrls = imageUrls,
            )
        }
    }

    data class CreateCommentResponse(
        val commentId: Long,
    )

    data class ToggleCommentLikeResponse(
        val commentId: Long,
        val liked: Boolean,
    )

    data class TodayRequest(
        val timezone: String? = null,
    ) {
        fun toCommand(): GetTodayDailyVoteCommand {
            return GetTodayDailyVoteCommand(timezone = timezone)
        }
    }

}
