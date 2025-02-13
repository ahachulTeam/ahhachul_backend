package backend.team.ahachul_backend.api.community.application.command.out

import backend.team.ahachul_backend.api.community.application.command.`in`.SearchCommunityPostCommand
import backend.team.ahachul_backend.api.community.domain.model.CommunityCategoryType
import backend.team.ahachul_backend.common.domain.entity.SubwayLineEntity
import backend.team.ahachul_backend.common.utils.PageTokenUtils
import org.springframework.data.domain.Sort
import java.time.LocalDateTime

class GetSliceCommunityPostCommand(
    val categoryType: CommunityCategoryType?,
    val subwayLine: SubwayLineEntity?,
    val content: String?,
    val hashTag: String?,
    val writer: String?,
    val sort: Sort,
    val date: LocalDateTime?,
    val communityPostId: Long?,
    val pageSize: Int
) {
    companion object {
        fun from(
            command: SearchCommunityPostCommand,
            subwayLine: SubwayLineEntity?,
        ): GetSliceCommunityPostCommand {
            val pageToken = command.pageToken?.let {
                PageTokenUtils.decodePageToken(it, listOf(LocalDateTime::class.java, Long::class.java))
            }

            return GetSliceCommunityPostCommand(
                categoryType = command.categoryType,
                subwayLine = subwayLine,
                content = command.content,
                hashTag = command.hashTag,
                writer = command.writer,
                sort = command.sort,
                date = pageToken?.get(0) as LocalDateTime?,
                communityPostId = pageToken?.get(1) as Long?,
                pageSize = command.pageSize
            )
        }
    }
}