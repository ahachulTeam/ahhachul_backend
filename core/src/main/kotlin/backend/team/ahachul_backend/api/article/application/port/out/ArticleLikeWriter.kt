package backend.team.ahachul_backend.api.article.application.port.out

import backend.team.ahachul_backend.api.article.domain.entity.ArticleLikeEntity
import backend.team.ahachul_backend.api.article.domain.model.ArticleType

interface ArticleLikeWriter {

    fun save(entity: ArticleLikeEntity): ArticleLikeEntity

    fun delete(articleType: ArticleType, articleId: Long, memberId: Long)
}
