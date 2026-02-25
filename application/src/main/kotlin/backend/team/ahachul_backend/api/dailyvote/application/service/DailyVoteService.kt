package backend.team.ahachul_backend.api.dailyvote.application.service

import backend.team.ahachul_backend.api.common.application.port.out.SubwayLineStationReader
import backend.team.ahachul_backend.api.dailyvote.adapter.`in`.dto.DailyVoteDto
import backend.team.ahachul_backend.api.dailyvote.application.port.`in`.DailyVoteUseCase
import backend.team.ahachul_backend.api.dailyvote.application.port.`in`.dto.CreateDailyVoteCommentCommand
import backend.team.ahachul_backend.api.dailyvote.application.port.`in`.dto.GetDailyVoteCommentsCommand
import backend.team.ahachul_backend.api.dailyvote.application.port.`in`.dto.GetTodayDailyVoteCommand
import backend.team.ahachul_backend.api.dailyvote.application.port.`in`.dto.VoteDailyPollCommand
import backend.team.ahachul_backend.api.dailyvote.application.port.out.DailyVoteCommentLikeReader
import backend.team.ahachul_backend.api.dailyvote.application.port.out.DailyVoteCommentLikeWriter
import backend.team.ahachul_backend.api.dailyvote.application.port.out.DailyVoteCommentReader
import backend.team.ahachul_backend.api.dailyvote.application.port.out.DailyVoteCommentWriter
import backend.team.ahachul_backend.api.dailyvote.application.port.out.DailyVotePollReader
import backend.team.ahachul_backend.api.dailyvote.application.port.out.DailyVotePollWriter
import backend.team.ahachul_backend.api.dailyvote.application.port.out.DailyVoteResponseReader
import backend.team.ahachul_backend.api.dailyvote.application.port.out.DailyVoteResponseWriter
import backend.team.ahachul_backend.api.dailyvote.domain.entity.DailyVoteCommentEntity
import backend.team.ahachul_backend.api.dailyvote.domain.entity.DailyVoteCommentLikeEntity
import backend.team.ahachul_backend.api.dailyvote.domain.entity.DailyVotePollEntity
import backend.team.ahachul_backend.api.dailyvote.domain.entity.DailyVoteResponseEntity
import backend.team.ahachul_backend.api.dailyvote.domain.model.DailyVoteCommentStatusType
import backend.team.ahachul_backend.api.dailyvote.domain.model.DailyVoteContextType
import backend.team.ahachul_backend.api.dailyvote.domain.model.DailyVoteKindType
import backend.team.ahachul_backend.api.dailyvote.domain.model.DailyVoteSlotType
import backend.team.ahachul_backend.api.member.application.port.out.MemberReader
import backend.team.ahachul_backend.api.member.application.port.out.MemberStationReader
import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity
import backend.team.ahachul_backend.api.common.domain.entity.StationEntity
import backend.team.ahachul_backend.common.domain.entity.SubwayLineEntity
import backend.team.ahachul_backend.common.exception.CommonException
import backend.team.ahachul_backend.common.response.ResponseCode
import backend.team.ahachul_backend.common.utils.RequestUtils
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.util.Locale

@Service
@Transactional(readOnly = true)
class DailyVoteService(
    private val memberReader: MemberReader,
    private val memberStationReader: MemberStationReader,
    private val subwayLineStationReader: SubwayLineStationReader,
    private val dailyVotePollReader: DailyVotePollReader,
    private val dailyVotePollWriter: DailyVotePollWriter,
    private val dailyVoteResponseReader: DailyVoteResponseReader,
    private val dailyVoteResponseWriter: DailyVoteResponseWriter,
    private val dailyVoteCommentReader: DailyVoteCommentReader,
    private val dailyVoteCommentWriter: DailyVoteCommentWriter,
    private val dailyVoteCommentLikeReader: DailyVoteCommentLikeReader,
    private val dailyVoteCommentLikeWriter: DailyVoteCommentLikeWriter,
    private val objectMapper: ObjectMapper,
) : DailyVoteUseCase {

    private data class VoteOption(
        val code: String,
        val label: String,
        val emoji: String,
    )

    private val voteOptions = listOf(
        VoteOption(code = "TERRIBLE", label = "지옥철이었어요", emoji = "😭"),
        VoteOption(code = "HARD", label = "힘들었어요", emoji = "😵"),
        VoteOption(code = "NORMAL", label = "보통이었어요", emoji = "😐"),
        VoteOption(code = "GOOD", label = "괜찮았어요", emoji = "🙂"),
        VoteOption(code = "GREAT", label = "쾌적했어요", emoji = "😎"),
    )

    private val voteOptionCodes = voteOptions.map { it.code }.toSet()

    override fun getToday(command: GetTodayDailyVoteCommand): DailyVoteDto.TodayResponse {
        val member = currentMember()
        val favorites = memberStationReader.getByMember(member)
            .sortedBy { it.id }

        if (favorites.isEmpty()) {
            return DailyVoteDto.TodayResponse(
                generatedAt = OffsetDateTime.now().toString(),
                profileHint = "NO_FAVORITE_STATION",
                primaryPoll = null,
                secondaryPoll = null,
                stationDiary = null,
            )
        }

        val zoneId = resolveZoneId(command.timezone)
        val now = OffsetDateTime.now(zoneId)
        val slot = resolveSlot(now.toLocalTime())
        val favoriteStation = favorites.first().station
        val favoriteLine = resolveRepresentativeLine(favoriteStation)
        val primaryContext = resolvePrimaryContext(favorites.mapNotNull { it.label })
        val secondaryContext = if (primaryContext == DailyVoteContextType.SCHOOL) {
            DailyVoteContextType.COMMUTE
        } else {
            DailyVoteContextType.SCHOOL
        }

        val primaryPoll = getOrCreateMainPoll(
            pollDate = now.toLocalDate(),
            slot = slot,
            context = primaryContext,
            station = favoriteStation,
            subwayLine = favoriteLine,
            isPrimary = true,
        )
        val secondaryPoll = getOrCreateMainPoll(
            pollDate = now.toLocalDate(),
            slot = slot,
            context = secondaryContext,
            station = favoriteStation,
            subwayLine = favoriteLine,
            isPrimary = false,
        )
        val stationDiaryPoll = getOrCreateStationDiaryPoll(
            pollDate = now.toLocalDate(),
            slot = slot,
            context = primaryContext,
            station = favoriteStation,
            subwayLine = favoriteLine,
        )

        val memberId = member.id
        val primaryCard = toPollCard(primaryPoll, memberId)
        val secondaryCard = toPollCard(secondaryPoll, memberId)
        val diaryCommentCount = dailyVoteCommentReader.countByPollIdAndStatus(
            pollId = stationDiaryPoll.id,
            status = DailyVoteCommentStatusType.CREATED,
        )

        return DailyVoteDto.TodayResponse(
            generatedAt = now.toString(),
            profileHint = primaryContext.name,
            primaryPoll = primaryCard,
            secondaryPoll = secondaryCard,
            stationDiary = DailyVoteDto.StationDiaryCard(
                pollId = stationDiaryPoll.id,
                question = stationDiaryPoll.question,
                stationId = stationDiaryPoll.station.id,
                stationName = stationDiaryPoll.station.name,
                visible = primaryCard.voted,
                commentCount = diaryCommentCount,
            ),
        )
    }

    @Transactional
    override fun vote(command: VoteDailyPollCommand): DailyVoteDto.VoteResponse {
        validateOptionCode(command.optionCode)

        val member = currentMember()
        val poll = dailyVotePollReader.getById(command.pollId)

        val existing = dailyVoteResponseReader.findByPollAndMember(
            pollId = poll.id,
            memberId = member.id,
        )

        if (existing == null) {
            dailyVoteResponseWriter.save(
                DailyVoteResponseEntity.of(
                    poll = poll,
                    member = member,
                    optionCode = command.optionCode,
                ),
            )
        } else {
            existing.changeOption(command.optionCode)
            dailyVoteResponseWriter.save(existing)
        }

        return DailyVoteDto.VoteResponse(
            poll = toPollCard(poll, member.id),
        )
    }

    override fun getComments(command: GetDailyVoteCommentsCommand): DailyVoteDto.CommentsResponse {
        val poll = dailyVotePollReader.getById(command.pollId)
        val sort = resolveCommentSort(command.sort)
        val comments = dailyVoteCommentReader.findByPollIdAndStatusDesc(
            pollId = poll.id,
            status = DailyVoteCommentStatusType.CREATED,
        )

        val commentIds = comments.map { it.id }
        val likeCountByCommentId = dailyVoteCommentLikeReader.countByCommentIds(commentIds)
        val memberId = currentMemberIdOrNull()
        val likedCommentIds = if (memberId == null) {
            emptySet()
        } else {
            dailyVoteCommentLikeReader.findLikedCommentIds(commentIds, memberId)
        }

        val sortedComments = if (sort == "popular") {
            comments.sortedWith(
                compareByDescending<DailyVoteCommentEntity> { likeCountByCommentId[it.id] ?: 0L }
                    .thenByDescending { it.createdAt },
            )
        } else {
            comments.sortedByDescending { it.createdAt }
        }

        return DailyVoteDto.CommentsResponse(
            pollId = poll.id,
            sort = sort,
            comments = sortedComments.map { comment ->
                DailyVoteDto.CommentItem(
                    commentId = comment.id,
                    writer = comment.member.nickname ?: "익명",
                    content = comment.content,
                    imageUrls = parseImageUrls(comment.imageUrls),
                    likeCount = likeCountByCommentId[comment.id] ?: 0L,
                    likedByMe = likedCommentIds.contains(comment.id),
                    mine = memberId != null && comment.member.id == memberId,
                    createdAt = comment.createdAt.toString(),
                )
            },
        )
    }

    @Transactional
    override fun createComment(command: CreateDailyVoteCommentCommand): DailyVoteDto.CreateCommentResponse {
        val member = currentMember()
        val poll = dailyVotePollReader.getById(command.pollId)

        val normalizedContent = command.content.trim()
        val imageUrls = sanitizeImageUrls(command.imageUrls)
        if (normalizedContent.isBlank() && imageUrls.isEmpty()) {
            throw CommonException(ResponseCode.BAD_REQUEST)
        }

        val imageUrlsJson = if (imageUrls.isEmpty()) null else objectMapper.writeValueAsString(imageUrls)
        val comment = dailyVoteCommentWriter.save(
            DailyVoteCommentEntity.of(
                poll = poll,
                member = member,
                content = normalizedContent,
                imageUrls = imageUrlsJson,
            ),
        )

        return DailyVoteDto.CreateCommentResponse(commentId = comment.id)
    }

    @Transactional
    override fun likeComment(commentId: Long): DailyVoteDto.ToggleCommentLikeResponse {
        val member = currentMember()
        val comment = dailyVoteCommentReader.getCommentById(commentId)
        if (comment.status == DailyVoteCommentStatusType.DELETED) {
            throw CommonException(ResponseCode.DAILY_VOTE_COMMENT_NOT_FOUND)
        }

        val existing = dailyVoteCommentLikeReader.find(comment.id, member.id)
        if (existing == null) {
            dailyVoteCommentLikeWriter.save(DailyVoteCommentLikeEntity.of(comment, member))
        }

        return DailyVoteDto.ToggleCommentLikeResponse(
            commentId = comment.id,
            liked = true,
        )
    }

    @Transactional
    override fun unlikeComment(commentId: Long): DailyVoteDto.ToggleCommentLikeResponse {
        val member = currentMember()
        val comment = dailyVoteCommentReader.getCommentById(commentId)

        val existing = dailyVoteCommentLikeReader.find(comment.id, member.id)
        if (existing != null) {
            dailyVoteCommentLikeWriter.delete(comment.id, member.id)
        }

        return DailyVoteDto.ToggleCommentLikeResponse(
            commentId = comment.id,
            liked = false,
        )
    }

    private fun getOrCreateMainPoll(
        pollDate: java.time.LocalDate,
        slot: DailyVoteSlotType,
        context: DailyVoteContextType,
        station: StationEntity,
        subwayLine: SubwayLineEntity,
        isPrimary: Boolean,
    ): DailyVotePollEntity {
        val existing = dailyVotePollReader.find(
            pollDate = pollDate,
            pollSlot = slot,
            pollContext = context,
            pollKind = DailyVoteKindType.MAIN,
            stationId = station.id,
            subwayLineId = subwayLine.id,
            isPrimary = isPrimary,
        )
        if (existing != null) {
            return existing
        }

        return dailyVotePollWriter.save(
            DailyVotePollEntity.of(
                pollDate = pollDate,
                pollSlot = slot,
                pollContext = context,
                pollKind = DailyVoteKindType.MAIN,
                station = station,
                subwayLine = subwayLine,
                question = buildMainQuestion(slot, context, subwayLine.name),
                isPrimary = isPrimary,
            ),
        )
    }

    private fun getOrCreateStationDiaryPoll(
        pollDate: java.time.LocalDate,
        slot: DailyVoteSlotType,
        context: DailyVoteContextType,
        station: StationEntity,
        subwayLine: SubwayLineEntity,
    ): DailyVotePollEntity {
        val existing = dailyVotePollReader.find(
            pollDate = pollDate,
            pollSlot = slot,
            pollContext = context,
            pollKind = DailyVoteKindType.STATION_DIARY,
            stationId = station.id,
            subwayLineId = subwayLine.id,
            isPrimary = true,
        )
        if (existing != null) {
            return existing
        }

        return dailyVotePollWriter.save(
            DailyVotePollEntity.of(
                pollDate = pollDate,
                pollSlot = slot,
                pollContext = context,
                pollKind = DailyVoteKindType.STATION_DIARY,
                station = station,
                subwayLine = subwayLine,
                question = "오늘의 ${station.name}역은 어떠셨나요?",
                isPrimary = true,
            ),
        )
    }

    private fun resolveRepresentativeLine(station: StationEntity): SubwayLineEntity {
        val mappings = subwayLineStationReader.findByStation(station)
            .sortedBy { it.subwayLine.id }

        return mappings.firstOrNull()?.subwayLine
            ?: throw CommonException(ResponseCode.INVALID_DOMAIN)
    }

    private fun resolvePrimaryContext(labels: List<String>): DailyVoteContextType {
        val normalized = labels.map { it.lowercase(Locale.KOREAN) }
        val hasWorkLabel = normalized.any { text ->
            WORK_LABEL_KEYWORDS.any { keyword -> text.contains(keyword) }
        }
        if (hasWorkLabel) {
            return DailyVoteContextType.COMMUTE
        }

        val hasSchoolLabel = normalized.any { text ->
            SCHOOL_LABEL_KEYWORDS.any { keyword -> text.contains(keyword) }
        }
        if (hasSchoolLabel) {
            return DailyVoteContextType.SCHOOL
        }

        return DailyVoteContextType.COMMUTE
    }

    private fun buildMainQuestion(
        slot: DailyVoteSlotType,
        context: DailyVoteContextType,
        lineName: String,
    ): String {
        return when (context) {
            DailyVoteContextType.COMMUTE -> {
                if (slot == DailyVoteSlotType.MORNING) {
                    "오늘 ${lineName} 출근길 어땠나요?"
                } else {
                    "오늘 ${lineName} 퇴근길 어땠나요?"
                }
            }

            DailyVoteContextType.SCHOOL -> {
                if (slot == DailyVoteSlotType.MORNING) {
                    "오늘 ${lineName} 등교길 어땠나요?"
                } else {
                    "오늘 ${lineName} 하교길 어땠나요?"
                }
            }
        }
    }

    private fun resolveSlot(now: LocalTime): DailyVoteSlotType {
        if (now.hour in 15..23) {
            return DailyVoteSlotType.EVENING
        }
        return DailyVoteSlotType.MORNING
    }

    private fun resolveZoneId(timezone: String?): ZoneId {
        val normalized = timezone?.trim().takeUnless { it.isNullOrBlank() } ?: DEFAULT_TIMEZONE
        return runCatching { ZoneId.of(normalized) }
            .getOrElse { ZoneId.of(DEFAULT_TIMEZONE) }
    }

    private fun validateOptionCode(optionCode: String) {
        if (optionCode !in voteOptionCodes) {
            throw CommonException(ResponseCode.DAILY_VOTE_OPTION_INVALID)
        }
    }

    private fun toPollCard(
        poll: DailyVotePollEntity,
        memberId: Long,
    ): DailyVoteDto.PollCard {
        val responses = dailyVoteResponseReader.findByPollId(poll.id)
        val grouped = responses.groupingBy { it.optionCode }.eachCount()
        val totalVoteCount = responses.size.toLong()
        val myOptionCode = responses.firstOrNull { it.member.id == memberId }?.optionCode

        return DailyVoteDto.PollCard(
            pollId = poll.id,
            question = poll.question,
            pollContext = poll.pollContext.name,
            pollSlot = poll.pollSlot.name,
            stationId = poll.station.id,
            stationName = poll.station.name,
            subwayLineId = poll.subwayLine.id,
            subwayLineName = poll.subwayLine.name,
            isPrimary = poll.primaryYn.isY(),
            voted = myOptionCode != null,
            selectedOptionCode = myOptionCode,
            totalVoteCount = totalVoteCount,
            options = voteOptions.map { option ->
                val voteCount = grouped[option.code]?.toLong() ?: 0L
                val voteRatePercent = if (totalVoteCount == 0L) {
                    0
                } else {
                    ((voteCount * 100.0) / totalVoteCount).toInt()
                }
                DailyVoteDto.PollOption(
                    optionCode = option.code,
                    label = option.label,
                    emoji = option.emoji,
                    voteCount = voteCount,
                    voteRatePercent = voteRatePercent,
                )
            },
        )
    }

    private fun sanitizeImageUrls(imageUrls: List<String>?): List<String> {
        return imageUrls.orEmpty()
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .filter { IMAGE_URL_REGEX.matches(it) }
            .distinct()
            .take(8)
    }

    private fun parseImageUrls(imageUrlsJson: String?): List<String> {
        if (imageUrlsJson.isNullOrBlank()) {
            return emptyList()
        }

        return runCatching {
            objectMapper.readValue(imageUrlsJson, object : TypeReference<List<String>>() {})
        }.getOrDefault(emptyList())
    }

    private fun resolveCommentSort(sort: String?): String {
        val normalized = sort?.trim()?.lowercase(Locale.KOREAN)
        return if (normalized == "popular") {
            "popular"
        } else {
            "latest"
        }
    }

    private fun currentMember(): MemberEntity {
        val memberId = currentMemberIdOrNull() ?: throw CommonException(ResponseCode.INVALID_AUTH)
        return memberReader.getMember(memberId)
    }

    private fun currentMemberIdOrNull(): Long? {
        return RequestUtils.getAttribute(RequestUtils.Attribute.MEMBER_ID)?.toLongOrNull()
    }

    companion object {
        private const val DEFAULT_TIMEZONE = "Asia/Seoul"
        private val IMAGE_URL_REGEX = Regex("^https?://\\S+$")
        private val WORK_LABEL_KEYWORDS = listOf("직장", "회사", "work", "office", "company", "출근")
        private val SCHOOL_LABEL_KEYWORDS = listOf("학교", "학생", "캠퍼스", "school", "student", "university", "등교")
    }
}
