package backend.team.ahachul_backend.api.dailyvote.application.port.`in`.dto

data class GetTodayDailyVoteCommand(
    val timezone: String?,
)

data class VoteDailyPollCommand(
    val pollId: Long,
    val optionCode: String,
)

data class GetDailyVoteCommentsCommand(
    val pollId: Long,
    val sort: String?,
)

data class CreateDailyVoteCommentCommand(
    val pollId: Long,
    val content: String,
    val imageUrls: List<String>?,
)
