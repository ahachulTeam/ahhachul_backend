package backend.team.ahachul_backend.api.member.adapter.web.`in`.dto

import backend.team.ahachul_backend.api.article.domain.model.ArticleType
import backend.team.ahachul_backend.api.member.domain.model.GenderType

class GetMemberProfileDto {
    data class Response(
        val memberId: Long,
        val nickname: String?,
        val imageUrl: String?,
        val email: String?,
        val maskedEmail: String?,
        val gender: GenderType?,
        val ageRange: String?,
        val isMine: Boolean,
        val visibility: Visibility,
        val activities: Activities,
    )

    data class Visibility(
        val profilePublic: Boolean,
        val emailPublic: Boolean,
        val genderAgePublic: Boolean,
        val postsPublic: Boolean,
        val commentsPublic: Boolean,
        val profileVisible: Boolean,
        val postsVisible: Boolean,
        val commentsVisible: Boolean,
    )

    data class Activities(
        val posts: List<PostActivity>,
        val comments: List<CommentActivity>,
    )

    data class PostActivity(
        val articleType: ArticleType,
        val articleId: Long,
        val title: String,
        val contentPreview: String,
        val writer: String?,
        val subwayLineId: Long?,
        val stationId: Long?,
        val createdAt: String,
    )

    data class CommentActivity(
        val commentId: Long,
        val articleType: ArticleType,
        val articleId: Long,
        val contentPreview: String,
        val writer: String?,
        val createdAt: String,
    )
}
