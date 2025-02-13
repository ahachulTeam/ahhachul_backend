package backend.team.ahachul_backend.api.community.application.command.`in`

import backend.team.ahachul_backend.api.community.domain.model.CommunityCategoryType
import org.springframework.data.domain.Sort

class SearchCommunityPostCommand(
    val categoryType: CommunityCategoryType?,
    val subwayLineId: Long?,
    val content: String?,
    val hashTag: String?,
    val writer: String?,
    val sort: Sort,
    val pageToken: String?,
    val pageSize: Int
) {
}
