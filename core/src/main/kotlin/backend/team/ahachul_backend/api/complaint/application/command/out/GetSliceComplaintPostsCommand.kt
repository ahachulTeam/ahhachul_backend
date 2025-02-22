package backend.team.ahachul_backend.api.complaint.application.command.out

import backend.team.ahachul_backend.api.complaint.application.command.`in`.SearchComplaintPostCommand
import backend.team.ahachul_backend.common.domain.entity.SubwayLineEntity
import backend.team.ahachul_backend.common.utils.PageTokenUtils
import java.time.LocalDateTime

class GetSliceComplaintPostsCommand(
    val keyword: String?,
    val subwayLine: SubwayLineEntity?,
    val date: LocalDateTime?,
    val complaintPostId: Long?,
    val pageSize : Int,
) {
    companion object {
        fun of(command: SearchComplaintPostCommand, subwayLine: SubwayLineEntity?): GetSliceComplaintPostsCommand {
            val pageToken = command.pageToken?.let {
                PageTokenUtils.decodePageToken(it, listOf(LocalDateTime::class.java, Long::class.java))
            }

            return GetSliceComplaintPostsCommand(
                keyword = command.keyword,
                subwayLine = subwayLine,
                date = pageToken?.get(0) as LocalDateTime?,
                complaintPostId = pageToken?.get(1) as Long?,
                pageSize = command.pageSize,
            )
        }
    }
}