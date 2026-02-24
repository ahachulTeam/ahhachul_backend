package backend.team.ahachul_backend.api.community.application.command.`in`

import org.springframework.data.domain.Sort

class SearchCommunityHotPostCommand(
    val subwayLineIds: List<Long>?,
    val stationId: Long? = null,
    val content: String?,
    val hashTag: String?,
    val writer: String?,
    val sort: Sort,
    val pageToken: String?,
    val pageSize: Int
) {
}
