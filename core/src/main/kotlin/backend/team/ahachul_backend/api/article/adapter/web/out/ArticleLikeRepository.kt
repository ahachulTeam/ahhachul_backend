package backend.team.ahachul_backend.api.article.adapter.web.out

import backend.team.ahachul_backend.api.article.domain.entity.ArticleLikeEntity
import backend.team.ahachul_backend.api.article.domain.model.ArticleType
import org.springframework.data.jpa.repository.JpaRepository

interface ArticleLikeRepository : JpaRepository<ArticleLikeEntity, Long> {

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

    fun findAllByMemberIdOrderByCreatedAtDesc(memberId: Long): List<ArticleLikeEntity>
}
