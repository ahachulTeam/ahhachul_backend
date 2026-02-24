package backend.team.ahachul_backend.api.article.application.port.`in`

import backend.team.ahachul_backend.api.article.domain.model.ArticleType

interface ArticleBookmarkUseCase {

    fun bookmark(articleType: ArticleType, articleId: Long)

    fun unbookmark(articleType: ArticleType, articleId: Long)
}
