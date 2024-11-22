package backend.team.ahachul_backend.api.comment.domain.model

enum class CommentVisibility(
    val isPrivate: Boolean,
) {
    PUBLIC(false), PRIVATE(true);

    companion object {
        fun from(isPrivate: Boolean?): CommentVisibility {
            return values().firstOrNull { it.isPrivate == isPrivate } ?: PUBLIC
        }
    }
}