package backend.team.ahachul_backend.api.complaint.adapter.out

import backend.team.ahachul_backend.api.complaint.application.command.out.GetSliceComplaintPostsCommand
import backend.team.ahachul_backend.api.complaint.domain.entity.ComplaintPostEntity
import backend.team.ahachul_backend.api.complaint.domain.entity.QComplaintPostEntity.complaintPostEntity
import backend.team.ahachul_backend.api.complaint.domain.model.ComplaintPostType
import backend.team.ahachul_backend.common.domain.entity.SubwayLineEntity
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class CustomComplaintPostRepository(
    private val queryFactory: JPAQueryFactory,
) {

    fun searchComplaintPosts(command: GetSliceComplaintPostsCommand): List<ComplaintPostEntity> {
        val orderSpecifier = listOf(complaintPostEntity.createdAt.desc(), complaintPostEntity.id.asc())

        return queryFactory
            .selectFrom(complaintPostEntity)
            .where(
                subwayLineEq(command.subwayLine),
                contentLike(command.keyword),
                createdAtBeforeOrEqual(
                    command.date,
                    command.complaintPostId
                ),
                statusNotDeleteEq()
            )
            .orderBy(*orderSpecifier.toTypedArray())
            .limit((command.pageSize + 1).toLong())
            .fetch()
    }

    private fun subwayLineEq(subwayLine: SubwayLineEntity?) =
        subwayLine?.let { complaintPostEntity.subwayLine.eq(subwayLine) }

    private fun contentLike(keyword: String?) =
        keyword?.let { complaintPostEntity.content.contains(keyword) }

    private fun statusNotDeleteEq() =
        complaintPostEntity.status.ne(ComplaintPostType.DELETED)

    private fun createdAtBeforeOrEqual(localDateTime: LocalDateTime?, id: Long?) =
        localDateTime?.let { date ->
            id?.let { postId ->
                complaintPostEntity.createdAt.lt(date).or(
                    complaintPostEntity.createdAt.eq(date).and(complaintPostEntity.id.lt(postId))
                )
            }
        }
}