package backend.team.ahachul_backend.api.story.application.service

import backend.team.ahachul_backend.api.common.application.port.out.StationReader
import backend.team.ahachul_backend.api.member.application.port.out.MemberReader
import backend.team.ahachul_backend.api.story.adapter.`in`.dto.StoryDto
import backend.team.ahachul_backend.api.story.application.port.`in`.StoryUseCase
import backend.team.ahachul_backend.api.story.application.port.`in`.dto.CreateStoryCommand
import backend.team.ahachul_backend.api.story.application.port.`in`.dto.GetMemberStoriesCommand
import backend.team.ahachul_backend.api.story.application.port.out.StoryReader
import backend.team.ahachul_backend.api.story.application.port.out.StoryWriter
import backend.team.ahachul_backend.api.story.domain.entity.StoryEntity
import backend.team.ahachul_backend.api.story.domain.model.StoryStatusType
import backend.team.ahachul_backend.common.client.AwsS3Client
import backend.team.ahachul_backend.common.exception.BusinessException
import backend.team.ahachul_backend.common.response.ResponseCode
import backend.team.ahachul_backend.common.utils.AwsS3Utils
import backend.team.ahachul_backend.common.utils.RequestUtils
import backend.team.ahachul_backend.common.persistence.SubwayLineReader
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.text.Normalizer
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Service
@Transactional(readOnly = true)
class StoryService(
    private val storyReader: StoryReader,
    private val storyWriter: StoryWriter,
    private val memberReader: MemberReader,
    private val stationReader: StationReader,
    private val subwayLineReader: SubwayLineReader,
    private val awsS3Client: AwsS3Client,
    private val awsS3Utils: AwsS3Utils,
) : StoryUseCase {

    companion object {
        private const val MAX_LIMIT = 60
        private const val MIN_LIMIT = 1
        private const val MAX_CAPTION_LENGTH = 280
        private const val MAX_IMAGE_SIZE_BYTES = 15L * 1024L * 1024L
        private const val DEFAULT_TIMEZONE = "Asia/Seoul"
    }

    private val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    override fun getMyStories(limit: Int): StoryDto.ProfileStoriesResponse {
        val member = getCurrentMember()
        val normalizedLimit = limit.coerceIn(MIN_LIMIT, MAX_LIMIT)

        val stories = storyReader.findByMemberIdAndStatus(
            memberId = member.id,
            status = StoryStatusType.CREATED,
            pageable = PageRequest.of(0, normalizedLimit),
        )

        return buildStoriesResponse(
            memberId = member.id,
            nickname = member.nickname,
            isMine = true,
            storiesVisible = true,
            stories = stories,
        )
    }

    override fun getMemberStories(command: GetMemberStoriesCommand): StoryDto.ProfileStoriesResponse {
        val targetMember = memberReader.getMemberByNickname(normalizeNickname(command.nickname))
        val loginMemberId = RequestUtils.getAttribute(RequestUtils.Attribute.MEMBER_ID)?.toLongOrNull()
        val isMine = loginMemberId != null && loginMemberId == targetMember.id
        val enforcePublicPolicy = command.asPublic || !isMine
        val profileVisible = !enforcePublicPolicy || targetMember.isProfilePublic()
        val storiesVisible = profileVisible && (!enforcePublicPolicy || targetMember.isActivityPostsPublic())
        val normalizedLimit = command.limit.coerceIn(MIN_LIMIT, MAX_LIMIT)

        val stories = if (storiesVisible) {
            storyReader.findByMemberIdAndStatus(
                memberId = targetMember.id,
                status = StoryStatusType.CREATED,
                pageable = PageRequest.of(0, normalizedLimit),
            )
        } else {
            emptyList()
        }

        return buildStoriesResponse(
            memberId = targetMember.id,
            nickname = if (profileVisible) targetMember.nickname else null,
            isMine = isMine,
            storiesVisible = storiesVisible,
            stories = stories,
        )
    }

    override fun getPublicStories(
        limit: Int,
        stationId: Long?,
        subwayLineId: Long?,
    ): StoryDto.PublicStoriesResponse {
        val normalizedLimit = limit.coerceIn(MIN_LIMIT, MAX_LIMIT)
        val stories = storyReader.findPublicStories(
            status = StoryStatusType.CREATED,
            pageable = PageRequest.of(0, normalizedLimit),
            stationId = stationId,
            subwayLineId = subwayLineId,
        )

        return StoryDto.PublicStoriesResponse(
            generatedAt = ZonedDateTime.now(ZoneId.of(DEFAULT_TIMEZONE)).format(dateTimeFormatter),
            stories = stories.map { story ->
                StoryDto.PublicStoryItem(
                    storyId = story.id,
                    memberId = story.member.id,
                    nickname = story.member.nickname ?: "알수없음",
                    imageUrl = story.imageUrl,
                    caption = story.caption,
                    stationId = story.station?.id,
                    stationName = story.station?.name,
                    subwayLineId = story.subwayLine?.id,
                    subwayLineName = story.subwayLine?.name,
                    createdAt = story.createdAt.atZone(ZoneId.of(DEFAULT_TIMEZONE)).format(dateTimeFormatter),
                )
            },
        )
    }

    @Transactional
    override fun createStory(command: CreateStoryCommand): StoryDto.CreateResponse {
        validateImage(command)

        val member = getCurrentMember()
        val normalizedCaption = normalizeCaption(command.caption)
        val station = command.stationId?.let { stationReader.getById(it) }
        val subwayLine = command.subwayLineId?.let { subwayLineReader.getById(it) }
        val fileName = awsS3Client.upload(command.imageFile)
        val imageUrl = awsS3Utils.getUrl(fileName)

        val story = storyWriter.save(
            StoryEntity.of(
                member = member,
                imageUrl = imageUrl,
                caption = normalizedCaption,
                station = station,
                subwayLine = subwayLine,
            ),
        )
        return StoryDto.CreateResponse.of(story)
    }

    @Transactional
    override fun deleteStory(storyId: Long): StoryDto.DeleteResponse {
        val story = storyReader.getById(storyId)
        val member = getCurrentMember()
        if (story.member.id != member.id) {
            throw BusinessException(ResponseCode.STORY_FORBIDDEN)
        }

        story.delete()
        storyWriter.save(story)

        return StoryDto.DeleteResponse(
            storyId = story.id,
            deleted = true,
        )
    }

    private fun buildStoriesResponse(
        memberId: Long,
        nickname: String?,
        isMine: Boolean,
        storiesVisible: Boolean,
        stories: List<StoryEntity>,
    ): StoryDto.ProfileStoriesResponse {
        return StoryDto.ProfileStoriesResponse(
            generatedAt = ZonedDateTime.now(ZoneId.of(DEFAULT_TIMEZONE)).format(dateTimeFormatter),
            memberId = memberId,
            nickname = nickname,
            isMine = isMine,
            storiesVisible = storiesVisible,
            stories = stories.map { story ->
                StoryDto.StoryItem(
                    storyId = story.id,
                    imageUrl = story.imageUrl,
                    caption = story.caption,
                    stationId = story.station?.id,
                    stationName = story.station?.name,
                    subwayLineId = story.subwayLine?.id,
                    subwayLineName = story.subwayLine?.name,
                    createdAt = story.createdAt.atZone(ZoneId.of(DEFAULT_TIMEZONE)).format(dateTimeFormatter),
                )
            },
        )
    }

    private fun getCurrentMember() = memberReader.getMember(
        RequestUtils.getAttribute(RequestUtils.Attribute.MEMBER_ID)!!.toLong(),
    )

    private fun normalizeNickname(nickname: String): String {
        return Normalizer.normalize(nickname.trim(), Normalizer.Form.NFC)
    }

    private fun normalizeCaption(caption: String?): String? {
        val normalized = caption?.trim().orEmpty()
        if (normalized.isEmpty()) {
            return null
        }
        if (normalized.length > MAX_CAPTION_LENGTH) {
            throw BusinessException(ResponseCode.STORY_CAPTION_TOO_LONG)
        }
        return normalized
    }

    private fun validateImage(command: CreateStoryCommand) {
        val file = command.imageFile
        if (file.isEmpty || file.size <= 0) {
            throw BusinessException(ResponseCode.STORY_IMAGE_REQUIRED)
        }

        val contentType = file.contentType.orEmpty().lowercase()
        if (!contentType.startsWith("image/")) {
            throw BusinessException(ResponseCode.STORY_INVALID_IMAGE)
        }
        if (file.size > MAX_IMAGE_SIZE_BYTES) {
            throw BusinessException(ResponseCode.STORY_INVALID_IMAGE)
        }
    }
}
