package backend.team.ahachul_backend.api.article.application.port.out

import backend.team.ahachul_backend.api.article.domain.entity.ArticleLikeEntity
import backend.team.ahachul_backend.api.article.domain.model.ArticleType

interface ArticleLikeReader {

    fun exists(articleType: ArticleType, articleId: Long, memberId: Long): Boolean

    fun count(articleType: ArticleType, articleId: Long): Long

    fun findAllByMemberId(memberId: Long): List<ArticleLikeEntity>
}
