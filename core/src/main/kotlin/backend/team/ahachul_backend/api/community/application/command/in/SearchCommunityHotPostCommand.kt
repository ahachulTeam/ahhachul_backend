package backend.team.ahachul_backend.api.community.application.command.`in`

import org.springframework.data.domain.Sort

class SearchCommunityHotPostCommand(
    val subwayLineId: Long?,
    val content: String?,
    val hashTag: String?,
    val writer: String?,
    val sort: Sort,
    val pageToken: String?,
    val pageSize: Int
) {
}
