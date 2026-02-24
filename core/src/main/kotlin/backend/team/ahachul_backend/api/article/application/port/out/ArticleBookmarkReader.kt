package backend.team.ahachul_backend.api.article.application.port.out

import backend.team.ahachul_backend.api.article.domain.entity.ArticleBookmarkEntity
import backend.team.ahachul_backend.api.article.domain.model.ArticleType

interface ArticleBookmarkReader {

    fun exists(articleType: ArticleType, articleId: Long, memberId: Long): Boolean

    fun count(articleType: ArticleType, articleId: Long): Long

    fun findAllByMemberId(memberId: Long): List<ArticleBookmarkEntity>
}
