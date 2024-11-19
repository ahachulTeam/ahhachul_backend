package backend.team.ahachul_backend.api.community.adapter.web.`in`.dto.post

import backend.team.ahachul_backend.api.community.domain.model.CommunityCategoryType
import backend.team.ahachul_backend.common.domain.model.YNType
import org.springframework.data.domain.Pageable

class SearchCommunityPostCommand(
    val categoryType: CommunityCategoryType? = null,
    val subwayLineId: Long? = null,
    val content: String? = null,
    val hashTag: String? = null,
    val hotPostYn: YNType? = null,
    val pageable: Pageable,
) {
}
