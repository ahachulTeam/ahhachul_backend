package backend.team.ahachul_backend.api.lost.application.service.command.out

import backend.team.ahachul_backend.api.lost.application.service.command.`in`.SearchLostPostCommand
import backend.team.ahachul_backend.api.lost.domain.entity.CategoryEntity
import backend.team.ahachul_backend.api.lost.domain.model.LostType
import backend.team.ahachul_backend.common.domain.entity.SubwayLineEntity
import backend.team.ahachul_backend.common.utils.PageTokenUtils
import java.time.LocalDateTime

class GetSliceLostPostsCommand(
    val lostType: LostType,
    val subwayLine: SubwayLineEntity?,
    val category: CategoryEntity?,
    val keyword: String?,
    val date: LocalDateTime?,
    val lostPostId: Long?,
    val pageSize : Int
) {
    companion object {
        fun from(
            command: SearchLostPostCommand, subwayLine: SubwayLineEntity?, category: CategoryEntity?
        ): GetSliceLostPostsCommand {
            val pageToken = command.pageToken?.let {
                PageTokenUtils.decodePageToken(it, listOf(LocalDateTime::class.java, Long::class.java))
            }

            return GetSliceLostPostsCommand(
                lostType = command.lostType,
                subwayLine = subwayLine,
                category = category,
                keyword = command.keyword,
                date = pageToken?.get(0) as LocalDateTime?,
                lostPostId = pageToken?.get(1) as Long?,
                pageSize = command.pageSize,
            )
        }
    }
}
