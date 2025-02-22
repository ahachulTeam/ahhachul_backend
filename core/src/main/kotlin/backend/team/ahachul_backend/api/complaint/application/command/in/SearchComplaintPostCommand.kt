package backend.team.ahachul_backend.api.complaint.application.command.`in`

class SearchComplaintPostCommand(
    val subwayLineId: Long?,
    val keyword: String?,
    val pageToken: String?,
    val pageSize: Int
) {

}