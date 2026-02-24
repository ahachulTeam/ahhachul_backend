package backend.team.ahachul_backend.api.member.adapter.web.`in`.dto

import backend.team.ahachul_backend.api.article.domain.model.ArticleType

class GetArticleHistoryDto {

    data class Response(
        val likedArticles: List<ArticleHistory>,
        val bookmarkedArticles: List<ArticleHistory>
    )

    data class ArticleHistory(
        val articleType: ArticleType,
        val articleId: Long,
        val title: String,
        val contentPreview: String,
        val writer: String?,
        val subwayLineId: Long?,
        val stationId: Long?,
        val articleCreatedAt: String,
        val reactedAt: String,
    )
}
