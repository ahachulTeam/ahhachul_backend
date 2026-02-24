package backend.team.ahachul_backend.api.article.domain.entity

import backend.team.ahachul_backend.api.article.domain.model.ArticleType
import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity
import backend.team.ahachul_backend.common.domain.entity.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "tb_article_like")
class ArticleLikeEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_like_id")
    val id: Long = 0,

    @Enumerated(EnumType.STRING)
    @Column(name = "article_type")
    val articleType: ArticleType,

    @Column(name = "article_id")
    val articleId: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: MemberEntity,
) : BaseEntity() {

    companion object {
        fun of(articleType: ArticleType, articleId: Long, member: MemberEntity): ArticleLikeEntity {
            return ArticleLikeEntity(
                articleType = articleType,
                articleId = articleId,
                member = member
            )
        }
    }
}
