package backend.team.ahachul_backend.api.lost.adapter.web.out

import backend.team.ahachul_backend.api.lost.application.service.command.GetRecommendLostPostsCommand
import backend.team.ahachul_backend.api.lost.application.service.command.GetSliceLostPostsCommand
import backend.team.ahachul_backend.api.lost.domain.entity.CategoryEntity
import backend.team.ahachul_backend.api.lost.domain.entity.LostPostEntity
import backend.team.ahachul_backend.api.lost.domain.entity.QLostPostEntity.lostPostEntity
import backend.team.ahachul_backend.api.lost.domain.model.LostOrigin
import backend.team.ahachul_backend.api.lost.domain.model.LostType
import backend.team.ahachul_backend.common.domain.entity.SubwayLineEntity
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import org.springframework.stereotype.Repository

@Repository
class CustomLostPostRepository(
    private val queryFactory: JPAQueryFactory
) {

    fun searchLostPosts(command: GetSliceLostPostsCommand): Slice<LostPostEntity> {
        val pageable = command.pageable
        val response = queryFactory.selectFrom(lostPostEntity)
            .where(
                lostOriginEq(command.lostOrigin),
                subwayLineEq(command.subwayLine),
                lostTypeEq(command.lostType)
            )
            .orderBy(lostPostEntity.createdAt.desc())
            .offset(getOffset(pageable).toLong())
            .limit((pageable.pageSize + 1).toLong())
            .fetch()

        return SliceImpl(response, pageable, hasNext(response, pageable))
    }

    fun searchRecommendPost(command: GetRecommendLostPostsCommand): List<LostPostEntity> {
        return queryFactory.selectFrom(lostPostEntity)
            .where(
                lostTypeEq(command.lostType),
                subwayLineEq(command.subwayLine),
                categoryEq(command.category)
            )
            .orderBy(Expressions.numberTemplate(Double::class.java, "function('rand')").asc())
            .limit(command.size)
            .fetch()
    }

    fun searchRandomPostNotEqualCategory(command: GetRecommendLostPostsCommand): List<LostPostEntity> {
        return queryFactory.selectFrom(lostPostEntity)
            .where(
                lostTypeEq(command.lostType),
                subwayLineEq(command.subwayLine),
                categoryNotEq(command.category)
            )
            .orderBy(Expressions.numberTemplate(Double::class.java, "function('rand')").asc())
            .limit(command.size)
            .fetch()
    }

    private fun getOffset(pageable: Pageable): Int {
        return when {
            pageable.pageNumber != 0 -> pageable.pageNumber * pageable.pageSize
            else -> pageable.pageNumber
        }
    }

    private fun hasNext(response: MutableList<LostPostEntity>, pageable: Pageable): Boolean {
        return when {
            response.size > pageable.pageSize -> {
                response.removeAt(response.size - 1)
                true
            }
            else -> false
        }
    }

    private fun lostOriginEq(lostOrigin: LostOrigin?) =
        lostOrigin?.let { lostPostEntity.origin.eq(lostOrigin) }

    private fun subwayLineEq(subwayLine: SubwayLineEntity?) =
        subwayLine?.let { lostPostEntity.subwayLine.eq(subwayLine) }

    private fun lostTypeEq(lostType: LostType?) =
        lostType?.let { lostPostEntity.lostType.eq(lostType) }

    private fun categoryEq(category: CategoryEntity?) =
        category?.let { lostPostEntity.category.eq(category) }

    private fun categoryNotEq(category: CategoryEntity?) =
        category?.let { lostPostEntity.category.ne(category) }
}
