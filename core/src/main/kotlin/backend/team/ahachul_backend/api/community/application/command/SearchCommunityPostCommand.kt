package backend.team.ahachul_backend.api.community.adapter.web.`in`.dto.post

import backend.team.ahachul_backend.api.community.domain.model.CommunityCategoryType
import backend.team.ahachul_backend.common.domain.model.YNType
import org.springframework.data.domain.Sort

class SearchCommunityPostCommand(
    val categoryType: CommunityCategoryType?,
    val subwayLineId: Long?,
    val content: String?,
    val hashTag: String?,
    val hotPostYn: YNType?,
    val writer: String?,
    val sort: Sort,
    val pageToken: String?,
    val pageSize: Int
) {
}
