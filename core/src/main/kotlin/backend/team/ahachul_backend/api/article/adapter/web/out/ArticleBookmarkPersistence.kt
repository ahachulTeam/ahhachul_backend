package backend.team.ahachul_backend.api.article.adapter.web.out

import backend.team.ahachul_backend.api.article.application.port.out.ArticleBookmarkReader
import backend.team.ahachul_backend.api.article.application.port.out.ArticleBookmarkWriter
import backend.team.ahachul_backend.api.article.domain.entity.ArticleBookmarkEntity
import backend.team.ahachul_backend.api.article.domain.model.ArticleType
import org.springframework.stereotype.Component

@Component
class ArticleBookmarkPersistence(
    private val repository: ArticleBookmarkRepository
) : ArticleBookmarkReader, ArticleBookmarkWriter {

    override fun save(entity: ArticleBookmarkEntity): ArticleBookmarkEntity {
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

    override fun findAllByMemberId(memberId: Long): List<ArticleBookmarkEntity> {
        return repository.findAllByMemberIdOrderByCreatedAtDesc(memberId)
    }
}
