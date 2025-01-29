package backend.team.ahachul_backend.api.comment.adapter.web.out

import backend.team.ahachul_backend.api.comment.application.command.GetCommentsCommand
import backend.team.ahachul_backend.api.comment.domain.SearchComment
import backend.team.ahachul_backend.api.comment.domain.entity.QCommentEntity.commentEntity
import backend.team.ahachul_backend.api.comment.domain.entity.QCommentLikeEntity.commentLikeEntity
import backend.team.ahachul_backend.api.comment.domain.model.PostType
import backend.team.ahachul_backend.api.member.domain.entity.QMemberEntity.memberEntity
import backend.team.ahachul_backend.common.domain.model.YNType
import com.querydsl.core.types.ExpressionUtils
import com.querydsl.core.types.ExpressionUtils.count
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Repository

@Repository
class CustomCommentRepository(
    private val queryFactory: JPAQueryFactory
) {

    fun searchComments(command: GetCommentsCommand): List<SearchComment> {
        val orderSpecifier = getOrder(command.sort)

        return queryFactory.select(
            Projections.constructor(
                SearchComment::class.java,
                commentEntity.id,
                commentEntity.upperComment,
                commentEntity.content,
                commentEntity.status,
                commentEntity.createdAt,
                commentEntity.createdBy,
                commentEntity.member,
                commentEntity.visibility,
                ExpressionUtils.`as`(
                    JPAExpressions.select(count(commentLikeEntity.id))
                        .from(commentLikeEntity)
                        .where(
                            commentLikeEntity.comment.id.eq(commentEntity.id)
                                .and(commentLikeEntity.likeYn.eq(YNType.Y))
                        ),
                    "likeCnt"
                )
            )
        )
            .from(commentEntity)
            .join(commentEntity.member, memberEntity)
            .leftJoin(commentEntity.upperComment)
            .where(
                postIdEq(command.postType, command.postId)
            )
            .orderBy(orderSpecifier)
            .fetch()

    }

    private fun getOrder(sort: Sort): OrderSpecifier<*>? {
        if (sort.isUnsorted) return commentEntity.createdAt.asc()

        val property = sort.toList()[0].property
        val direction = sort.toList()[0].direction

        val path = when (property) {
            "likes" -> Expressions.numberPath(Long::class.java, "likeCnt")
            "createdAt" -> commentEntity.createdAt
            else -> commentEntity.createdAt
        }

        return if (direction.isAscending) path.asc() else path.desc()
    }

    private fun postIdEq(postType: PostType, postId: Long) =
        when (postType) {
            PostType.COMMUNITY -> commentEntity.communityPost.id.eq(postId)
            PostType.LOST -> commentEntity.lostPost.id.eq(postId)
            PostType.COMPLAINT -> commentEntity.complaintPost.id.eq(postId)
        }
}