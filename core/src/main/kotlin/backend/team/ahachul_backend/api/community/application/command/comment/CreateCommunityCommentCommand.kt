package backend.team.ahachul_backend.api.community.adapter.web.`in`.dto.comment

class CreateCommunityCommentCommand(
    val postId: Long,
    val upperCommentId: Long?,
    val content: String,
) {
}