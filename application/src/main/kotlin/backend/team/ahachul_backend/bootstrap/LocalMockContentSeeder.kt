package backend.team.ahachul_backend.bootstrap

import backend.team.ahachul_backend.api.article.adapter.web.out.ArticleBookmarkRepository
import backend.team.ahachul_backend.api.article.adapter.web.out.ArticleLikeRepository
import backend.team.ahachul_backend.api.article.domain.entity.ArticleBookmarkEntity
import backend.team.ahachul_backend.api.article.domain.entity.ArticleLikeEntity
import backend.team.ahachul_backend.api.article.domain.model.ArticleType
import backend.team.ahachul_backend.api.comment.adapter.web.out.CommentLikeRepository
import backend.team.ahachul_backend.api.comment.adapter.web.out.CommentRepository
import backend.team.ahachul_backend.api.comment.domain.entity.CommentEntity
import backend.team.ahachul_backend.api.comment.domain.entity.CommentLikeEntity
import backend.team.ahachul_backend.api.comment.domain.model.CommentVisibility
import backend.team.ahachul_backend.api.common.adapter.web.out.StationRepository
import backend.team.ahachul_backend.api.common.adapter.web.out.SubwayLineStationRepository
import backend.team.ahachul_backend.api.common.domain.entity.StationEntity
import backend.team.ahachul_backend.api.community.adapter.web.out.CommunityPostRepository
import backend.team.ahachul_backend.api.community.domain.entity.CommunityPostEntity
import backend.team.ahachul_backend.api.community.domain.model.CommunityCategoryType
import backend.team.ahachul_backend.api.complaint.adapter.out.ComplaintPostRepository
import backend.team.ahachul_backend.api.complaint.domain.entity.ComplaintPostEntity
import backend.team.ahachul_backend.api.complaint.domain.model.ComplaintType
import backend.team.ahachul_backend.api.complaint.domain.model.ShortContentType
import backend.team.ahachul_backend.api.dailyvote.adapter.web.out.DailyVoteCommentLikeRepository
import backend.team.ahachul_backend.api.dailyvote.adapter.web.out.DailyVoteCommentRepository
import backend.team.ahachul_backend.api.dailyvote.adapter.web.out.DailyVotePollRepository
import backend.team.ahachul_backend.api.dailyvote.adapter.web.out.DailyVoteResponseRepository
import backend.team.ahachul_backend.api.dailyvote.domain.entity.DailyVoteCommentEntity
import backend.team.ahachul_backend.api.dailyvote.domain.entity.DailyVoteCommentLikeEntity
import backend.team.ahachul_backend.api.dailyvote.domain.entity.DailyVotePollEntity
import backend.team.ahachul_backend.api.dailyvote.domain.entity.DailyVoteResponseEntity
import backend.team.ahachul_backend.api.dailyvote.domain.model.DailyVoteCommentStatusType
import backend.team.ahachul_backend.api.dailyvote.domain.model.DailyVoteContextType
import backend.team.ahachul_backend.api.dailyvote.domain.model.DailyVoteKindType
import backend.team.ahachul_backend.api.dailyvote.domain.model.DailyVoteSlotType
import backend.team.ahachul_backend.api.foreigner.adapter.web.out.StationSocialMeetupParticipantRepository
import backend.team.ahachul_backend.api.foreigner.adapter.web.out.StationSocialMeetupRepository
import backend.team.ahachul_backend.api.foreigner.domain.entity.StationSocialMeetupEntity
import backend.team.ahachul_backend.api.foreigner.domain.entity.StationSocialMeetupParticipantEntity
import backend.team.ahachul_backend.api.foreigner.domain.model.StationSocialParticipantStatusType
import backend.team.ahachul_backend.api.lost.adapter.web.out.LostPostRepository
import backend.team.ahachul_backend.api.lost.domain.entity.LostPostEntity
import backend.team.ahachul_backend.api.lost.domain.model.LostType
import backend.team.ahachul_backend.api.member.adapter.web.out.MemberRepository
import backend.team.ahachul_backend.api.member.adapter.web.out.MemberStationRepository
import backend.team.ahachul_backend.api.member.adapter.web.out.MemberStationRouteRepository
import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity
import backend.team.ahachul_backend.api.member.domain.entity.MemberStationEntity
import backend.team.ahachul_backend.api.member.domain.entity.MemberStationRouteEntity
import backend.team.ahachul_backend.api.member.domain.model.MemberStatusType
import backend.team.ahachul_backend.api.member.domain.model.ProviderType
import backend.team.ahachul_backend.api.message.adapter.web.out.MessageRepository
import backend.team.ahachul_backend.api.message.adapter.web.out.MessageRoomRepository
import backend.team.ahachul_backend.api.message.domain.entity.MessageEntity
import backend.team.ahachul_backend.api.message.domain.entity.MessageRoomEntity
import backend.team.ahachul_backend.common.domain.entity.SubwayLineEntity
import backend.team.ahachul_backend.common.domain.model.YNType
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Profile
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime

@Profile("local")
@Component
class LocalMockContentSeeder(
    private val memberRepository: MemberRepository,
    private val stationRepository: StationRepository,
    private val subwayLineStationRepository: SubwayLineStationRepository,
    private val communityPostRepository: CommunityPostRepository,
    private val complaintPostRepository: ComplaintPostRepository,
    private val lostPostRepository: LostPostRepository,
    private val commentRepository: CommentRepository,
    private val commentLikeRepository: CommentLikeRepository,
    private val articleLikeRepository: ArticleLikeRepository,
    private val articleBookmarkRepository: ArticleBookmarkRepository,
    private val messageRoomRepository: MessageRoomRepository,
    private val messageRepository: MessageRepository,
    private val memberStationRepository: MemberStationRepository,
    private val memberStationRouteRepository: MemberStationRouteRepository,
    private val dailyVotePollRepository: DailyVotePollRepository,
    private val dailyVoteResponseRepository: DailyVoteResponseRepository,
    private val dailyVoteCommentRepository: DailyVoteCommentRepository,
    private val dailyVoteCommentLikeRepository: DailyVoteCommentLikeRepository,
    private val stationSocialMeetupRepository: StationSocialMeetupRepository,
    private val stationSocialMeetupParticipantRepository: StationSocialMeetupParticipantRepository,
) {
    private data class StationLinePair(
        val station: StationEntity,
        val subwayLine: SubwayLineEntity,
    )

    @EventListener(ApplicationReadyEvent::class)
    @Transactional
    fun seed() {
        val stationLinePairs = resolveStationLinePairs()
        if (stationLinePairs.isEmpty()) {
            logger.warn("[local-seed] station/line 매핑 데이터가 없어 목데이터 생성을 건너뜁니다.")
            return
        }

        val members = resolveSeedMembers()
        seedMemberStationsAndRoutes(members, stationLinePairs)

        val communityPosts = seedCommunityPosts(members, stationLinePairs)
        val complaintPosts = seedComplaintPosts(members, stationLinePairs)
        val lostPosts = seedLostPosts(members, stationLinePairs)
        val comments = seedComments(members, communityPosts, complaintPosts, lostPosts)

        seedCommentLikes(members, comments)
        seedArticleReactions(members, communityPosts, complaintPosts, lostPosts)
        seedMessageRoomsAndMessages(members)
        seedDailyVoteData(members, stationLinePairs)
        seedStationSocialMeetups(members, stationLinePairs)

        logger.info(
            "[local-seed] 완료 members={}, stationPairs={}, community={}, complaint={}, lost={}, comments={}",
            members.size,
            stationLinePairs.size,
            communityPosts.size,
            complaintPosts.size,
            lostPosts.size,
            comments.size,
        )
    }

    private fun resolveStationLinePairs(): List<StationLinePair> {
        val mappings = subwayLineStationRepository.findAllByOrderBySubwayLineIdAscIdAsc()
        if (mappings.isEmpty()) {
            return emptyList()
        }

        val preferredPairs = PREFERRED_STATION_NAMES.mapNotNull { stationName ->
            val station = stationRepository.findByName(stationName).orElse(null) ?: return@mapNotNull null
            val mapping = mappings.firstOrNull { it.station.id == station.id } ?: return@mapNotNull null
            StationLinePair(station = mapping.station, subwayLine = mapping.subwayLine)
        }

        return (preferredPairs + mappings.map { StationLinePair(it.station, it.subwayLine) })
            .distinctBy { "${it.station.id}:${it.subwayLine.id}" }
            .take(SEED_STATION_LINE_PAIR_COUNT)
    }

    private fun resolveSeedMembers(): List<MemberEntity> {
        val existingSeedMembers = memberRepository.findAll()
            .filter { isSeedMember(it) }
            .sortedBy { it.providerUserId }
            .toMutableList()

        if (existingSeedMembers.size >= SEED_MEMBER_COUNT) {
            return existingSeedMembers.take(SEED_MEMBER_COUNT)
        }

        val created = mutableListOf<MemberEntity>()
        var order = 1
        while (existingSeedMembers.size + created.size < SEED_MEMBER_COUNT) {
            val providerUserId = "$SEED_MEMBER_PREFIX$order"
            if (memberRepository.findByProviderUserId(providerUserId) != null) {
                order += 1
                continue
            }

            val provider = when (order % 3) {
                1 -> ProviderType.GOOGLE
                2 -> ProviderType.KAKAO
                else -> ProviderType.APPLE
            }

            created += memberRepository.save(
                MemberEntity(
                    nickname = "아하러$order",
                    providerUserId = providerUserId,
                    provider = provider,
                    email = "local.seed$order@ahhachul.dev",
                    gender = null,
                    ageRange = AGE_RANGES[(order - 1) % AGE_RANGES.size],
                    status = MemberStatusType.ACTIVE,
                ),
            )
            order += 1
        }

        existingSeedMembers += created
        val result = existingSeedMembers
            .sortedBy { it.providerUserId }
            .take(SEED_MEMBER_COUNT)

        logger.info("[local-seed] members created={}, total={}", created.size, result.size)
        return result
    }

    private fun seedMemberStationsAndRoutes(
        members: List<MemberEntity>,
        stationLinePairs: List<StationLinePair>,
    ) {
        members.forEachIndexed { index, member ->
            val currentStations = memberStationRepository.findAllByMember(member)
            if (currentStations.isEmpty()) {
                val home = stationLinePairs[index % stationLinePairs.size].station
                val primary = pickDifferentStation(home, stationLinePairs, index + 3)

                memberStationRepository.saveAll(
                    listOf(
                        MemberStationEntity(
                            label = if (index % 2 == 0) "직장" else "학교",
                            member = member,
                            station = primary,
                        ),
                        MemberStationEntity(
                            label = "집",
                            member = member,
                            station = home,
                        ),
                    ),
                )
            }

            if (memberStationRouteRepository.countByMember(member) > 0L) {
                return@forEachIndexed
            }

            val source = stationLinePairs[index % stationLinePairs.size].station
            val destination = pickDifferentStation(source, stationLinePairs, index + 5)
            val extraDestination = pickDifferentStation(source, stationLinePairs, index + 9)

            val routes = mutableListOf<MemberStationRouteEntity>()
            if (!memberStationRouteRepository.existsByMemberAndSourceStationIdAndDestinationStationId(
                    member = member,
                    sourceStationId = source.id,
                    destinationStationId = destination.id,
                )
            ) {
                routes += MemberStationRouteEntity(
                    member = member,
                    sourceStation = source,
                    destinationStation = destination,
                    title = "$SEED_PREFIX 출퇴근 경로 ${index + 1}",
                )
            }

            if (extraDestination.id != destination.id &&
                !memberStationRouteRepository.existsByMemberAndSourceStationIdAndDestinationStationId(
                    member = member,
                    sourceStationId = source.id,
                    destinationStationId = extraDestination.id,
                )
            ) {
                routes += MemberStationRouteEntity(
                    member = member,
                    sourceStation = source,
                    destinationStation = extraDestination,
                    title = "$SEED_PREFIX 자주가는 경로 ${index + 1}",
                )
            }

            if (routes.isNotEmpty()) {
                memberStationRouteRepository.saveAll(routes)
            }
        }
    }

    private fun seedCommunityPosts(
        members: List<MemberEntity>,
        stationLinePairs: List<StationLinePair>,
    ): List<CommunityPostEntity> {
        val existing = loadSeedCommunityPosts().toMutableList()
        if (existing.size >= SEED_COMMUNITY_POST_COUNT) {
            return existing.take(SEED_COMMUNITY_POST_COUNT)
        }

        val categories = CommunityCategoryType.values()
        val addFrom = existing.size + 1
        val entities = (addFrom..SEED_COMMUNITY_POST_COUNT).map { order ->
            val pair = stationLinePairs[(order - 1) % stationLinePairs.size]
            val member = members[(order - 1) % members.size]

            CommunityPostEntity(
                title = "$SEED_PREFIX 커뮤니티 샘플 게시글 ${1000 + order}",
                content = "커뮤니티 샘플 본문 ${1000 + order}\n${pair.station.name}역(${pair.subwayLine.name}) 사용자 체감 정보를 공유해요.",
                categoryType = categories[(order - 1) % categories.size],
                member = member,
                subwayLineEntity = pair.subwayLine,
                station = pair.station,
            )
        }

        val created = if (entities.isEmpty()) {
            emptyList()
        } else {
            communityPostRepository.saveAll(entities)
        }
        existing += created
        logger.info("[local-seed] community posts created={}, total={}", created.size, existing.size)
        return existing
    }

    private fun seedComplaintPosts(
        members: List<MemberEntity>,
        stationLinePairs: List<StationLinePair>,
    ): List<ComplaintPostEntity> {
        val existing = loadSeedComplaintPosts().toMutableList()
        if (existing.size >= SEED_COMPLAINT_POST_COUNT) {
            return existing.take(SEED_COMPLAINT_POST_COUNT)
        }

        val complaintTypes = ComplaintType.values()
        val shortContents = ShortContentType.values()
        val addFrom = existing.size + 1
        val entities = (addFrom..SEED_COMPLAINT_POST_COUNT).map { order ->
            val pair = stationLinePairs[(order - 1) % stationLinePairs.size]
            val member = members[(order - 1) % members.size]

            ComplaintPostEntity(
                complaintType = complaintTypes[(order - 1) % complaintTypes.size],
                shortContentType = shortContents[(order - 1) % shortContents.size],
                content = "$SEED_PREFIX 민원 샘플 본문 ${2000 + order}\n${pair.station.name}역 인근 개선 요청입니다.",
                phoneNumber = null,
                trainNo = "2${200 + order}",
                location = (order % 8) + 1,
                member = member,
                subwayLine = pair.subwayLine,
                station = pair.station,
            )
        }

        val created = if (entities.isEmpty()) {
            emptyList()
        } else {
            complaintPostRepository.saveAll(entities)
        }
        existing += created
        logger.info("[local-seed] complaint posts created={}, total={}", created.size, existing.size)
        return existing
    }

    private fun seedLostPosts(
        members: List<MemberEntity>,
        stationLinePairs: List<StationLinePair>,
    ): List<LostPostEntity> {
        val existing = loadSeedLostPosts().toMutableList()
        if (existing.size >= SEED_LOST_POST_COUNT) {
            return existing.take(SEED_LOST_POST_COUNT)
        }

        val addFrom = existing.size + 1
        val entities = (addFrom..SEED_LOST_POST_COUNT).map { order ->
            val pair = stationLinePairs[(order - 1) % stationLinePairs.size]
            val member = members[(order - 1) % members.size]
            val lostType = if (order % 2 == 0) LostType.LOST else LostType.ACQUIRE
            val titlePrefix = if (lostType == LostType.LOST) "분실" else "습득"

            LostPostEntity(
                member = member,
                subwayLine = pair.subwayLine,
                station = pair.station,
                category = null,
                title = "$SEED_PREFIX ${titlePrefix} 샘플 게시글 ${3000 + order}",
                content = "유실물 샘플 본문 ${3000 + order}\n${pair.station.name}역에서 확인 부탁드립니다.",
                lostType = lostType,
            )
        }

        val created = if (entities.isEmpty()) {
            emptyList()
        } else {
            lostPostRepository.saveAll(entities)
        }
        existing += created
        logger.info("[local-seed] lost posts created={}, total={}", created.size, existing.size)
        return existing
    }

    private fun seedComments(
        members: List<MemberEntity>,
        communityPosts: List<CommunityPostEntity>,
        complaintPosts: List<ComplaintPostEntity>,
        lostPosts: List<LostPostEntity>,
    ): List<CommentEntity> {
        val existing = loadSeedComments().toMutableList()
        if (existing.size >= SEED_COMMENT_COUNT) {
            return existing.take(SEED_COMMENT_COUNT)
        }

        if (communityPosts.isEmpty() || complaintPosts.isEmpty() || lostPosts.isEmpty()) {
            return existing
        }

        val missing = SEED_COMMENT_COUNT - existing.size
        val topLevel = mutableListOf<CommentEntity>()
        for (idx in 0 until missing) {
            val order = existing.size + idx + 1
            val member = members[order % members.size]
            val visibility = if (order % 12 == 0) CommentVisibility.PRIVATE else CommentVisibility.PUBLIC
            val content = "$SEED_PREFIX 댓글 ${4000 + order}"

            when (order % 3) {
                0 -> {
                    val post = communityPosts[order % communityPosts.size]
                    topLevel += CommentEntity(
                        content = content,
                        imageUrls = null,
                        visibility = visibility,
                        upperComment = null,
                        communityPost = post,
                        lostPost = null,
                        complaintPost = null,
                        member = member,
                    )
                }

                1 -> {
                    val post = complaintPosts[order % complaintPosts.size]
                    topLevel += CommentEntity(
                        content = content,
                        imageUrls = null,
                        visibility = visibility,
                        upperComment = null,
                        communityPost = null,
                        lostPost = null,
                        complaintPost = post,
                        member = member,
                    )
                }

                else -> {
                    val post = lostPosts[order % lostPosts.size]
                    topLevel += CommentEntity(
                        content = content,
                        imageUrls = null,
                        visibility = visibility,
                        upperComment = null,
                        communityPost = null,
                        lostPost = post,
                        complaintPost = null,
                        member = member,
                    )
                }
            }
        }

        val savedTopLevel = if (topLevel.isEmpty()) {
            emptyList()
        } else {
            commentRepository.saveAll(topLevel)
        }

        val replies = savedTopLevel
            .filterIndexed { index, _ -> index % 6 == 0 }
            .mapIndexed { index, parent ->
                val order = existing.size + index + 1
                val replier = members[(order + 7) % members.size]
                CommentEntity(
                    content = "$SEED_PREFIX 답글 ${5000 + order}",
                    imageUrls = null,
                    visibility = if (order % 4 == 0) CommentVisibility.PRIVATE else CommentVisibility.PUBLIC,
                    upperComment = parent,
                    communityPost = parent.communityPost,
                    lostPost = parent.lostPost,
                    complaintPost = parent.complaintPost,
                    member = replier,
                )
            }

        val savedReplies = if (replies.isEmpty()) {
            emptyList()
        } else {
            commentRepository.saveAll(replies)
        }

        existing += savedTopLevel
        existing += savedReplies
        logger.info(
            "[local-seed] comments created(top={}, replies={}), total={}",
            savedTopLevel.size,
            savedReplies.size,
            existing.size,
        )
        return existing
    }

    private fun seedCommentLikes(
        members: List<MemberEntity>,
        comments: List<CommentEntity>,
    ) {
        if (comments.isEmpty()) {
            return
        }

        val existingKeys = commentLikeRepository.findAll()
            .filter { isSeedMember(it.member) && it.comment.content.startsWith(SEED_PREFIX) }
            .map { "${it.comment.id}:${it.member.id}" }
            .toMutableSet()

        if (existingKeys.size >= SEED_COMMENT_LIKE_COUNT) {
            return
        }

        val likes = mutableListOf<CommentLikeEntity>()
        comments.take(SEED_COMMENT_LIKE_SOURCE_LIMIT).forEachIndexed { index, comment ->
            if (existingKeys.size >= SEED_COMMENT_LIKE_COUNT) {
                return@forEachIndexed
            }

            val candidateMembers = listOf(
                members[(index + 3) % members.size],
                members[(index + 9) % members.size],
            ).distinctBy { it.id }

            candidateMembers.forEach { member ->
                if (member.id == comment.member.id || existingKeys.size >= SEED_COMMENT_LIKE_COUNT) {
                    return@forEach
                }

                val key = "${comment.id}:${member.id}"
                if (existingKeys.add(key)) {
                    likes += CommentLikeEntity.of(
                        comment = comment,
                        member = member,
                        isLike = YNType.Y,
                    )
                }
            }
        }

        if (likes.isNotEmpty()) {
            commentLikeRepository.saveAll(likes)
        }
        logger.info("[local-seed] comment likes created={}, total={}", likes.size, existingKeys.size)
    }

    private fun seedArticleReactions(
        members: List<MemberEntity>,
        communityPosts: List<CommunityPostEntity>,
        complaintPosts: List<ComplaintPostEntity>,
        lostPosts: List<LostPostEntity>,
    ) {
        seedArticleLikes(members, communityPosts, complaintPosts, lostPosts)
        seedArticleBookmarks(members, communityPosts, complaintPosts, lostPosts)
    }

    private fun seedArticleLikes(
        members: List<MemberEntity>,
        communityPosts: List<CommunityPostEntity>,
        complaintPosts: List<ComplaintPostEntity>,
        lostPosts: List<LostPostEntity>,
    ) {
        val existingKeys = articleLikeRepository.findAll()
            .filter { isSeedMember(it.member) }
            .map { "${it.articleType}:${it.articleId}:${it.member.id}" }
            .toMutableSet()

        if (existingKeys.size >= SEED_ARTICLE_LIKE_COUNT) {
            return
        }

        val entities = mutableListOf<ArticleLikeEntity>()
        val addLike: (ArticleType, Long, MemberEntity) -> Unit = { articleType, articleId, member ->
            if (existingKeys.size < SEED_ARTICLE_LIKE_COUNT) {
                val key = "$articleType:$articleId:${member.id}"
                if (existingKeys.add(key)) {
                    entities += ArticleLikeEntity.of(
                        articleType = articleType,
                        articleId = articleId,
                        member = member,
                    )
                }
            }
        }

        communityPosts.take(SEED_REACTION_POST_LIMIT).forEachIndexed { index, post ->
            addLike(ArticleType.COMMUNITY, post.id, members[(index + 2) % members.size])
            addLike(ArticleType.COMMUNITY, post.id, members[(index + 7) % members.size])
        }
        complaintPosts.take(SEED_REACTION_POST_LIMIT).forEachIndexed { index, post ->
            addLike(ArticleType.COMPLAINT, post.id, members[(index + 3) % members.size])
            addLike(ArticleType.COMPLAINT, post.id, members[(index + 8) % members.size])
        }
        lostPosts.take(SEED_REACTION_POST_LIMIT).forEachIndexed { index, post ->
            addLike(ArticleType.LOST, post.id, members[(index + 4) % members.size])
            addLike(ArticleType.LOST, post.id, members[(index + 9) % members.size])
        }

        if (entities.isNotEmpty()) {
            articleLikeRepository.saveAll(entities)
        }
        logger.info("[local-seed] article likes created={}, total={}", entities.size, existingKeys.size)
    }

    private fun seedArticleBookmarks(
        members: List<MemberEntity>,
        communityPosts: List<CommunityPostEntity>,
        complaintPosts: List<ComplaintPostEntity>,
        lostPosts: List<LostPostEntity>,
    ) {
        val existingKeys = articleBookmarkRepository.findAll()
            .filter { isSeedMember(it.member) }
            .map { "${it.articleType}:${it.articleId}:${it.member.id}" }
            .toMutableSet()

        if (existingKeys.size >= SEED_ARTICLE_BOOKMARK_COUNT) {
            return
        }

        val entities = mutableListOf<ArticleBookmarkEntity>()
        val addBookmark: (ArticleType, Long, MemberEntity) -> Unit = { articleType, articleId, member ->
            if (existingKeys.size < SEED_ARTICLE_BOOKMARK_COUNT) {
                val key = "$articleType:$articleId:${member.id}"
                if (existingKeys.add(key)) {
                    entities += ArticleBookmarkEntity.of(
                        articleType = articleType,
                        articleId = articleId,
                        member = member,
                    )
                }
            }
        }

        communityPosts.take(SEED_REACTION_POST_LIMIT).forEachIndexed { index, post ->
            addBookmark(ArticleType.COMMUNITY, post.id, members[(index + 1) % members.size])
        }
        complaintPosts.take(SEED_REACTION_POST_LIMIT).forEachIndexed { index, post ->
            addBookmark(ArticleType.COMPLAINT, post.id, members[(index + 2) % members.size])
        }
        lostPosts.take(SEED_REACTION_POST_LIMIT).forEachIndexed { index, post ->
            addBookmark(ArticleType.LOST, post.id, members[(index + 3) % members.size])
        }

        if (entities.isNotEmpty()) {
            articleBookmarkRepository.saveAll(entities)
        }
        logger.info("[local-seed] article bookmarks created={}, total={}", entities.size, existingKeys.size)
    }

    private fun seedMessageRoomsAndMessages(members: List<MemberEntity>) {
        val hasSeedRooms = messageRoomRepository.findAll().any { room ->
            isSeedMember(room.memberA) && isSeedMember(room.memberB)
        }
        if (hasSeedRooms) {
            return
        }

        val rooms = mutableListOf<MessageRoomEntity>()
        val roomKeys = mutableSetOf<String>()
        val roomCount = SEED_MESSAGE_ROOM_COUNT.coerceAtMost(members.size)

        for (index in 0 until roomCount) {
            val memberA = members[index % members.size]
            var memberB = members[(index + 7) % members.size]
            if (memberA.id == memberB.id) {
                memberB = members[(index + 1) % members.size]
            }

            val key = listOf(memberA.id, memberB.id).sorted().joinToString(":")
            if (roomKeys.add(key)) {
                rooms += MessageRoomEntity.of(memberA = memberA, memberB = memberB)
            }
        }

        val savedRooms = messageRoomRepository.saveAll(rooms)
        val messages = mutableListOf<MessageEntity>()

        savedRooms.forEachIndexed { roomIndex, room ->
            for (seq in 1..SEED_MESSAGES_PER_ROOM) {
                val sender = if (seq % 2 == 0) room.memberA else room.memberB
                val content = "$SEED_PREFIX 쪽지방 ${roomIndex + 1} 메시지 $seq"
                val message = MessageEntity.of(
                    messageRoom = room,
                    senderMember = sender,
                    content = content,
                )
                if (seq <= SEED_MESSAGES_PER_ROOM - 2) {
                    message.markRead()
                }
                messages += message

                val minutesAgo = ((savedRooms.size - roomIndex) * 10L + (SEED_MESSAGES_PER_ROOM - seq)).coerceAtLeast(1L)
                room.updateLastMessage(content = content, createdAt = LocalDateTime.now().minusMinutes(minutesAgo))
            }
        }

        if (messages.isNotEmpty()) {
            messageRepository.saveAll(messages)
            messageRoomRepository.saveAll(savedRooms)
        }
        logger.info("[local-seed] message rooms={}, messages={}", savedRooms.size, messages.size)
    }

    private fun seedDailyVoteData(
        members: List<MemberEntity>,
        stationLinePairs: List<StationLinePair>,
    ) {
        val pollDate = LocalDate.now()
        val slots = listOf(DailyVoteSlotType.MORNING, DailyVoteSlotType.EVENING)
        val targetPairs = stationLinePairs.take(SEED_DAILY_VOTE_PAIR_COUNT)

        targetPairs.forEachIndexed { index, pair ->
            slots.forEach { slot ->
                val primaryContext = if (index % 2 == 0) DailyVoteContextType.COMMUTE else DailyVoteContextType.SCHOOL
                val secondaryContext = if (primaryContext == DailyVoteContextType.COMMUTE) {
                    DailyVoteContextType.SCHOOL
                } else {
                    DailyVoteContextType.COMMUTE
                }

                val mainPrimary = findOrCreateDailyVotePoll(
                    pollDate = pollDate,
                    pollSlot = slot,
                    pollContext = primaryContext,
                    pollKind = DailyVoteKindType.MAIN,
                    stationLinePair = pair,
                    isPrimary = true,
                    question = buildMainQuestion(slot, primaryContext, pair.subwayLine.name),
                )
                val mainSecondary = findOrCreateDailyVotePoll(
                    pollDate = pollDate,
                    pollSlot = slot,
                    pollContext = secondaryContext,
                    pollKind = DailyVoteKindType.MAIN,
                    stationLinePair = pair,
                    isPrimary = false,
                    question = buildMainQuestion(slot, secondaryContext, pair.subwayLine.name),
                )
                val stationDiary = findOrCreateDailyVotePoll(
                    pollDate = pollDate,
                    pollSlot = slot,
                    pollContext = primaryContext,
                    pollKind = DailyVoteKindType.STATION_DIARY,
                    stationLinePair = pair,
                    isPrimary = true,
                    question = "오늘의 ${pair.station.name}역은 어떠셨나요?",
                )

                listOf(mainPrimary, mainSecondary, stationDiary).forEach { poll ->
                    seedDailyVoteResponses(members, poll)
                    seedDailyVoteComments(members, poll)
                }
            }
        }
    }

    private fun findOrCreateDailyVotePoll(
        pollDate: LocalDate,
        pollSlot: DailyVoteSlotType,
        pollContext: DailyVoteContextType,
        pollKind: DailyVoteKindType,
        stationLinePair: StationLinePair,
        isPrimary: Boolean,
        question: String,
    ): DailyVotePollEntity {
        val existing = dailyVotePollRepository.findByPollDateAndPollSlotAndPollContextAndPollKindAndStationIdAndSubwayLineIdAndPrimaryYn(
            pollDate = pollDate,
            pollSlot = pollSlot,
            pollContext = pollContext,
            pollKind = pollKind,
            stationId = stationLinePair.station.id,
            subwayLineId = stationLinePair.subwayLine.id,
            primaryYn = if (isPrimary) YNType.Y else YNType.N,
        )
        if (existing != null) {
            return existing
        }

        return dailyVotePollRepository.save(
            DailyVotePollEntity.of(
                pollDate = pollDate,
                pollSlot = pollSlot,
                pollContext = pollContext,
                pollKind = pollKind,
                station = stationLinePair.station,
                subwayLine = stationLinePair.subwayLine,
                question = question,
                isPrimary = isPrimary,
            ),
        )
    }

    private fun seedDailyVoteResponses(
        members: List<MemberEntity>,
        poll: DailyVotePollEntity,
    ) {
        if (dailyVoteResponseRepository.findByPollId(poll.id).isNotEmpty()) {
            return
        }

        val participants = members.take(SEED_DAILY_VOTE_RESPONSE_COUNT.coerceAtMost(members.size))
        val entities = participants.mapIndexed { index, member ->
            DailyVoteResponseEntity.of(
                poll = poll,
                member = member,
                optionCode = DAILY_VOTE_OPTIONS[(index + poll.id.toInt()) % DAILY_VOTE_OPTIONS.size],
            )
        }
        if (entities.isNotEmpty()) {
            dailyVoteResponseRepository.saveAll(entities)
        }
    }

    private fun seedDailyVoteComments(
        members: List<MemberEntity>,
        poll: DailyVotePollEntity,
    ) {
        val existing = dailyVoteCommentRepository.findByPollIdAndStatusOrderByCreatedAtDesc(
            pollId = poll.id,
            status = DailyVoteCommentStatusType.CREATED,
        )
        if (existing.isNotEmpty()) {
            return
        }

        val comments = (1..SEED_DAILY_VOTE_COMMENT_COUNT).map { order ->
            val member = members[(poll.id.toInt() + order) % members.size]
            val phase = if (poll.pollSlot == DailyVoteSlotType.MORNING) "아침" else "저녁"
            DailyVoteCommentEntity.of(
                poll = poll,
                member = member,
                content = "$SEED_PREFIX ${poll.station.name} ${poll.subwayLine.name} $phase 체감 공유 ${order}",
                imageUrls = null,
            )
        }
        val savedComments = if (comments.isEmpty()) {
            emptyList()
        } else {
            dailyVoteCommentRepository.saveAll(comments)
        }

        val likes = mutableListOf<DailyVoteCommentLikeEntity>()
        savedComments.forEachIndexed { index, comment ->
            val firstMember = members[(index + 2) % members.size]
            val secondMember = members[(index + 6) % members.size]

            if (firstMember.id != comment.member.id &&
                dailyVoteCommentLikeRepository.findByCommentIdAndMemberId(comment.id, firstMember.id) == null
            ) {
                likes += DailyVoteCommentLikeEntity.of(comment = comment, member = firstMember)
            }
            if (secondMember.id != comment.member.id &&
                dailyVoteCommentLikeRepository.findByCommentIdAndMemberId(comment.id, secondMember.id) == null
            ) {
                likes += DailyVoteCommentLikeEntity.of(comment = comment, member = secondMember)
            }
        }
        if (likes.isNotEmpty()) {
            dailyVoteCommentLikeRepository.saveAll(likes)
        }
    }

    private fun seedStationSocialMeetups(
        members: List<MemberEntity>,
        stationLinePairs: List<StationLinePair>,
    ) {
        val existing = loadSeedStationSocialMeetups().toMutableList()
        if (existing.size >= SEED_MEETUP_COUNT) {
            return
        }

        val meetupPairs = resolveMeetupPairs(stationLinePairs)
        val addFrom = existing.size + 1
        val entities = (addFrom..SEED_MEETUP_COUNT).map { order ->
            val pair = meetupPairs[(order - 1) % meetupPairs.size]
            val hostMember = members[(order - 1) % members.size]
            val nationalCode = NATIONALITY_CODES[(order - 1) % NATIONALITY_CODES.size]
            val meetupAt = LocalDateTime.now()
                .plusDays(((order - 1) % 10 + 1).toLong())
                .withHour(18 + ((order - 1) % 4))
                .withMinute(30)

            StationSocialMeetupEntity.of(
                station = pair.station,
                subwayLine = pair.subwayLine,
                hostMember = hostMember,
                title = "$SEED_PREFIX ${pair.station.name} 언어교환/친목 모임 ${6000 + order}",
                description = "${pair.station.name}역 근처에서 언어교환과 친목 모임을 진행해요. 초보자도 환영합니다.",
                meetupAt = meetupAt,
                maxParticipants = 8 + (order % 5),
                nationalityCode = nationalCode,
                sameNationalityOnlyYn = if (order % 4 == 0) YNType.Y else YNType.N,
            )
        }

        val created = if (entities.isEmpty()) {
            emptyList()
        } else {
            stationSocialMeetupRepository.saveAll(entities)
        }
        existing += created

        val participants = mutableListOf<StationSocialMeetupParticipantEntity>()
        existing.forEachIndexed { meetupIndex, meetup ->
            for (offset in 1..SEED_MEETUP_PARTICIPANT_COUNT) {
                val member = members[(meetupIndex + offset + 4) % members.size]
                if (member.id == meetup.hostMember.id) {
                    continue
                }
                if (stationSocialMeetupParticipantRepository.findByMeetupIdAndMemberId(meetup.id, member.id) != null) {
                    continue
                }

                val status = if (offset <= 3) {
                    StationSocialParticipantStatusType.ACCEPTED
                } else {
                    StationSocialParticipantStatusType.REQUESTED
                }
                val nationalityCode = NATIONALITY_CODES[(meetupIndex + offset) % NATIONALITY_CODES.size]

                participants += StationSocialMeetupParticipantEntity.of(
                    meetup = meetup,
                    member = member,
                    status = status,
                    introductionMessage = "같이 지하철 타고 이동하며 대화해요! (${meetup.station.name})",
                    nationalityCode = nationalityCode,
                    matchOpenYn = YNType.Y,
                )
            }
        }

        if (participants.isNotEmpty()) {
            stationSocialMeetupParticipantRepository.saveAll(participants)
        }
        logger.info(
            "[local-seed] social meetups created={}, total={}, participants created={}",
            created.size,
            existing.size,
            participants.size,
        )
    }

    private fun resolveMeetupPairs(stationLinePairs: List<StationLinePair>): List<StationLinePair> {
        val hotspotPairs = HOTSPOT_STATION_NAMES.mapNotNull { hotspot ->
            stationLinePairs.firstOrNull { it.station.name == hotspot }
        }

        return (hotspotPairs + stationLinePairs)
            .distinctBy { "${it.station.id}:${it.subwayLine.id}" }
    }

    private fun pickDifferentStation(
        baseStation: StationEntity,
        stationLinePairs: List<StationLinePair>,
        startIndex: Int,
    ): StationEntity {
        for (offset in stationLinePairs.indices) {
            val candidate = stationLinePairs[(startIndex + offset) % stationLinePairs.size].station
            if (candidate.id != baseStation.id) {
                return candidate
            }
        }
        return baseStation
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

    private fun loadSeedCommunityPosts(): List<CommunityPostEntity> {
        return communityPostRepository.findAll()
            .filter { it.title.startsWith(SEED_PREFIX) }
            .sortedBy { it.id }
    }

    private fun loadSeedComplaintPosts(): List<ComplaintPostEntity> {
        return complaintPostRepository.findAll()
            .filter { it.content.startsWith(SEED_PREFIX) }
            .sortedBy { it.id }
    }

    private fun loadSeedLostPosts(): List<LostPostEntity> {
        return lostPostRepository.findAll()
            .filter { it.title.startsWith(SEED_PREFIX) }
            .sortedBy { it.id }
    }

    private fun loadSeedComments(): List<CommentEntity> {
        return commentRepository.findAll()
            .filter { it.content.startsWith(SEED_PREFIX) }
            .sortedBy { it.id }
    }

    private fun loadSeedStationSocialMeetups(): List<StationSocialMeetupEntity> {
        return stationSocialMeetupRepository.findAll()
            .filter { it.title.startsWith(SEED_PREFIX) }
            .sortedBy { it.id }
    }

    private fun isSeedMember(member: MemberEntity): Boolean {
        return member.providerUserId.startsWith(SEED_MEMBER_PREFIX)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(LocalMockContentSeeder::class.java)

        private const val SEED_PREFIX = "[LOCAL-SEED]"
        private const val SEED_MEMBER_PREFIX = "local-seed-member-"
        private const val SEED_MEMBER_COUNT = 36
        private const val SEED_STATION_LINE_PAIR_COUNT = 20

        private const val SEED_COMMUNITY_POST_COUNT = 80
        private const val SEED_COMPLAINT_POST_COUNT = 60
        private const val SEED_LOST_POST_COUNT = 60
        private const val SEED_COMMENT_COUNT = 260
        private const val SEED_COMMENT_LIKE_COUNT = 320
        private const val SEED_COMMENT_LIKE_SOURCE_LIMIT = 180

        private const val SEED_ARTICLE_LIKE_COUNT = 260
        private const val SEED_ARTICLE_BOOKMARK_COUNT = 190
        private const val SEED_REACTION_POST_LIMIT = 60

        private const val SEED_MESSAGE_ROOM_COUNT = 18
        private const val SEED_MESSAGES_PER_ROOM = 9

        private const val SEED_DAILY_VOTE_PAIR_COUNT = 6
        private const val SEED_DAILY_VOTE_RESPONSE_COUNT = 20
        private const val SEED_DAILY_VOTE_COMMENT_COUNT = 8

        private const val SEED_MEETUP_COUNT = 28
        private const val SEED_MEETUP_PARTICIPANT_COUNT = 5

        private val DAILY_VOTE_OPTIONS = listOf("TERRIBLE", "HARD", "NORMAL", "GOOD", "GREAT")
        private val AGE_RANGES = listOf("20", "30", "40", "50")
        private val NATIONALITY_CODES = listOf("KR", "JP", "CN", "US", "VN", null)

        private val PREFERRED_STATION_NAMES = listOf(
            "강남",
            "서초",
            "명동",
            "홍대입구",
            "성수",
            "서울역",
            "잠실",
            "신림",
            "이태원",
            "안암",
        )

        private val HOTSPOT_STATION_NAMES = listOf(
            "명동",
            "성수",
            "홍대입구",
            "이태원",
            "강남",
        )
    }
}
