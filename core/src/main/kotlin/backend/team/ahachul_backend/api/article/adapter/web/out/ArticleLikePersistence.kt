package backend.team.ahachul_backend.api.article.adapter.web.out

import backend.team.ahachul_backend.api.article.application.port.out.ArticleLikeReader
import backend.team.ahachul_backend.api.article.application.port.out.ArticleLikeWriter
import backend.team.ahachul_backend.api.article.domain.entity.ArticleLikeEntity
import backend.team.ahachul_backend.api.article.domain.model.ArticleType
import org.springframework.stereotype.Component

@Component
class ArticleLikePersistence(
    private val repository: ArticleLikeRepository
) : ArticleLikeReader, ArticleLikeWriter {

    override fun save(entity: ArticleLikeEntity): ArticleLikeEntity {
        return repository.save(entity)
    }

    override fun delete(articleType: ArticleType, articleId: Long, memberId: Long) {
        repository.deleteByArticleTypeAndArticleIdAndMemberId(articleType, articleId, memberId)
    }

    override fun exists(articleType: ArticleType, articleId: Long, memberId: Long): Boolean {
        return repository.existsByArticleTypeAndArticleIdAndMemberId(articleType, articleId, memberId)
    }

    override fun count(articleType: ArticleType, articleId: Long): Long {
        return repository.countByArticleTypeAndArticleId(articleType, articleId)
    }

    override fun findAllByMemberId(memberId: Long): List<ArticleLikeEntity> {
        return repository.findAllByMemberIdOrderByCreatedAtDesc(memberId)
    }
}
