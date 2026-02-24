package backend.team.ahachul_backend.api.article.application.port.`in`

import backend.team.ahachul_backend.api.article.domain.model.ArticleType

interface ArticleLikeUseCase {

    fun like(articleType: ArticleType, articleId: Long)

    fun unlike(articleType: ArticleType, articleId: Long)
}
