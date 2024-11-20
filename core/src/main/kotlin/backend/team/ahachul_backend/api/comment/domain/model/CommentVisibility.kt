package backend.team.ahachul_backend.api.comment.domain.model

enum class CommentVisibility {
    PUBLIC, PRIVATE;

    companion object {
        fun fromIsPrivate(isPrivate: Boolean?): CommentVisibility = if (isPrivate == true) PRIVATE else PUBLIC
    }
}