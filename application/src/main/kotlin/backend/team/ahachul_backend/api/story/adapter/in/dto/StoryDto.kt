package backend.team.ahachul_backend.api.story.adapter.`in`.dto

import backend.team.ahachul_backend.api.story.domain.entity.StoryEntity
import backend.team.ahachul_backend.api.story.application.port.`in`.dto.CreateStoryCommand
import backend.team.ahachul_backend.api.story.application.port.`in`.dto.GetMemberStoriesCommand
import org.springframework.web.multipart.MultipartFile

class StoryDto {
    data class CreateRequest(
        val caption: String? = null,
        val stationId: Long? = null,
        val subwayLineId: Long? = null,
    ) {
        fun toCommand(imageFile: MultipartFile): CreateStoryCommand {
            return CreateStoryCommand(
                caption = caption,
                stationId = stationId,
                subwayLineId = subwayLineId,
                imageFile = imageFile,
            )
        }
    }

    data class StoryItem(
        val storyId: Long,
        val imageUrl: String,
        val caption: String?,
        val stationId: Long?,
        val stationName: String?,
        val subwayLineId: Long?,
        val subwayLineName: String?,
        val createdAt: String,
    ) {
        companion object {
            fun from(entity: StoryEntity): StoryItem {
                return StoryItem(
                    storyId = entity.id,
                    imageUrl = entity.imageUrl,
                    caption = entity.caption,
                    stationId = entity.station?.id,
                    stationName = entity.station?.name,
                    subwayLineId = entity.subwayLine?.id,
                    subwayLineName = entity.subwayLine?.name,
                    createdAt = entity.createdAt.toString(),
                )
            }
        }
    }

    data class ProfileStoriesResponse(
        val generatedAt: String,
        val memberId: Long,
        val nickname: String?,
        val isMine: Boolean,
        val storiesVisible: Boolean,
        val stories: List<StoryItem>,
    )

    data class CreateResponse(
        val story: StoryItem,
    ) {
        companion object {
            fun of(entity: StoryEntity): CreateResponse {
                return CreateResponse(
                    story = StoryItem.from(entity),
                )
            }
        }
    }

    data class DeleteResponse(
        val storyId: Long,
        val deleted: Boolean,
    )

    data class MemberStoriesRequest(
        val asPublic: Boolean = false,
        val limit: Int = 24,
    ) {
        fun toCommand(nickname: String): GetMemberStoriesCommand {
            return GetMemberStoriesCommand(
                nickname = nickname,
                asPublic = asPublic,
                limit = limit,
            )
        }
    }
}
