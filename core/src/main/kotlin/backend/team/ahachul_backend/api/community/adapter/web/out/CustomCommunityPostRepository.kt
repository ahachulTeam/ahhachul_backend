package backend.team.ahachul_backend.api.community.adapter.web.out

import backend.team.ahachul_backend.api.community.domain.GetCommunityPost
import backend.team.ahachul_backend.api.community.domain.SearchCommunityPost
import backend.team.ahachul_backend.api.comment.domain.entity.QCommentEntity.commentEntity
import backend.team.ahachul_backend.api.community.application.command.out.GetSliceCommunityHotPostCommand
import backend.team.ahachul_backend.api.community.application.command.out.GetSliceCommunityPostCommand
import backend.team.ahachul_backend.api.community.domain.entity.QCommunityPostEntity.communityPostEntity
import backend.team.ahachul_backend.api.community.domain.entity.QCommunityPostHashTagEntity.communityPostHashTagEntity
import backend.team.ahachul_backend.api.community.domain.entity.QCommunityPostLikeEntity.communityPostLikeEntity
import backend.team.ahachul_backend.api.community.domain.model.CommunityCategoryType
import backend.team.ahachul_backend.api.member.domain.entity.QMemberEntity.memberEntity
import backend.team.ahachul_backend.common.domain.entity.QHashTagEntity.hashTagEntity
import backend.team.ahachul_backend.common.domain.entity.QSubwayLineEntity.subwayLineEntity
import backend.team.ahachul_backend.common.domain.entity.SubwayLineEntity
import backend.team.ahachul_backend.common.domain.model.YNType
import com.querydsl.core.types.ConstructorExpression
import com.querydsl.core.types.ExpressionUtils
import com.querydsl.core.types.ExpressionUtils.count
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class CustomCommunityPostRepository(
    private val queryFactory: JPAQueryFactory
) {

    companion object {
        const val HOT_POST_LIMIT_DAYS = 3L
    }

    fun getByCustom(postId: Long, memberId: String?): GetCommunityPost? {
        return queryFactory.select(
            Projections.constructor(
                GetCommunityPost::class.java,
                communityPostEntity.id,
                communityPostEntity.title,
                communityPostEntity.content,
                communityPostEntity.categoryType,
                ExpressionUtils.`as`(
                    JPAExpressions.select(count(communityPostLikeEntity.id))
                        .from(communityPostLikeEntity)
                        .where(
                            communityPostLikeEntity.communityPost.id.eq(postId)
                                .and(communityPostLikeEntity.likeYn.eq(YNType.Y))
                        ),
                    "likeCnt"
                ),
                ExpressionUtils.`as`(
                    JPAExpressions.select(count(communityPostLikeEntity.id))
                        .from(communityPostLikeEntity)
                        .where(
                            communityPostLikeEntity.communityPost.id.eq(postId)
                                .and(communityPostLikeEntity.likeYn.eq(YNType.N))
                        ),
                    "hateCnt"
                ),
                if (memberId != null) {
                    ExpressionUtils.`as`(
                        JPAExpressions.select(JPAExpressions.selectOne())
                            .from(communityPostLikeEntity)
                            .where(
                                communityPostLikeEntity.communityPost.id.eq(postId)
                                    .and(communityPostLikeEntity.likeYn.eq(YNType.Y))
                                    .and(communityPostLikeEntity.member.id.eq(memberId.toLong()))
                            )
                            .exists(),
                        "likeYn"
                    )
                } else {
                    Expressions.constant(false)
                },
                if (memberId != null) {
                    ExpressionUtils.`as`(
                        JPAExpressions.select(JPAExpressions.selectOne())
                            .from(communityPostLikeEntity)
                            .where(
                                communityPostLikeEntity.communityPost.id.eq(postId)
                                    .and(communityPostLikeEntity.likeYn.eq(YNType.N))
                                    .and(communityPostLikeEntity.member.id.eq(memberId.toLong()))
                            )
                            .exists(),
                        "hateYn"
                    )
                } else {
                    Expressions.constant(false)
                },
                communityPostEntity.hotPostYn,
                communityPostEntity.regionType,
                communityPostEntity.subwayLineEntity.id,
                communityPostEntity.createdAt,
                communityPostEntity.createdBy,
                communityPostEntity.member.nickname.`as`("writer"),
            )
        )
            .from(communityPostEntity)
            .join(communityPostEntity.member, memberEntity)
            .join(communityPostEntity.subwayLineEntity, subwayLineEntity)
            .where(communityPostEntity.id.eq(postId))
            .fetchOne()
    }

    fun searchCommunityPosts(command: GetSliceCommunityPostCommand): List<SearchCommunityPost> {
        val orderSpecifier = getOrder(command.sort)

        return queryFactory.select(searchCommunityPostSimpleConstructorExpression())
            .from(communityPostEntity)
            .join(communityPostEntity.member, memberEntity)
            .join(communityPostEntity.subwayLineEntity, subwayLineEntity)
            .where(
                categoryTypeEq(command.categoryType),
                subwayLineEq(command.subwayLine),
                hashTagEqWithSubQuery(command.hashTag),
                titleOrContentContains(command.content),
                hotPostYnEq(command.hotPostYn),
                writerEq(command.writer),
                createdAtBeforeOrEqual(
                    command.date,
                    command.communityPostId
                )
            )
            .orderBy(orderSpecifier)
            .limit((command.pageSize + 1).toLong())
            .fetch()
    }

    fun searchCommunityHotPosts(command: GetSliceCommunityHotPostCommand): List<SearchCommunityPost> {
        val orderSpecifier = getOrder(command.sort)

        return queryFactory.select(searchCommunityPostSimpleConstructorExpression())
            .from(communityPostEntity)
            .join(communityPostEntity.member, memberEntity)
            .join(communityPostEntity.subwayLineEntity, subwayLineEntity)
            .where(
                hotPost(),
                subwayLineEq(command.subwayLine),
                hashTagEqWithSubQuery(command.hashTag),
                titleOrContentContains(command.content),
                writerEq(command.writer),
                createdAtBeforeOrEqual(
                    command.date,
                    command.communityPostId
                )
            )
            .orderBy(orderSpecifier)
            .limit((command.pageSize + 1).toLong())
            .fetch()
    }

    private fun getOrder(sort: Sort): OrderSpecifier<*>? {
        if (sort.isUnsorted) return communityPostEntity.createdAt.desc()

        val property = sort.toList()[0].property
        val direction = sort.toList()[0].direction
        val path = when (property) {
            "likes" -> Expressions.numberPath(Long::class.java, "likeCnt")
            "createdAt" -> communityPostEntity.createdAt
            "views" -> communityPostEntity.views
            else -> communityPostEntity.createdAt
        }
        return if (direction.isAscending) path.asc() else path.desc()
    }

    private fun categoryTypeEq(categoryType: CommunityCategoryType?) =
        categoryType?.let { communityPostEntity.categoryType.eq(categoryType) }

    private fun subwayLineEq(subwayLine: SubwayLineEntity?) =
        subwayLine?.let { communityPostEntity.subwayLineEntity.eq(subwayLine) }

    private fun hotPostYnEq(hotPostYn: YNType?) =
        hotPostYn?.let { communityPostEntity.hotPostYn.eq(hotPostYn)
            .and(communityPostEntity.hotPostSelectedDate.after(LocalDateTime.now().minusDays(HOT_POST_LIMIT_DAYS))) }

    private fun hotPost() = hotPostYnEq(YNType.Y)

    private fun hashTagEqWithSubQuery(hashTag: String?) =
        hashTag?.let {
            communityPostEntity.`in`(
                JPAExpressions.select(communityPostHashTagEntity.communityPost)
                    .from(communityPostHashTagEntity)
                    .join(communityPostHashTagEntity.hashTag, hashTagEntity)
                    .where(hashTagEntity.name.eq(hashTag))
            )
        }

    private fun titleOrContentContains(content: String?) =
        content?.let { communityPostEntity.title.contains(content).or(communityPostEntity.content.contains(content)) }

    private fun writerEq(writer: String?) =
        writer?.let { communityPostEntity.member.nickname.eq(writer) }

    private fun createdAtBeforeOrEqual(localDateTime: LocalDateTime?, id: Long?) =
        localDateTime?.let { date ->
            id?.let { communityPostId ->
                communityPostEntity.createdAt.lt(date).or(
                    communityPostEntity.createdAt.eq(date).and(communityPostEntity.id.lt(communityPostId))
                )
            }
        }

    private fun searchCommunityPostSimpleConstructorExpression(): ConstructorExpression<SearchCommunityPost>? =
        Projections.constructor(
            SearchCommunityPost::class.java,
            communityPostEntity.id,
            communityPostEntity.title,
            communityPostEntity.content,
            communityPostEntity.categoryType,
            communityPostEntity.regionType,
            communityPostEntity.subwayLineEntity.id,
            ExpressionUtils.`as`(
                JPAExpressions.select(count(communityPostLikeEntity.id))
                    .from(communityPostLikeEntity)
                    .where(
                        communityPostLikeEntity.communityPost.id.eq(communityPostEntity.id)
                            .and(communityPostLikeEntity.likeYn.eq(YNType.Y))
                    ),
                "likeCnt"
            ),
            JPAExpressions.select(commentEntity.id.count())
                .from(commentEntity)
                .where(
                    commentEntity.communityPost.id.eq(communityPostEntity.id)
                ),
            communityPostEntity.hotPostYn,
            communityPostEntity.createdAt,
            communityPostEntity.createdBy,
            communityPostEntity.member.nickname,
        )
}
