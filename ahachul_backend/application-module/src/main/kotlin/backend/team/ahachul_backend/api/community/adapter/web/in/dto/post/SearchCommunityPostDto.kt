package backend.team.ahachul_backend.api.community.adapter.web.`in`.dto.post

import backend.team.ahachul_backend.api.community.domain.SearchCommunityPost
import backend.team.ahachul_backend.api.community.domain.model.CommunityCategoryType
import backend.team.ahachul_backend.common.domain.model.RegionType
import backend.team.ahachul_backend.common.domain.model.YNType
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime

class SearchCommunityPostDto {

    data class Request(
        val categoryType: CommunityCategoryType?,
        val subwayLineId: Long?,
        val content: String?,
        val hashTag: String?,
        val hotPostYn: YNType?,
    ) {
        fun toCommand(pageable: Pageable): SearchCommunityPostCommand {
            return SearchCommunityPostCommand(
                categoryType = categoryType,
                subwayLineId = subwayLineId,
                content = content,
                hashTag = hashTag,
                hotPostYn = hotPostYn,
                pageable = pageable
            )
        }
    }

    data class Response(
        val hasNext: Boolean,
        val nextPageNum: Int?,
        val posts: List<CommunityPost>,
    ) {
        companion object {
            fun of(hasNext: Boolean, posts: List<CommunityPost>, currentPageNum: Int): Response {
                return Response(
                    hasNext = hasNext,
                    nextPageNum = if (hasNext) currentPageNum + 1 else null,
                    posts = posts
                )
            }
        }
    }

    data class CommunityPost(
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
        val createdAt: LocalDateTime,
        val createdBy: String,
        val writer: String,
        val image: ImageDto?,
    ) {
        companion object {
            fun of(searchCommunityPost: SearchCommunityPost, image: ImageDto?, views: Int, hashTags: List<String>): CommunityPost {
                return CommunityPost(
                    id = searchCommunityPost.id,
                    title = searchCommunityPost.title,
                    content = searchCommunityPost.content,
                    categoryType = searchCommunityPost.categoryType,
                    hashTags = hashTags,
                    commentCnt = searchCommunityPost.commentCnt,
                    viewCnt = views,
                    likeCnt = searchCommunityPost.likeCnt,
                    hotPostYn = searchCommunityPost.hotPostYn,
                    regionType = searchCommunityPost.regionType,
                    subwayLineId = searchCommunityPost.subwayLineId,
                    createdAt = searchCommunityPost.createdAt,
                    createdBy = searchCommunityPost.createdBy,
                    writer = searchCommunityPost.writer,
                    image = image,
                )
            }
        }
    }
}
