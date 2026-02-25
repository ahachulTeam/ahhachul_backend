package backend.team.ahachul_backend.api.foreigner.application.port.`in`.dto

data class TranslateCommunityPostCommand(
    val postId: Long,
    val targetLocale: ForeignerLocale,
)
