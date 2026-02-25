package backend.team.ahachul_backend.api.foreigner.application.service

import backend.team.ahachul_backend.api.common.application.port.out.StationReader
import backend.team.ahachul_backend.api.common.application.port.out.SubwayLineStationReader
import backend.team.ahachul_backend.api.common.domain.entity.StationEntity
import backend.team.ahachul_backend.api.common.domain.entity.SubwayLineStationEntity
import backend.team.ahachul_backend.api.community.application.command.`in`.SearchCommunityHotPostCommand
import backend.team.ahachul_backend.api.community.application.command.out.GetSliceCommunityHotPostCommand
import backend.team.ahachul_backend.api.community.application.port.out.CommunityPostReader
import backend.team.ahachul_backend.api.community.domain.model.CommunityPostType
import backend.team.ahachul_backend.api.foreigner.adapter.`in`.dto.ForeignerModeDto
import backend.team.ahachul_backend.api.foreigner.adapter.`in`.dto.ForeignerStationSocialDto
import backend.team.ahachul_backend.api.foreigner.application.port.`in`.ForeignerModeUseCase
import backend.team.ahachul_backend.api.foreigner.application.port.`in`.dto.CreateForeignerStationSocialMeetupCommand
import backend.team.ahachul_backend.api.foreigner.application.port.`in`.dto.ForeignerLocale
import backend.team.ahachul_backend.api.foreigner.application.port.`in`.dto.GetForeignerStationGuideCommand
import backend.team.ahachul_backend.api.foreigner.application.port.`in`.dto.GetForeignerStationSocialHotspotsCommand
import backend.team.ahachul_backend.api.foreigner.application.port.`in`.dto.GetForeignerStationSocialOverviewCommand
import backend.team.ahachul_backend.api.foreigner.application.port.`in`.dto.JoinForeignerStationSocialMeetupCommand
import backend.team.ahachul_backend.api.foreigner.application.port.`in`.dto.OpenForeignerStationSocialMatchCommand
import backend.team.ahachul_backend.api.foreigner.application.port.`in`.dto.ReviewForeignerStationSocialParticipantCommand
import backend.team.ahachul_backend.api.foreigner.application.port.`in`.dto.TranslateCommunityPostCommand
import backend.team.ahachul_backend.api.foreigner.application.port.out.StationSocialMeetupParticipantReader
import backend.team.ahachul_backend.api.foreigner.application.port.out.StationSocialMeetupParticipantWriter
import backend.team.ahachul_backend.api.foreigner.application.port.out.StationSocialMeetupReader
import backend.team.ahachul_backend.api.foreigner.application.port.out.StationSocialMeetupWriter
import backend.team.ahachul_backend.api.foreigner.domain.entity.StationSocialMeetupEntity
import backend.team.ahachul_backend.api.foreigner.domain.entity.StationSocialMeetupParticipantEntity
import backend.team.ahachul_backend.api.foreigner.domain.model.StationSocialMeetupStatusType
import backend.team.ahachul_backend.api.foreigner.domain.model.StationSocialParticipantStatusType
import backend.team.ahachul_backend.api.member.application.port.out.MemberReader
import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity
import backend.team.ahachul_backend.api.message.application.port.out.MessageRoomReader
import backend.team.ahachul_backend.api.message.application.port.out.MessageRoomWriter
import backend.team.ahachul_backend.api.message.application.port.out.MessageWriter
import backend.team.ahachul_backend.api.message.domain.entity.MessageEntity
import backend.team.ahachul_backend.api.message.domain.entity.MessageRoomEntity
import backend.team.ahachul_backend.common.domain.entity.SubwayLineEntity
import backend.team.ahachul_backend.common.domain.model.YNType
import backend.team.ahachul_backend.common.exception.CommonException
import backend.team.ahachul_backend.common.response.ResponseCode
import backend.team.ahachul_backend.common.utils.RequestUtils
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import org.springframework.data.domain.Sort
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.OffsetDateTime
import java.util.Locale

@Service
@Transactional(readOnly = true)
class ForeignerModeService(
    private val stationReader: StationReader,
    private val subwayLineStationReader: SubwayLineStationReader,
    private val communityPostReader: CommunityPostReader,
    private val memberReader: MemberReader,
    private val stationSocialMeetupReader: StationSocialMeetupReader,
    private val stationSocialMeetupWriter: StationSocialMeetupWriter,
    private val stationSocialMeetupParticipantReader: StationSocialMeetupParticipantReader,
    private val stationSocialMeetupParticipantWriter: StationSocialMeetupParticipantWriter,
    private val messageRoomReader: MessageRoomReader,
    private val messageRoomWriter: MessageRoomWriter,
    private val messageWriter: MessageWriter,
    private val objectMapper: ObjectMapper,
) : ForeignerModeUseCase {

    private data class TranslationResult(
        val text: String,
        val fallback: Boolean,
    )

    private data class HotspotSeed(
        val stationCandidates: List<String>,
        val preferredLineName: String?,
        val districtLabelByLocale: Map<ForeignerLocale, String>,
        val summaryByLocale: Map<ForeignerLocale, String>,
        val contentTagsByLocale: Map<ForeignerLocale, List<String>>,
    )

    override fun getStationGuide(command: GetForeignerStationGuideCommand): ForeignerModeDto.StationGuideResponse {
        val subwayLineStation =
            subwayLineStationReader.findBySubwayLineIdAndStationId(command.subwayLineId, command.stationId)
        val stationNameKo = subwayLineStation.station.name
        val subwayLineNameKo = subwayLineStation.subwayLine.name
        val romanizedName = toTitleCaseRomanized(romanizeText(stationNameKo))

        val locale = command.locale
        val localizedStationName = localizeStationName(stationNameKo, romanizedName, locale)
        val localizedSubwayLineName = localizeSubwayLineName(subwayLineNameKo, locale)
        val templates = buildTemplates(locale, localizedStationName, localizedSubwayLineName)
        val cultureGuide = buildCultureGuide(locale)

        return ForeignerModeDto.StationGuideResponse(
            generatedAt = OffsetDateTime.now().toString(),
            station = ForeignerModeDto.StationDescriptor(
                stationId = subwayLineStation.station.id,
                subwayLineId = subwayLineStation.subwayLine.id,
                nameKo = stationNameKo,
                nameLocalized = localizedStationName,
                romanizedName = romanizedName,
                pronunciation = toPronunciation(stationNameKo),
                subwayLineNameKo = subwayLineNameKo,
                subwayLineNameLocalized = localizedSubwayLineName,
                locale = locale.code,
            ),
            templates = templates,
            cultureGuide = cultureGuide,
            oneClickActions = buildOneClickActions(
                locale = locale,
                stationId = subwayLineStation.station.id,
                subwayLineId = subwayLineStation.subwayLine.id,
                templates = templates,
                cultureGuide = cultureGuide,
            ),
            supportedLocales = ForeignerLocale.values().map { it.code },
        )
    }

    override fun translateCommunityPost(command: TranslateCommunityPostCommand): ForeignerModeDto.CommunityPostTranslationResponse {
        val post = communityPostReader.getByCustom(command.postId, null)
        if (post.status == CommunityPostType.DELETED) {
            throw CommonException(ResponseCode.POST_NOT_FOUND)
        }

        val originalTitle = normalizeWhitespace(post.title)
        val originalContent = normalizeWhitespace(extractLexicalText(post.content))
        val titleTranslation = translateText(originalTitle, command.targetLocale)
        val contentTranslation = translateText(originalContent, command.targetLocale)
        val isFallback = titleTranslation.fallback || contentTranslation.fallback

        return ForeignerModeDto.CommunityPostTranslationResponse(
            postId = post.id,
            sourceLocale = detectSourceLocale("$originalTitle $originalContent"),
            targetLocale = command.targetLocale.code,
            originalTitle = originalTitle,
            originalContent = originalContent,
            translatedTitle = titleTranslation.text,
            translatedContent = contentTranslation.text,
            isFallback = isFallback,
            notice = buildTranslationNotice(command.targetLocale),
        )
    }

    override fun getStationSocialHotspots(
        command: GetForeignerStationSocialHotspotsCommand,
    ): ForeignerStationSocialDto.HotspotsResponse {
        val now = LocalDateTime.now()
        val hotspots = HOTSPOT_SEEDS.mapNotNull { seed ->
            val station = findStationByCandidates(seed.stationCandidates) ?: return@mapNotNull null
            val stationMappings = subwayLineStationReader.findByStation(station)
            if (stationMappings.isEmpty()) {
                return@mapNotNull null
            }

            val selectedMapping = selectPreferredLine(seed.preferredLineName, stationMappings) ?: stationMappings.first()
            val romanizedName = toTitleCaseRomanized(romanizeText(station.name))
            val localizedStationName = localizeStationName(station.name, romanizedName, command.locale)
            val localizedLineName = localizeSubwayLineName(selectedMapping.subwayLine.name, command.locale)
            val reviews = loadStationReviewPosts(station.id, stationMappings.map { it.subwayLine }, limit = 3)

            ForeignerStationSocialDto.HotspotStation(
                stationId = station.id,
                subwayLineId = selectedMapping.subwayLine.id,
                stationNameKo = station.name,
                stationNameLocalized = localizedStationName,
                lineNameLocalized = localizedLineName,
                romanizedName = romanizedName,
                districtLabel = seed.districtLabelByLocale[command.locale] ?: seed.districtLabelByLocale[ForeignerLocale.EN].orEmpty(),
                summary = seed.summaryByLocale[command.locale] ?: seed.summaryByLocale[ForeignerLocale.EN].orEmpty(),
                contentTags = seed.contentTagsByLocale[command.locale] ?: seed.contentTagsByLocale[ForeignerLocale.EN].orEmpty(),
                upcomingMeetupCount = stationSocialMeetupReader.countByStationAndStatusAndMeetupAtAfter(
                    stationId = station.id,
                    status = StationSocialMeetupStatusType.OPEN,
                    from = now,
                ),
                reviewCount = reviews.size,
            )
        }

        return ForeignerStationSocialDto.HotspotsResponse(
            generatedAt = OffsetDateTime.now().toString(),
            locale = command.locale.code,
            hotspots = hotspots,
        )
    }

    override fun getStationSocialOverview(
        command: GetForeignerStationSocialOverviewCommand,
    ): ForeignerStationSocialDto.OverviewResponse {
        val station = stationReader.getById(command.stationId)
        val mappings = subwayLineStationReader.findByStation(station).sortedBy { it.subwayLine.id }
        if (mappings.isEmpty()) {
            throw CommonException(ResponseCode.INVALID_DOMAIN)
        }

        val selectedMapping = command.subwayLineId?.let {
            runCatching { subwayLineStationReader.findBySubwayLineIdAndStationId(it, station.id) }.getOrNull()
        } ?: mappings.first()

        val viewerMemberId = runCatching { getMemberId() }.getOrNull()
        val viewerNationality = command.nationalityCode

        val from = LocalDate.now().atStartOfDay()
        val to = from.plusDays(31)
        val candidateMeetups = stationSocialMeetupReader.findByStationAndRange(
            stationId = station.id,
            from = from,
            to = to,
            status = StationSocialMeetupStatusType.OPEN,
        )

        val filteredMeetups = candidateMeetups
            .filter { meetup ->
                includeByNationalityPolicy(
                    meetup = meetup,
                    sameNationalityOnly = command.sameNationalityOnly,
                    nationalityCode = viewerNationality,
                )
            }
            .take(command.limit)

        val meetupItems = filteredMeetups.map { meetup ->
            val participants = stationSocialMeetupParticipantReader.findByMeetupId(meetup.id)
            val filteredParticipants = participants.filter { participant ->
                includeParticipantByNationality(
                    participant = participant,
                    sameNationalityOnly = command.sameNationalityOnly,
                    nationalityCode = viewerNationality,
                )
            }
            val acceptedCount = participants.count { it.status == StationSocialParticipantStatusType.ACCEPTED }.toLong()

            ForeignerStationSocialDto.MeetupItem(
                meetupId = meetup.id,
                title = meetup.title,
                description = meetup.description,
                meetupAt = meetup.meetupAt.format(ISO_DATE_TIME_FORMATTER),
                maxParticipants = meetup.maxParticipants,
                acceptedCount = acceptedCount,
                hostMemberId = meetup.hostMember.id,
                hostNickname = meetup.hostMember.nickname ?: "알 수 없음",
                nationalityCode = meetup.nationalityCode,
                sameNationalityOnly = meetup.sameNationalityOnlyYn.isY(),
                status = meetup.status.name,
                mine = viewerMemberId == meetup.hostMember.id,
                participants = filteredParticipants.map { participant ->
                    ForeignerStationSocialDto.ParticipantItem(
                        participantId = participant.id,
                        memberId = participant.member.id,
                        nickname = participant.member.nickname ?: "알 수 없음",
                        nationalityCode = participant.nationalityCode,
                        status = participant.status.name,
                        mine = viewerMemberId == participant.member.id,
                    )
                },
            )
        }

        val calendar = filteredMeetups.groupBy { it.meetupAt.toLocalDate() }
            .entries
            .sortedBy { it.key }
            .map { (date, meetups) ->
                ForeignerStationSocialDto.CalendarItem(
                    date = date.toString(),
                    meetupCount = meetups.size,
                )
            }

        val reviewPosts = loadStationReviewPosts(station.id, mappings.map { it.subwayLine }, limit = 10)
            .map { review ->
                ForeignerStationSocialDto.ReviewPostItem(
                    postId = review.id,
                    title = review.title,
                    preview = truncate(extractLexicalText(review.content), 120),
                    writer = review.writer,
                    createdAt = review.createdAt.format(ISO_DATE_TIME_FORMATTER),
                )
            }

        val romanizedName = toTitleCaseRomanized(romanizeText(station.name))
        val stationLocalized = localizeStationName(station.name, romanizedName, command.locale)
        val lineLocalized = localizeSubwayLineName(selectedMapping.subwayLine.name, command.locale)
        val guide = buildCultureGuide(command.locale)

        return ForeignerStationSocialDto.OverviewResponse(
            generatedAt = OffsetDateTime.now().toString(),
            locale = command.locale.code,
            station = ForeignerStationSocialDto.StationInfo(
                stationId = station.id,
                subwayLineId = selectedMapping.subwayLine.id,
                stationNameKo = station.name,
                stationNameLocalized = stationLocalized,
                lineNameKo = selectedMapping.subwayLine.name,
                lineNameLocalized = lineLocalized,
                romanizedName = romanizedName,
                pronunciation = toPronunciation(station.name),
                cultureTips = listOf(
                    guide.lastTrainTip,
                    guide.transferEtiquetteTip,
                    guide.safetyTip,
                    guide.emergencyPhrase,
                ),
            ),
            sameNationalityOnly = command.sameNationalityOnly,
            nationalityCode = viewerNationality,
            calendar = calendar,
            meetups = meetupItems,
            reviewPosts = reviewPosts,
        )
    }

    @Transactional
    override fun createStationSocialMeetup(
        command: CreateForeignerStationSocialMeetupCommand,
    ): ForeignerStationSocialDto.CreateMeetupResponse {
        val memberId = getMemberId()
        val hostMember = memberReader.getMember(memberId)
        val lineStation = subwayLineStationReader.findBySubwayLineIdAndStationId(command.subwayLineId, command.stationId)

        val normalizedTitle = command.title.trim()
        val normalizedDescription = command.description.trim()
        if (normalizedTitle.isBlank() || normalizedDescription.isBlank()) {
            throw CommonException(ResponseCode.BAD_REQUEST)
        }
        if (command.maxParticipants !in 2..500) {
            throw CommonException(ResponseCode.BAD_REQUEST)
        }
        if (command.sameNationalityOnly && command.nationalityCode.isNullOrBlank()) {
            throw CommonException(ResponseCode.BAD_REQUEST)
        }

        val meetup = stationSocialMeetupWriter.save(
            StationSocialMeetupEntity.of(
                station = lineStation.station,
                subwayLine = lineStation.subwayLine,
                hostMember = hostMember,
                title = normalizedTitle,
                description = normalizedDescription,
                meetupAt = command.meetupAt,
                maxParticipants = command.maxParticipants,
                nationalityCode = command.nationalityCode,
                sameNationalityOnlyYn = YNType.convert(command.sameNationalityOnly),
            ),
        )

        stationSocialMeetupParticipantWriter.save(
            StationSocialMeetupParticipantEntity.of(
                meetup = meetup,
                member = hostMember,
                status = StationSocialParticipantStatusType.ACCEPTED,
                introductionMessage = "host",
                nationalityCode = command.nationalityCode,
            ),
        )

        return ForeignerStationSocialDto.CreateMeetupResponse(
            meetupId = meetup.id,
            createdAt = meetup.createdAt.format(ISO_DATE_TIME_FORMATTER),
        )
    }

    @Transactional
    override fun joinStationSocialMeetup(
        command: JoinForeignerStationSocialMeetupCommand,
    ): ForeignerStationSocialDto.JoinMeetupResponse {
        val memberId = getMemberId()
        val member = memberReader.getMember(memberId)
        val meetup = stationSocialMeetupReader.getById(command.meetupId)
        validateJoinRequest(meetup, command.nationalityCode, memberId)

        val participant = stationSocialMeetupParticipantReader.findByMeetupIdAndMemberId(meetup.id, memberId)?.apply {
            if (status == StationSocialParticipantStatusType.REQUESTED || status == StationSocialParticipantStatusType.ACCEPTED) {
                throw CommonException(ResponseCode.STATION_SOCIAL_JOIN_FORBIDDEN)
            }
            request(command.introductionMessage, command.nationalityCode)
        } ?: StationSocialMeetupParticipantEntity.of(
            meetup = meetup,
            member = member,
            status = StationSocialParticipantStatusType.REQUESTED,
            introductionMessage = command.introductionMessage,
            nationalityCode = command.nationalityCode,
        )

        val saved = stationSocialMeetupParticipantWriter.save(participant)
        return ForeignerStationSocialDto.JoinMeetupResponse(
            meetupId = meetup.id,
            participantId = saved.id,
            status = saved.status.name,
        )
    }

    @Transactional
    override fun reviewStationSocialParticipant(
        command: ReviewForeignerStationSocialParticipantCommand,
    ): ForeignerStationSocialDto.ReviewParticipantResponse {
        val memberId = getMemberId()
        val meetup = stationSocialMeetupReader.getById(command.meetupId)
        if (!meetup.isHost(memberId)) {
            throw CommonException(ResponseCode.STATION_SOCIAL_JOIN_FORBIDDEN)
        }

        val participant = stationSocialMeetupParticipantReader.getParticipantById(command.participantId)
        if (participant.meetup.id != meetup.id) {
            throw CommonException(ResponseCode.STATION_SOCIAL_PARTICIPANT_NOT_FOUND)
        }

        if (command.approve) {
            val acceptedCount = stationSocialMeetupParticipantReader.countByMeetupIdAndStatus(
                meetup.id,
                StationSocialParticipantStatusType.ACCEPTED,
            )
            if (!participant.isAccepted() && acceptedCount >= meetup.maxParticipants) {
                throw CommonException(ResponseCode.STATION_SOCIAL_CAPACITY_EXCEEDED)
            }
            participant.approve()
            if (acceptedCount + 1 >= meetup.maxParticipants) {
                meetup.close()
                stationSocialMeetupWriter.save(meetup)
            }
        } else {
            participant.reject()
        }

        val saved = stationSocialMeetupParticipantWriter.save(participant)
        return ForeignerStationSocialDto.ReviewParticipantResponse(
            meetupId = meetup.id,
            participantId = saved.id,
            status = saved.status.name,
        )
    }

    @Transactional
    override fun openStationSocialMatch(
        command: OpenForeignerStationSocialMatchCommand,
    ): ForeignerStationSocialDto.OpenMatchResponse {
        val memberId = getMemberId()
        val meetup = stationSocialMeetupReader.getById(command.meetupId)
        val participants = stationSocialMeetupParticipantReader.findByMeetupId(meetup.id)

        val callable = meetup.isHost(memberId) || participants.any {
            it.member.id == memberId && it.status == StationSocialParticipantStatusType.ACCEPTED
        }
        if (!callable) {
            throw CommonException(ResponseCode.STATION_SOCIAL_JOIN_FORBIDDEN)
        }
        if (command.targetMemberId == memberId) {
            throw CommonException(ResponseCode.INVALID_MESSAGE_REQUEST)
        }

        val targetAllowed = meetup.hostMember.id == command.targetMemberId || participants.any {
            it.member.id == command.targetMemberId &&
                it.status == StationSocialParticipantStatusType.ACCEPTED &&
                it.matchOpenYn.isY()
        }
        if (!targetAllowed) {
            throw CommonException(ResponseCode.STATION_SOCIAL_PARTICIPANT_NOT_FOUND)
        }

        val sender = memberReader.getMember(memberId)
        val target = memberReader.getMember(command.targetMemberId)
        val room = resolveMessageRoom(sender, target)
        val message = messageWriter.save(
            MessageEntity.of(
                messageRoom = room,
                senderMember = sender,
                content = command.openingMessage?.takeIf { it.isNotBlank() }
                    ?: "안녕하세요! ${meetup.title} 모임에서 매칭되어 연락드려요.",
            ),
        )
        room.updateLastMessage(message.content, message.createdAt)

        return ForeignerStationSocialDto.OpenMatchResponse(
            meetupId = meetup.id,
            targetMemberId = target.id,
            roomId = room.id,
            messageId = message.id,
        )
    }

    private fun getMemberId(): Long {
        return RequestUtils.getAttribute(RequestUtils.Attribute.MEMBER_ID)?.toLongOrNull()
            ?: throw CommonException(ResponseCode.INVALID_AUTH)
    }

    private fun validateJoinRequest(
        meetup: StationSocialMeetupEntity,
        nationalityCode: String?,
        memberId: Long,
    ) {
        if (!meetup.isOpen()) {
            throw CommonException(ResponseCode.STATION_SOCIAL_JOIN_FORBIDDEN)
        }
        if (meetup.isHost(memberId)) {
            throw CommonException(ResponseCode.STATION_SOCIAL_JOIN_FORBIDDEN)
        }
        if (
            meetup.sameNationalityOnlyYn.isY() &&
            !meetup.nationalityCode.isNullOrBlank() &&
            meetup.nationalityCode != nationalityCode
        ) {
            throw CommonException(ResponseCode.STATION_SOCIAL_NATIONALITY_MISMATCH)
        }

        val acceptedCount = stationSocialMeetupParticipantReader.countByMeetupIdAndStatus(
            meetupId = meetup.id,
            status = StationSocialParticipantStatusType.ACCEPTED,
        )
        if (acceptedCount >= meetup.maxParticipants) {
            throw CommonException(ResponseCode.STATION_SOCIAL_CAPACITY_EXCEEDED)
        }
    }

    private fun resolveMessageRoom(sender: MemberEntity, receiver: MemberEntity): MessageRoomEntity {
        val normalizedPair = normalizeMemberPair(sender.id, receiver.id)
        val existed = messageRoomReader.findByMemberPair(
            memberAId = normalizedPair.first,
            memberBId = normalizedPair.second,
        )
        if (existed != null) {
            return existed
        }
        val memberA = if (sender.id == normalizedPair.first) sender else receiver
        val memberB = if (sender.id == normalizedPair.second) sender else receiver
        return messageRoomWriter.save(MessageRoomEntity.of(memberA = memberA, memberB = memberB))
    }

    private fun normalizeMemberPair(memberId: Long, otherMemberId: Long): Pair<Long, Long> {
        return if (memberId < otherMemberId) {
            memberId to otherMemberId
        } else {
            otherMemberId to memberId
        }
    }

    private fun includeByNationalityPolicy(
        meetup: StationSocialMeetupEntity,
        sameNationalityOnly: Boolean,
        nationalityCode: String?,
    ): Boolean {
        if (!sameNationalityOnly) {
            return true
        }
        if (nationalityCode.isNullOrBlank()) {
            return true
        }
        return meetup.nationalityCode == null || meetup.nationalityCode == nationalityCode
    }

    private fun includeParticipantByNationality(
        participant: StationSocialMeetupParticipantEntity,
        sameNationalityOnly: Boolean,
        nationalityCode: String?,
    ): Boolean {
        if (!sameNationalityOnly || nationalityCode.isNullOrBlank()) {
            return true
        }
        return participant.nationalityCode == null || participant.nationalityCode == nationalityCode
    }

    private fun findStationByCandidates(candidates: List<String>): StationEntity? {
        candidates.forEach { candidate ->
            val station = runCatching { stationReader.getByName(candidate) }.getOrNull()
            if (station != null) {
                return station
            }
        }
        return null
    }

    private fun selectPreferredLine(
        preferredLineName: String?,
        stationMappings: List<SubwayLineStationEntity>,
    ): SubwayLineStationEntity? {
        if (stationMappings.isEmpty()) {
            return null
        }
        if (preferredLineName == null) {
            return stationMappings.sortedBy { it.subwayLine.id }.first()
        }
        return stationMappings.find { it.subwayLine.name == preferredLineName }
            ?: stationMappings.sortedBy { it.subwayLine.id }.first()
    }

    private fun loadStationReviewPosts(
        stationId: Long,
        subwayLines: List<SubwayLineEntity>,
        limit: Int,
    ) = communityPostReader.searchCommunityHotPosts(
        GetSliceCommunityHotPostCommand.from(
            SearchCommunityHotPostCommand(
                subwayLineIds = subwayLines.map { it.id },
                stationId = stationId,
                content = null,
                hashTag = null,
                writer = null,
                sort = Sort.by(Sort.Order.desc("createdAt")),
                pageToken = null,
                pageSize = limit.coerceIn(1, 50),
            ),
            subwayLines = subwayLines,
        ),
    )

    private fun truncate(value: String, maxLength: Int): String {
        if (value.length <= maxLength) {
            return value
        }
        return "${value.take(maxLength - 1)}…"
    }

    private fun buildTemplates(
        locale: ForeignerLocale,
        stationName: String,
        lineName: String,
    ): ForeignerModeDto.TemplateBundle {
        return when (locale) {
            ForeignerLocale.KO -> ForeignerModeDto.TemplateBundle(
                complaintTitleTemplate = "[$lineName] $stationName 이용 민원 접수",
                complaintBodyTemplate = """
                    안녕하세요. 아래 내용으로 민원을 접수합니다.
                    - 발생 시각:
                    - 위치(역/출구/승강장): $stationName
                    - 노선: $lineName
                    - 상세 내용:
                    - 요청 사항:
                """.trimIndent(),
                lostTitleTemplate = "[$stationName] 분실물 확인 요청",
                lostBodyTemplate = """
                    안녕하세요. 아래 분실물을 찾고 있습니다.
                    - 분실 시각:
                    - 분실 위치: $stationName
                    - 노선: $lineName
                    - 물품 정보(색상/브랜드/특징):
                    - 연락 방법:
                """.trimIndent(),
            )

            ForeignerLocale.EN -> ForeignerModeDto.TemplateBundle(
                complaintTitleTemplate = "[$lineName] Service issue at $stationName",
                complaintBodyTemplate = """
                    Hello, I would like to report an issue.
                    - Date and time:
                    - Location (station/exit/platform): $stationName
                    - Line: $lineName
                    - Details:
                    - Requested action:
                """.trimIndent(),
                lostTitleTemplate = "Lost item report at $stationName",
                lostBodyTemplate = """
                    Hello, I am looking for a lost item.
                    - Lost time:
                    - Lost location: $stationName
                    - Line: $lineName
                    - Item details (color/brand/features):
                    - Contact:
                """.trimIndent(),
            )

            ForeignerLocale.TH -> ForeignerModeDto.TemplateBundle(
                complaintTitleTemplate = "[$lineName] แจ้งปัญหาที่ $stationName",
                complaintBodyTemplate = """
                    สวัสดีค่ะ/ครับ ต้องการแจ้งปัญหาการใช้งาน
                    - วันที่และเวลา:
                    - จุดที่เกิดเหตุ (สถานี/ทางออก/ชานชาลา): $stationName
                    - สายรถไฟ: $lineName
                    - รายละเอียด:
                    - สิ่งที่ต้องการให้ดำเนินการ:
                """.trimIndent(),
                lostTitleTemplate = "แจ้งของหายที่ $stationName",
                lostBodyTemplate = """
                    สวัสดีค่ะ/ครับ ต้องการแจ้งของหาย
                    - เวลาที่ของหาย:
                    - สถานที่: $stationName
                    - สายรถไฟ: $lineName
                    - รายละเอียดของสิ่งของ:
                    - ช่องทางติดต่อ:
                """.trimIndent(),
            )

            ForeignerLocale.CN -> ForeignerModeDto.TemplateBundle(
                complaintTitleTemplate = "[$lineName] $stationName 使用问题反馈",
                complaintBodyTemplate = """
                    您好，我想反馈以下问题。
                    - 发生时间：
                    - 发生位置（车站/出口/站台）：$stationName
                    - 线路：$lineName
                    - 详细说明：
                    - 希望处理方式：
                """.trimIndent(),
                lostTitleTemplate = "$stationName 失物查询",
                lostBodyTemplate = """
                    您好，我想寻找遗失物品。
                    - 遗失时间：
                    - 遗失地点：$stationName
                    - 线路：$lineName
                    - 物品特征（颜色/品牌/特征）：
                    - 联系方式：
                """.trimIndent(),
            )
        }
    }

    private fun buildCultureGuide(locale: ForeignerLocale): ForeignerModeDto.CultureGuide {
        return when (locale) {
            ForeignerLocale.KO -> ForeignerModeDto.CultureGuide(
                lastTrainTip = "막차 15분 전에는 승강장 도착을 권장합니다.",
                transferEtiquetteTip = "환승 통로에서는 한 줄 이동, 하차 승객 우선 탑승을 지켜주세요.",
                safetyTip = "혼잡 시 승강장 안전선 안쪽에서 대기하고, 무리한 탑승은 피하세요.",
                emergencyPhrase = "긴급 상황 시 역무실 또는 112에 즉시 신고하세요.",
            )

            ForeignerLocale.EN -> ForeignerModeDto.CultureGuide(
                lastTrainTip = "Try to arrive at the platform at least 15 minutes before the last train.",
                transferEtiquetteTip = "Keep to one side in transfer corridors and let passengers exit first.",
                safetyTip = "During crowding, wait behind the safety line and avoid forcing your way in.",
                emergencyPhrase = "In emergencies, contact station staff or call 112 immediately.",
            )

            ForeignerLocale.TH -> ForeignerModeDto.CultureGuide(
                lastTrainTip = "ควรมาถึงชานชาลาก่อนรถไฟเที่ยวสุดท้ายอย่างน้อย 15 นาที",
                transferEtiquetteTip = "เดินเรียงหนึ่งในทางเชื่อมและให้ผู้โดยสารลงก่อนขึ้น",
                safetyTip = "ช่วงคนแน่นให้ยืนหลังเส้นปลอดภัยและหลีกเลี่ยงการฝืนขึ้นรถ",
                emergencyPhrase = "กรณีฉุกเฉินให้ติดต่อเจ้าหน้าที่สถานีหรือโทร 112 ทันที",
            )

            ForeignerLocale.CN -> ForeignerModeDto.CultureGuide(
                lastTrainTip = "建议至少提前15分钟到达站台乘坐末班车。",
                transferEtiquetteTip = "换乘通道请单列通行，并先下后上。",
                safetyTip = "拥挤时请站在安全线内侧，避免强行上车。",
                emergencyPhrase = "紧急情况请立即联系车站工作人员或拨打112。",
            )
        }
    }

    private fun buildOneClickActions(
        locale: ForeignerLocale,
        stationId: Long,
        subwayLineId: Long,
        templates: ForeignerModeDto.TemplateBundle,
        cultureGuide: ForeignerModeDto.CultureGuide,
    ): List<ForeignerModeDto.OneClickAction> {
        val actionText = when (locale) {
            ForeignerLocale.KO -> mapOf(
                "callTitle" to "112 긴급전화",
                "callDesc" to "긴급 상황 시 즉시 112로 연결합니다.",
                "lostTitle" to "분실 신고 바로가기",
                "lostDesc" to "역/노선이 채워진 분실물 신고 화면으로 이동합니다.",
                "complaintTitle" to "민원 신고 바로가기",
                "complaintDesc" to "역/노선이 채워진 민원 접수 화면으로 이동합니다.",
                "copyTitle" to "긴급 문구 복사",
                "copyDesc" to "역무원에게 보여줄 긴급 문구를 복사합니다.",
            )
            ForeignerLocale.EN -> mapOf(
                "callTitle" to "Call 112",
                "callDesc" to "Immediately connect to emergency support.",
                "lostTitle" to "Lost item report",
                "lostDesc" to "Open lost-item form with station prefilled.",
                "complaintTitle" to "Service complaint",
                "complaintDesc" to "Open complaint form with station prefilled.",
                "copyTitle" to "Copy emergency phrase",
                "copyDesc" to "Copy an emergency phrase for station staff.",
            )
            ForeignerLocale.TH -> mapOf(
                "callTitle" to "โทร 112",
                "callDesc" to "เชื่อมต่อสายฉุกเฉินทันที",
                "lostTitle" to "แจ้งของหาย",
                "lostDesc" to "เปิดฟอร์มของหายพร้อมข้อมูลสถานี",
                "complaintTitle" to "แจ้งปัญหาการใช้งาน",
                "complaintDesc" to "เปิดฟอร์มร้องเรียนพร้อมข้อมูลสถานี",
                "copyTitle" to "คัดลอกข้อความฉุกเฉิน",
                "copyDesc" to "คัดลอกข้อความสำหรับแจ้งเจ้าหน้าที่",
            )
            ForeignerLocale.CN -> mapOf(
                "callTitle" to "拨打112",
                "callDesc" to "立即连接紧急支援",
                "lostTitle" to "失物申报",
                "lostDesc" to "打开已预填车站信息的失物表单",
                "complaintTitle" to "服务投诉",
                "complaintDesc" to "打开已预填车站信息的投诉表单",
                "copyTitle" to "复制紧急短语",
                "copyDesc" to "复制用于向站务员求助的短语",
            )
        }

        val prefillQuery = "prefill=1&templateLocale=${locale.code}&stationId=$stationId&subwayLineId=$subwayLineId"

        return listOf(
            ForeignerModeDto.OneClickAction(
                actionType = ForeignerModeDto.OneClickActionType.CALL_EMERGENCY_112,
                title = actionText.getValue("callTitle"),
                description = actionText.getValue("callDesc"),
                deepLink = "tel:112",
                payloadTemplate = null,
            ),
            ForeignerModeDto.OneClickAction(
                actionType = ForeignerModeDto.OneClickActionType.OPEN_LOST_REPORT,
                title = actionText.getValue("lostTitle"),
                description = actionText.getValue("lostDesc"),
                deepLink = "/lost-found/new?$prefillQuery",
                payloadTemplate = templates.lostBodyTemplate,
            ),
            ForeignerModeDto.OneClickAction(
                actionType = ForeignerModeDto.OneClickActionType.OPEN_COMPLAINT_REPORT,
                title = actionText.getValue("complaintTitle"),
                description = actionText.getValue("complaintDesc"),
                deepLink = "/complaint/new?$prefillQuery",
                payloadTemplate = templates.complaintBodyTemplate,
            ),
            ForeignerModeDto.OneClickAction(
                actionType = ForeignerModeDto.OneClickActionType.COPY_EMERGENCY_PHRASE,
                title = actionText.getValue("copyTitle"),
                description = actionText.getValue("copyDesc"),
                deepLink = "copy://emergency-phrase",
                payloadTemplate = cultureGuide.emergencyPhrase,
            ),
        )
    }

    private fun buildTranslationNotice(locale: ForeignerLocale): String {
        return when (locale) {
            ForeignerLocale.KO -> "자동 번역 결과는 참고용이며 원문과 차이가 있을 수 있습니다."
            ForeignerLocale.EN -> "Auto-translation is for reference and may differ from the original meaning."
            ForeignerLocale.TH -> "ผลการแปลอัตโนมัติเป็นข้อมูลอ้างอิงและอาจแตกต่างจากต้นฉบับ"
            ForeignerLocale.CN -> "自动翻译仅供参考，可能与原文含义存在差异。"
        }
    }

    private fun localizeStationName(
        stationNameKo: String,
        romanizedName: String,
        locale: ForeignerLocale,
    ): String {
        return when (locale) {
            ForeignerLocale.KO -> stationNameKo
            ForeignerLocale.EN -> "$romanizedName Station"
            ForeignerLocale.TH -> "$stationNameKo ($romanizedName)"
            ForeignerLocale.CN -> "$stationNameKo（$romanizedName）"
        }
    }

    private fun localizeSubwayLineName(
        lineNameKo: String,
        locale: ForeignerLocale,
    ): String {
        val numericLine = Regex("""(\d+)호선""").find(lineNameKo)?.groupValues?.getOrNull(1)
        if (numericLine != null) {
            return when (locale) {
                ForeignerLocale.KO -> lineNameKo
                ForeignerLocale.EN -> "Line $numericLine"
                ForeignerLocale.TH -> "สาย $numericLine"
                ForeignerLocale.CN -> "${numericLine}号线"
            }
        }

        val lineNameByLocale = mapOf(
            ForeignerLocale.EN to mapOf(
                "신분당선" to "Shinbundang Line",
                "수인분당선" to "Suin-Bundang Line",
                "경의중앙선" to "Gyeongui-Jungang Line",
                "우이신설경전철" to "Ui-Sinseol Light Rail",
                "공항철도" to "Airport Railroad",
                "김포골드라인" to "Gimpo Gold Line",
            ),
            ForeignerLocale.TH to mapOf(
                "신분당선" to "สายชินบุนดัง",
                "수인분당선" to "สายซูอิน-บุนดัง",
                "경의중앙선" to "สายคย็องอี-จุงอัง",
                "우이신설경전철" to "รถไฟรางเบาอูอี-ชินซอล",
                "공항철도" to "รถไฟสนามบิน",
                "김포골드라인" to "สายกิมโปโกลด์",
            ),
            ForeignerLocale.CN to mapOf(
                "신분당선" to "新盆唐线",
                "수인분당선" to "水仁盆唐线",
                "경의중앙선" to "京义中央线",
                "우이신설경전철" to "牛耳新设轻轨",
                "공항철도" to "机场铁路",
                "김포골드라인" to "金浦黄金线",
            ),
        )

        return when (locale) {
            ForeignerLocale.KO -> lineNameKo
            else -> lineNameByLocale[locale]?.get(lineNameKo)
                ?: "$lineNameKo (${toTitleCaseRomanized(romanizeText(lineNameKo))})"
        }
    }

    private fun detectSourceLocale(text: String): String {
        return if (text.any { isHangul(it) }) {
            ForeignerLocale.KO.code
        } else {
            ForeignerLocale.EN.code
        }
    }

    private fun extractLexicalText(raw: String): String {
        if (raw.isBlank()) {
            return ""
        }

        val trimmed = raw.trim()
        if (!trimmed.startsWith("{")) {
            return trimmed
        }

        return runCatching {
            val tree = objectMapper.readTree(trimmed)
            val collector = mutableListOf<String>()
            collectTextNodes(tree, collector)
            normalizeWhitespace(collector.joinToString(" "))
        }.getOrDefault(trimmed)
    }

    private fun collectTextNodes(node: JsonNode, collector: MutableList<String>) {
        when {
            node.isObject -> {
                val textNode = node.get("text")
                if (textNode != null && textNode.isTextual) {
                    collector.add(textNode.asText())
                }
                node.fields().forEachRemaining { (_, child) ->
                    collectTextNodes(child, collector)
                }
            }

            node.isArray -> {
                node.forEach { child ->
                    collectTextNodes(child, collector)
                }
            }
        }
    }

    private fun translateText(source: String, targetLocale: ForeignerLocale): TranslationResult {
        if (targetLocale == ForeignerLocale.KO) {
            return TranslationResult(source, fallback = false)
        }

        if (source.isBlank()) {
            return TranslationResult(
                when (targetLocale) {
                    ForeignerLocale.EN -> "No content"
                    ForeignerLocale.TH -> "ไม่มีเนื้อหา"
                    ForeignerLocale.CN -> "无内容"
                    ForeignerLocale.KO -> "내용 없음"
                },
                fallback = true,
            )
        }

        val dictionary = TRANSLATION_DICTIONARY[targetLocale].orEmpty()
        var translated = source
        var replacementCount = 0
        val linePattern = Regex("""(\d+)호선""")
        translated = linePattern.replace(translated) { matchResult ->
            replacementCount += 1
            val lineNo = matchResult.groupValues[1]
            when (targetLocale) {
                ForeignerLocale.EN -> "Line $lineNo"
                ForeignerLocale.TH -> "สาย $lineNo"
                ForeignerLocale.CN -> "${lineNo}号线"
                ForeignerLocale.KO -> matchResult.value
            }
        }

        dictionary.forEach { (origin, converted) ->
            if (translated.contains(origin)) {
                replacementCount += 1
                translated = translated.replace(origin, converted)
            }
        }

        val normalizedTranslated = normalizeWhitespace(romanizeResidualHangul(translated))
        return if (replacementCount == 0) {
            TranslationResult(normalizedTranslated, fallback = true)
        } else {
            TranslationResult(normalizedTranslated, fallback = false)
        }
    }

    private fun romanizeResidualHangul(value: String): String {
        val builder = StringBuilder()
        value.forEach { ch ->
            if (isHangul(ch)) {
                builder.append(romanizeHangulSyllable(ch))
            } else {
                builder.append(ch)
            }
        }
        return builder.toString()
    }

    private fun toPronunciation(text: String): String {
        return text.split(Regex("\\s+"))
            .filter { it.isNotBlank() }
            .joinToString(" ") { word ->
                word.map { ch ->
                    if (isHangul(ch)) {
                        romanizeHangulSyllable(ch)
                    } else {
                        ch.toString()
                    }
                }.joinToString("-")
            }
    }

    private fun romanizeText(text: String): String {
        val builder = StringBuilder()
        text.forEach { ch ->
            if (isHangul(ch)) {
                builder.append(romanizeHangulSyllable(ch))
            } else {
                builder.append(ch)
            }
        }
        return normalizeWhitespace(builder.toString())
    }

    private fun toTitleCaseRomanized(value: String): String {
        return value.split(" ")
            .filter { it.isNotBlank() }
            .joinToString(" ") { token ->
                token.replaceFirstChar { first ->
                    if (first.isLowerCase()) {
                        first.titlecase(Locale.ENGLISH)
                    } else {
                        first.toString()
                    }
                }
            }
    }

    private fun normalizeWhitespace(value: String): String {
        return value.replace(Regex("\\s+"), " ").trim()
    }

    private fun isHangul(ch: Char): Boolean = ch.code in HANGUL_BASE..HANGUL_LAST

    private fun romanizeHangulSyllable(ch: Char): String {
        if (!isHangul(ch)) {
            return ch.toString()
        }

        val syllableIndex = ch.code - HANGUL_BASE
        val choIndex = syllableIndex / (JUNGSEONG_COUNT * JONGSEONG_COUNT)
        val jungIndex = (syllableIndex % (JUNGSEONG_COUNT * JONGSEONG_COUNT)) / JONGSEONG_COUNT
        val jongIndex = syllableIndex % JONGSEONG_COUNT

        return CHOSEONG_ROMANIZATION[choIndex] +
            JUNGSEONG_ROMANIZATION[jungIndex] +
            JONGSEONG_ROMANIZATION[jongIndex]
    }

    companion object {
        private val ISO_DATE_TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        private const val HANGUL_BASE = 0xAC00
        private const val HANGUL_LAST = 0xD7A3
        private const val JUNGSEONG_COUNT = 21
        private const val JONGSEONG_COUNT = 28

        private val HOTSPOT_SEEDS = listOf(
            HotspotSeed(
                stationCandidates = listOf("명동", "명동역"),
                preferredLineName = "4호선",
                districtLabelByLocale = mapOf(
                    ForeignerLocale.KO to "명동 관광/쇼핑",
                    ForeignerLocale.EN to "Myeong-dong shopping district",
                    ForeignerLocale.TH to "ย่านช้อปปิ้งมย็องดง",
                    ForeignerLocale.CN to "明洞购物区",
                ),
                summaryByLocale = mapOf(
                    ForeignerLocale.KO to "뷰티/약국/패션 소비가 집중되는 핵심 관광 허브",
                    ForeignerLocale.EN to "A major tourism hub for beauty, pharmacy, and fashion shopping",
                    ForeignerLocale.TH to "ฮับท่องเที่ยวหลักด้านบิวตี้ ร้านยา และแฟชั่น",
                    ForeignerLocale.CN to "以美妆、药妆与时尚购物为主的核心旅游枢纽",
                ),
                contentTagsByLocale = mapOf(
                    ForeignerLocale.EN to listOf("Beauty", "Pharmacy", "Duty Free", "Street Food"),
                    ForeignerLocale.KO to listOf("뷰티", "약국", "면세", "길거리음식"),
                ),
            ),
            HotspotSeed(
                stationCandidates = listOf("성수", "성수역"),
                preferredLineName = "2호선",
                districtLabelByLocale = mapOf(
                    ForeignerLocale.KO to "성수 라이프스타일",
                    ForeignerLocale.EN to "Seongsu lifestyle district",
                    ForeignerLocale.TH to "ย่านไลฟ์สไตล์ซองซู",
                    ForeignerLocale.CN to "圣水生活方式街区",
                ),
                summaryByLocale = mapOf(
                    ForeignerLocale.KO to "편집숍/팝업/브랜드 쇼룸이 밀집한 트렌드 중심지",
                    ForeignerLocale.EN to "Trend-focused area packed with select shops and pop-up stores",
                    ForeignerLocale.TH to "ย่านเทรนด์ที่รวมร้านคัดสรรและป๊อปอัปสโตร์",
                    ForeignerLocale.CN to "聚集精选店与快闪店的潮流中心区域",
                ),
                contentTagsByLocale = mapOf(
                    ForeignerLocale.EN to listOf("Select Shop", "Popup", "Design", "Cafe"),
                    ForeignerLocale.KO to listOf("편집숍", "팝업", "디자인", "카페"),
                ),
            ),
            HotspotSeed(
                stationCandidates = listOf("홍대입구", "홍대입구역"),
                preferredLineName = "2호선",
                districtLabelByLocale = mapOf(
                    ForeignerLocale.KO to "홍대 문화/야간",
                    ForeignerLocale.EN to "Hongdae culture and nightlife",
                    ForeignerLocale.TH to "ย่านวัฒนธรรมและไนต์ไลฟ์ฮงแด",
                    ForeignerLocale.CN to "弘大文化与夜生活区",
                ),
                summaryByLocale = mapOf(
                    ForeignerLocale.KO to "공연/거리예술/야간 상권 중심의 젊은 관광지",
                    ForeignerLocale.EN to "Youth-driven area known for street performances and nightlife",
                    ForeignerLocale.TH to "พื้นที่วัยรุ่นเด่นด้านการแสดงริมถนนและไนต์ไลฟ์",
                    ForeignerLocale.CN to "以街头演出与夜间商圈著称的年轻活力区域",
                ),
                contentTagsByLocale = mapOf(
                    ForeignerLocale.EN to listOf("Street 공연", "Nightlife", "Budget Food", "Vintage"),
                    ForeignerLocale.KO to listOf("공연", "야간", "가성비맛집", "빈티지"),
                ),
            ),
            HotspotSeed(
                stationCandidates = listOf("강남", "강남역"),
                preferredLineName = "2호선",
                districtLabelByLocale = mapOf(
                    ForeignerLocale.KO to "강남 비즈/쇼핑",
                    ForeignerLocale.EN to "Gangnam business & shopping",
                    ForeignerLocale.TH to "ย่านธุรกิจและช้อปปิ้งกังนัม",
                    ForeignerLocale.CN to "江南商务购物区",
                ),
                summaryByLocale = mapOf(
                    ForeignerLocale.KO to "대형 상업시설과 글로벌 브랜드 접근성이 높은 중심지",
                    ForeignerLocale.EN to "Core district with major retail complexes and global brands",
                    ForeignerLocale.TH to "ศูนย์กลางที่เข้าถึงห้างใหญ่และแบรนด์สากลง่าย",
                    ForeignerLocale.CN to "大型商业设施与全球品牌集中、可达性高的中心区",
                ),
                contentTagsByLocale = mapOf(
                    ForeignerLocale.EN to listOf("Business", "Shopping", "Medical", "Transit"),
                    ForeignerLocale.KO to listOf("비즈니스", "쇼핑", "의료", "환승"),
                ),
            ),
            HotspotSeed(
                stationCandidates = listOf("안국", "안국역"),
                preferredLineName = "3호선",
                districtLabelByLocale = mapOf(
                    ForeignerLocale.KO to "안국 전통/문화",
                    ForeignerLocale.EN to "Anguk heritage district",
                    ForeignerLocale.TH to "ย่านมรดกวัฒนธรรมอันกุก",
                    ForeignerLocale.CN to "安国传统文化区",
                ),
                summaryByLocale = mapOf(
                    ForeignerLocale.KO to "궁궐/한옥/전통 콘텐츠를 체험하기 좋은 역사 권역",
                    ForeignerLocale.EN to "Historic area for palaces, hanok villages, and cultural experiences",
                    ForeignerLocale.TH to "เขตประวัติศาสตร์เหมาะกับวัง ฮันอก และวัฒนธรรมดั้งเดิม",
                    ForeignerLocale.CN to "适合体验宫殿、韩屋与传统文化的历史街区",
                ),
                contentTagsByLocale = mapOf(
                    ForeignerLocale.EN to listOf("Hanok", "Museum", "Palace", "Traditional Food"),
                    ForeignerLocale.KO to listOf("한옥", "박물관", "궁궐", "전통음식"),
                ),
            ),
        )

        private val CHOSEONG_ROMANIZATION = arrayOf(
            "g", "kk", "n", "d", "tt", "r", "m", "b", "pp", "s", "ss", "", "j", "jj", "ch", "k", "t", "p", "h",
        )

        private val JUNGSEONG_ROMANIZATION = arrayOf(
            "a", "ae", "ya", "yae", "eo", "e", "yeo", "ye", "o", "wa", "wae", "oe", "yo", "u", "wo", "we", "wi", "yu", "eu", "ui", "i",
        )

        private val JONGSEONG_ROMANIZATION = arrayOf(
            "", "k", "k", "ks", "n", "nj", "nh", "t", "l", "lk", "lm", "lb", "ls", "lt", "lp", "lh", "m", "p", "ps", "t", "t", "ng", "t", "t", "k", "t", "p", "h",
        )

        private val TRANSLATION_DICTIONARY: Map<ForeignerLocale, List<Pair<String, String>>> = mapOf(
            ForeignerLocale.EN to listOf(
                "지하철" to "subway",
                "열차" to "train",
                "환승" to "transfer",
                "막차" to "last train",
                "지연" to "delay",
                "혼잡" to "crowded",
                "사고" to "incident",
                "운행" to "operation",
                "출구" to "exit",
                "승강장" to "platform",
                "분실물" to "lost item",
                "민원" to "complaint",
                "빠르게" to "quickly",
                "늦습니다" to "running late",
                "도착예정" to "ETA",
            ),
            ForeignerLocale.TH to listOf(
                "지하철" to "รถไฟใต้ดิน",
                "열차" to "ขบวนรถ",
                "환승" to "เปลี่ยนสาย",
                "막차" to "รถเที่ยวสุดท้าย",
                "지연" to "ล่าช้า",
                "혼잡" to "แออัด",
                "사고" to "เหตุขัดข้อง",
                "출구" to "ทางออก",
                "승강장" to "ชานชาลา",
                "분실물" to "ของหาย",
                "민원" to "คำร้องเรียน",
            ),
            ForeignerLocale.CN to listOf(
                "지하철" to "地铁",
                "열차" to "列车",
                "환승" to "换乘",
                "막차" to "末班车",
                "지연" to "延误",
                "혼잡" to "拥挤",
                "사고" to "事故",
                "출구" to "出口",
                "승강장" to "站台",
                "분실물" to "失物",
                "민원" to "投诉",
            ),
        )
    }
}
