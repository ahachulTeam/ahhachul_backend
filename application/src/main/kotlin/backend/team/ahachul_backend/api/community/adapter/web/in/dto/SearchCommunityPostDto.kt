package backend.team.ahachul_backend.api.community.adapter.web.`in`.dto

import backend.team.ahachul_backend.api.community.application.command.`in`.SearchCommunityPostCommand
import backend.team.ahachul_backend.api.community.domain.model.CommunityCategoryType
import backend.team.ahachul_backend.common.domain.model.RegionType
import backend.team.ahachul_backend.common.domain.model.YNType
import backend.team.ahachul_backend.common.dto.ImageDto
import org.springframework.data.domain.Sort

class SearchCommunityPostDto {

    data class Request(
        val categoryType: CommunityCategoryType?,
        val subwayLineId: Long?,
        val content: String?,
        val hashTag: String?,
        val hotPostYn: YNType?,
        val writer: String?,
        val sort: String
    ) {
        fun toCommand(pageToken: String?, pageSize: Int): SearchCommunityPostCommand {
            return SearchCommunityPostCommand(
                categoryType = categoryType,
                subwayLineId = subwayLineId,
                content = content,
                hashTag = hashTag,
                hotPostYn = hotPostYn,
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

    data class Response(
        val id: Long,
        val title: String,
        val content: String,
        val categoryType: CommunityCategoryType,
        val hashTags: List<String>,
        val commentCnt: Long,
        val viewCnt: Int,
        val likeCnt: Long,
        val hotPostYn: YNType,
        val regionType: RegionType,
        val subwayLineId: Long,
        val createdAt: String,
        val createdBy: String,
        val writer: String,
        val image: ImageDto?,
    )
}
