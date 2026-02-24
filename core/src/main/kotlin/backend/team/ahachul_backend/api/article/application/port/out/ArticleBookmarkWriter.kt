package backend.team.ahachul_backend.api.article.application.port.out

import backend.team.ahachul_backend.api.article.domain.entity.ArticleBookmarkEntity
import backend.team.ahachul_backend.api.article.domain.model.ArticleType

interface ArticleBookmarkWriter {

    fun save(entity: ArticleBookmarkEntity): ArticleBookmarkEntity

    fun delete(articleType: ArticleType, articleId: Long, memberId: Long)
}
