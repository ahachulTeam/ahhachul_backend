package backend.team.ahachul_backend.api.lost.application.service.command.out

import backend.team.ahachul_backend.api.lost.domain.entity.CategoryEntity
import backend.team.ahachul_backend.api.lost.domain.model.LostType
import backend.team.ahachul_backend.common.domain.entity.SubwayLineEntity

data class GetRecommendLostPostsCommand(
    val size: Long,
    val lostType: LostType = LostType.ACQUIRE,
    val subwayLine: SubwayLineEntity?,
    val category: CategoryEntity
) {

    companion object {
        fun from(size: Long, subwayLine: SubwayLineEntity?, category: CategoryEntity): GetRecommendLostPostsCommand {
            return GetRecommendLostPostsCommand(
                size = size,
                subwayLine = subwayLine,
                category = category
            )
        }
    }
}
