package backend.team.ahachul_backend.api.community.adapter.web.`in`.dto

import backend.team.ahachul_backend.api.community.domain.GetCommunityPost
import backend.team.ahachul_backend.api.community.domain.model.CommunityCategoryType
import backend.team.ahachul_backend.common.domain.model.RegionType
import backend.team.ahachul_backend.common.domain.model.YNType
import backend.team.ahachul_backend.common.dto.ImageDto
import java.time.LocalDateTime

class GetCommunityPostDto {

    data class Response(
        val id: Long,
        val title: String,
        val content: String,
        val categoryType: CommunityCategoryType,
        val hashTags: List<String>,
        val viewCnt: Int,
        val likeCnt: Int,
        val hateCnt: Int,
        val likeYn: YNType,
        val hateYn: YNType,
        val hotPostYn: YNType,
        val regionType: RegionType,
        val subwayLineId: Long,
        val createdAt: LocalDateTime,
        val createdBy: String,
        val writer: String,
        val images: List<ImageDto>
    ) {
        companion object {
            fun of(getCommunityPost: GetCommunityPost, hashTags: List<String>, views: Int, images: List<ImageDto>): Response {
                return Response(
                    id = getCommunityPost.id,
                    title = getCommunityPost.title,
                    content = getCommunityPost.content,
                    categoryType = getCommunityPost.categoryType,
                    hashTags = hashTags,
                    viewCnt = views,
                    likeCnt = getCommunityPost.likeCnt.toInt(),
                    hateCnt = getCommunityPost.hateCnt.toInt(),
                    likeYn = YNType.convert(getCommunityPost.likeYn),
                    hateYn = YNType.convert(getCommunityPost.hateYn),
                    hotPostYn = getCommunityPost.hotPostYn,
                    regionType = getCommunityPost.regionType,
                    subwayLineId = getCommunityPost.subwayLineId,
                    createdAt = getCommunityPost.createdAt,
                    createdBy = getCommunityPost.createdBy,
                    writer = getCommunityPost.writer,
                    images = images
                )
            }
        }
    }
}
