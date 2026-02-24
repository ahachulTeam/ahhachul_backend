package backend.team.ahachul_backend.api.complaint.application.command.`in`

class SearchComplaintPostCommand(
    val subwayLineIds: List<Long>?,
    val stationId: Long?,
    val keyword: String?,
    val pageToken: String?,
    val pageSize: Int
) {

}
