package backend.team.ahachul_backend.api.community.adapter.web.`in`.dto

import backend.team.ahachul_backend.api.community.application.command.`in`.SearchCommunityHotPostCommand
import org.springframework.data.domain.Sort

class SearchCommunityHotPostDto {

    data class Request(
        val subwayLineId: Long?,
        val content: String?,
        val hashTag: String?,
        val writer: String?,
        val sort: String
    ) {
        fun toCommand(pageToken: String?, pageSize: Int): SearchCommunityHotPostCommand {
            return SearchCommunityHotPostCommand(
                subwayLineId = subwayLineId,
                content = content,
                hashTag = hashTag,
                writer = writer,
                sort = toSort(),
                pageToken = pageToken,
                pageSize = pageSize
            )
        }

        private fun toSort(): Sort {
            val parts = sort.split(",")
            return Sort.by(Sort.Direction.fromString(parts[1]), parts[0])
        }
    }
}
