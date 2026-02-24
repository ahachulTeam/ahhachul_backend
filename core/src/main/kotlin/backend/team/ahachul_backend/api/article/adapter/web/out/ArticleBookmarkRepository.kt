package backend.team.ahachul_backend.api.article.adapter.web.out

import backend.team.ahachul_backend.api.article.domain.entity.ArticleBookmarkEntity
import backend.team.ahachul_backend.api.article.domain.model.ArticleType
import org.springframework.data.jpa.repository.JpaRepository

interface ArticleBookmarkRepository : JpaRepository<ArticleBookmarkEntity, Long> {

    fun existsByArticleTypeAndArticleIdAndMemberId(
        articleType: ArticleType,
        articleId: Long,
        memberId: Long
    ): Boolean

    fun countByArticleTypeAndArticleId(articleType: ArticleType, articleId: Long): Long

    fun deleteByArticleTypeAndArticleIdAndMemberId(
        articleType: ArticleType,
        articleId: Long,
        memberId: Long
    )

    fun findAllByMemberIdOrderByCreatedAtDesc(memberId: Long): List<ArticleBookmarkEntity>
}
