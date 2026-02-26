package backend.team.ahachul_backend.api.member.application.service

import backend.team.ahachul_backend.api.article.application.port.out.ArticleBookmarkReader
import backend.team.ahachul_backend.api.article.application.port.out.ArticleLikeReader
import backend.team.ahachul_backend.api.article.domain.model.ArticleType
import backend.team.ahachul_backend.api.comment.application.port.out.CommentReader
import backend.team.ahachul_backend.api.comment.domain.model.CommentType
import backend.team.ahachul_backend.api.common.application.port.out.StationReader
import backend.team.ahachul_backend.api.common.application.port.out.SubwayLineStationReader
import backend.team.ahachul_backend.api.common.domain.entity.StationEntity
import backend.team.ahachul_backend.api.community.application.port.out.CommunityPostReader
import backend.team.ahachul_backend.api.community.domain.model.CommunityPostType
import backend.team.ahachul_backend.api.complaint.application.port.out.ComplaintPostReader
import backend.team.ahachul_backend.api.complaint.domain.model.ComplaintPostType
import backend.team.ahachul_backend.api.lost.application.port.out.LostPostReader
import backend.team.ahachul_backend.api.lost.domain.model.LostPostType
import backend.team.ahachul_backend.api.member.adapter.web.`in`.dto.*
import backend.team.ahachul_backend.api.member.application.command.BookmarkStationCommand
import backend.team.ahachul_backend.api.member.application.command.BookmarkStationLocationMetaCommand
import backend.team.ahachul_backend.api.member.application.command.SearchMemberCommand
import backend.team.ahachul_backend.api.member.application.command.CreateFavoriteRouteCommand
import backend.team.ahachul_backend.api.member.application.port.`in`.MemberUseCase
import backend.team.ahachul_backend.api.member.application.command.BookmarkStationCommands
import backend.team.ahachul_backend.api.member.application.port.`in`.command.CheckNicknameCommand
import backend.team.ahachul_backend.api.member.application.port.`in`.command.UpdateMemberCommand
import backend.team.ahachul_backend.api.member.application.port.out.FcmTokenWriter
import backend.team.ahachul_backend.api.member.application.port.out.MemberReader
import backend.team.ahachul_backend.api.member.application.port.out.MemberStationRouteReader
import backend.team.ahachul_backend.api.member.application.port.out.MemberStationRouteWriter
import backend.team.ahachul_backend.api.member.application.port.out.MemberStationReader
import backend.team.ahachul_backend.api.member.application.port.out.MemberStationWriter
import backend.team.ahachul_backend.api.member.domain.entity.FcmTokenEntity
import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity
import backend.team.ahachul_backend.api.member.domain.entity.MemberStationRouteEntity
import backend.team.ahachul_backend.api.member.domain.entity.MemberStationEntity
import backend.team.ahachul_backend.common.exception.BusinessException
import backend.team.ahachul_backend.common.response.ResponseCode
import backend.team.ahachul_backend.common.utils.RequestUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.text.Normalizer
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.PriorityQueue

@Service
@Transactional(readOnly = true)
class MemberService(
    private val memberReader: MemberReader,
    private val stationReader: StationReader,
    private val memberStationWriter: MemberStationWriter,
    private val memberStationReader: MemberStationReader,
    private val memberStationRouteReader: MemberStationRouteReader,
    private val memberStationRouteWriter: MemberStationRouteWriter,
    private val subwayLineStationReader: SubwayLineStationReader,
    private val fcmTokenWriter: FcmTokenWriter,
    private val authLogoutCacheUtils: AuthLogoutCacheUtils,
    private val articleLikeReader: ArticleLikeReader,
    private val articleBookmarkReader: ArticleBookmarkReader,
    private val communityPostReader: CommunityPostReader,
    private val complaintPostReader: ComplaintPostReader,
    private val lostPostReader: LostPostReader,
    private val commentReader: CommentReader,
) : MemberUseCase {
    companion object {
        private const val NICKNAME_MIN_LENGTH = 2
        private const val NICKNAME_MAX_LENGTH = 10
        private const val MAX_FAVORITE_ROUTE_COUNT = 10
        private const val ROUTE_CONNECTION_SOURCE_MAX_DISTANCE = 1
        private const val ROUTE_CONNECTION_DESTINATION_MAX_DISTANCE = 1
        private const val ROUTE_CONNECTION_TOTAL_MAX_DISTANCE = 2
        private const val DEFAULT_COMMUTE_WALKING_MINUTES = 15
        private val DEFAULT_COMMUTE_ARRIVAL_TIME: LocalTime = LocalTime.of(9, 0)
        private const val DEFAULT_TIMEZONE = "Asia/Seoul"
        private val NICKNAME_REGEX = Regex("^[가-힣a-zA-Z0-9_]+$")
    }

    private data class GraphEdge(
        val fromStationId: Long,
        val toStationId: Long,
        val subwayLineId: Long,
        val subwayLineName: String,
    )

    private data class RouteState(
        val stationId: Long,
        val lineId: Long?,
    )

    private data class RouteMetric(
        val stops: Int,
        val transfers: Int,
    ) : Comparable<RouteMetric> {
        override fun compareTo(other: RouteMetric): Int {
            if (stops != other.stops) {
                return stops.compareTo(other.stops)
            }
            return transfers.compareTo(other.transfers)
        }
    }

    private data class StateWithMetric(
        val state: RouteState,
        val metric: RouteMetric,
    ) : Comparable<StateWithMetric> {
        override fun compareTo(other: StateWithMetric): Int {
            return metric.compareTo(other.metric)
        }
    }

    private data class Prev(
        val previousState: RouteState,
        val edge: GraphEdge,
    )

    private data class PathResult(
        val nodes: List<Long>,
        val edges: List<GraphEdge>,
        val metric: RouteMetric,
        val stationNamesById: Map<Long, String>,
    )

    private data class RouteConnectionCandidate(
        val member: MemberEntity,
        val route: MemberStationRouteEntity,
        val sourceDistance: Int,
        val destinationDistance: Int,
        val totalDistance: Int,
        val matchScore: Int,
    )

    private data class CommuteWalkingLegDecision(
        val stationId: Long,
        val stationName: String,
        val walkingMinutes: Int,
        val source: CommuteCoachDto.WalkingMinutesSource,
        val updatedAt: String?,
    )

    private val commuteTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    override fun getMember(): GetMemberDto.Response {
        val member = memberReader.getMember(RequestUtils.getAttribute(RequestUtils.Attribute.MEMBER_ID)!!.toLong())
        return GetMemberDto.Response.of(member)
    }

    @Transactional
    override fun updateMember(command: UpdateMemberCommand): UpdateMemberDto.Response {
        val member = memberReader.getMember(RequestUtils.getAttribute(RequestUtils.Attribute.MEMBER_ID)!!.toLong())
        command.nickname?.let {
            val normalizedNickname = normalizeInput(it)
            validateNickname(normalizedNickname)

            val isChangedNickname = member.nickname != normalizedNickname
            if (
                isChangedNickname &&
                memberReader.existMemberByNicknameExceptMemberId(normalizedNickname, member.id)
            ) {
                throw BusinessException(ResponseCode.DUPLICATE_NICKNAME)
            }

            member.changeNickname(normalizedNickname)
        }
        command.gender?.let { member.changeGender(it) }
        command.ageRange?.let { member.changeAgeRange(it) }
        command.profilePublic?.let { member.changeProfilePublic(it) }
        command.emailPublic?.let { member.changeEmailPublic(it) }
        command.genderAgePublic?.let { member.changeGenderAgePublic(it) }
        command.postsPublic?.let { member.changeActivityPostsPublic(it) }
        command.commentsPublic?.let { member.changeActivityCommentsPublic(it) }
        command.imageUrl?.let { member.changeImageUrl(normalizeOptionalInput(it)) }

        return UpdateMemberDto.Response.of(
                nickname = member.nickname,
                gender = member.gender,
                ageRange = member.ageRange,
                profilePublic = member.isProfilePublic(),
                emailPublic = member.isEmailPublic(),
                genderAgePublic = member.isGenderAgePublic(),
                postsPublic = member.isActivityPostsPublic(),
                commentsPublic = member.isActivityCommentsPublic(),
                imageUrl = member.imageUrl,
        )
    }

    override fun getMemberProfile(nickname: String, asPublic: Boolean, limit: Int): GetMemberProfileDto.Response {
        val targetMember = memberReader.getMemberByNickname(normalizeInput(nickname))
        val loginMemberId = RequestUtils.getAttribute(RequestUtils.Attribute.MEMBER_ID)?.toLongOrNull()
        val isMine = loginMemberId != null && loginMemberId == targetMember.id
        val enforcePublicPolicy = asPublic || !isMine
        val normalizedLimit = limit.coerceIn(1, 50)

        val profileVisible = !enforcePublicPolicy || targetMember.isProfilePublic()
        val postsVisible = profileVisible && (!enforcePublicPolicy || targetMember.isActivityPostsPublic())
        val commentsVisible = profileVisible && (!enforcePublicPolicy || targetMember.isActivityCommentsPublic())
        val emailVisible = profileVisible && (!enforcePublicPolicy || targetMember.isEmailPublic())
        val genderAgeVisible = profileVisible && (!enforcePublicPolicy || targetMember.isGenderAgePublic())

        val postActivities = if (postsVisible) {
            buildRecentPostActivities(targetMember.id, normalizedLimit)
        } else {
            emptyList()
        }

        val commentActivities = if (commentsVisible) {
            buildRecentCommentActivities(targetMember.id, normalizedLimit, enforcePublicPolicy)
        } else {
            emptyList()
        }

        return GetMemberProfileDto.Response(
            memberId = targetMember.id,
            nickname = targetMember.nickname,
            imageUrl = if (profileVisible) targetMember.imageUrl else null,
            email = if (emailVisible) targetMember.email else null,
            maskedEmail = if (emailVisible) maskEmail(targetMember.email) else null,
            gender = if (genderAgeVisible) targetMember.gender else null,
            ageRange = if (genderAgeVisible) targetMember.ageRange else null,
            isMine = isMine,
            visibility = GetMemberProfileDto.Visibility(
                profilePublic = targetMember.isProfilePublic(),
                emailPublic = targetMember.isEmailPublic(),
                genderAgePublic = targetMember.isGenderAgePublic(),
                postsPublic = targetMember.isActivityPostsPublic(),
                commentsPublic = targetMember.isActivityCommentsPublic(),
                profileVisible = profileVisible,
                postsVisible = postsVisible,
                commentsVisible = commentsVisible,
            ),
            activities = GetMemberProfileDto.Activities(
                posts = postActivities,
                comments = commentActivities,
            )
        )
    }

    @Transactional
    override fun deleteMember(request: DeleteMemberDto.Request) {
        val member = memberReader.getMember(RequestUtils.getAttribute(RequestUtils.Attribute.MEMBER_ID)!!.toLong())
        member.delete()

        authLogoutCacheUtils.logout(request.accessToken)
    }

    override fun checkNickname(command: CheckNicknameCommand): CheckNicknameDto.Response {
        val normalizedNickname = normalizeInput(command.nickname)
        validateNickname(normalizedNickname)

        return CheckNicknameDto.Response.of(
            available = !memberReader.existMember(normalizedNickname)
        )
    }

    @Transactional
    override fun bookmarkStation(command: BookmarkStationCommands): GetBookmarkStationDto.Response {
        validateDuplicateBookmarkStations(command.stations)

        val member = memberReader.getMember(RequestUtils.getAttribute(RequestUtils.Attribute.MEMBER_ID)!!.toLong())
        val bookmarkStations = memberStationReader.getByMember(member)

        if (isEqualsAlreadyRegisteredStation(bookmarkStations, command.stations)) {
            return createBookmarkStationResponse(bookmarkStations)
        }

        if (bookmarkStations.isNotEmpty()) {
            memberStationWriter.deleteAllByMember(member)
        }

        val savedMemberStations = saveNewStations(member, command.stations)
        return createBookmarkStationResponse(savedMemberStations)
    }

    override fun getBookmarkStation(): GetBookmarkStationDto.Response {
        val member = memberReader.getMember(RequestUtils.getAttribute(RequestUtils.Attribute.MEMBER_ID)!!.toLong())

        val bookmarkStations = memberStationReader.getByMember(member)

        return createBookmarkStationResponse(bookmarkStations)
    }

    override fun getFavoriteRouteRecommendations(limit: Int): FavoriteRouteDto.GraphResponse {
        val member = getCurrentMember()
        val favorites = memberStationReader.getByMember(member)
        if (favorites.size < 2) {
            return FavoriteRouteDto.GraphResponse(routes = emptyList())
        }

        val favoriteStationIds = favorites.map { it.station.id }.toSet()
        val anchor = favorites.first().station
        val normalizedLimit = limit.coerceIn(1, 6)

        val routes = favorites
            .drop(1)
            .take(normalizedLimit)
            .mapNotNull { destination ->
                runCatching {
                    buildRoute(
                        sourceStationId = anchor.id,
                        sourceStationName = anchor.name,
                        destinationStationId = destination.station.id,
                        destinationStationName = destination.station.name,
                        favoriteStationIds = favoriteStationIds,
                        routeType = FavoriteRouteDto.RouteType.RECOMMENDED,
                        routeId = null,
                        title = null,
                    )
                }.getOrNull()
            }

        return FavoriteRouteDto.GraphResponse(routes = routes)
    }

    override fun getFavoriteRoutes(): FavoriteRouteDto.GraphResponse {
        val member = getCurrentMember()
        val favorites = memberStationReader.getByMember(member)
        val favoriteStationIds = favorites.map { it.station.id }.toSet()
        val routes = memberStationRouteReader.findAllByMember(member).map { saved ->
            buildRoute(
                sourceStationId = saved.sourceStation.id,
                sourceStationName = saved.sourceStation.name,
                destinationStationId = saved.destinationStation.id,
                destinationStationName = saved.destinationStation.name,
                favoriteStationIds = favoriteStationIds,
                routeType = FavoriteRouteDto.RouteType.CUSTOM,
                routeId = saved.id,
                title = saved.title,
            )
        }
        return FavoriteRouteDto.GraphResponse(routes = routes)
    }

    override fun getRouteConnectionRecommendations(limit: Int, groupLimit: Int): RouteConnectionDto.Response {
        val member = getCurrentMember()
        val anchorRoute = resolveAnchorRoute(member) ?: return RouteConnectionDto.Response(
            generatedAt = ZonedDateTime.now(ZoneId.of(DEFAULT_TIMEZONE)).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
            matchingPolicy = RouteConnectionDto.MatchingPolicy(
                sourceMaxDistance = ROUTE_CONNECTION_SOURCE_MAX_DISTANCE,
                destinationMaxDistance = ROUTE_CONNECTION_DESTINATION_MAX_DISTANCE,
                totalMaxDistance = ROUTE_CONNECTION_TOTAL_MAX_DISTANCE,
            ),
            anchorRoute = null,
            recommendations = emptyList(),
            groups = emptyList(),
            graph = RouteConnectionDto.SocialGraph(
                nodes = listOf(
                    RouteConnectionDto.SocialGraphNode(
                        memberId = member.id,
                        nickname = resolveDisplayNickname(member),
                        me = true,
                    )
                ),
                edges = emptyList(),
            ),
        )

        val normalizedLimit = limit.coerceIn(1, 50)
        val normalizedGroupLimit = groupLimit.coerceIn(1, 20)
        val others = memberStationRouteReader.findAllByMemberIdNot(member.id)
        if (others.isEmpty()) {
            return RouteConnectionDto.Response(
                generatedAt = ZonedDateTime.now(ZoneId.of(DEFAULT_TIMEZONE)).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                matchingPolicy = RouteConnectionDto.MatchingPolicy(
                    sourceMaxDistance = ROUTE_CONNECTION_SOURCE_MAX_DISTANCE,
                    destinationMaxDistance = ROUTE_CONNECTION_DESTINATION_MAX_DISTANCE,
                    totalMaxDistance = ROUTE_CONNECTION_TOTAL_MAX_DISTANCE,
                ),
                anchorRoute = RouteConnectionDto.AnchorRoute(
                    sourceStationId = anchorRoute.sourceStationId,
                    sourceStationName = anchorRoute.sourceStationName,
                    destinationStationId = anchorRoute.destinationStationId,
                    destinationStationName = anchorRoute.destinationStationName,
                ),
                recommendations = emptyList(),
                groups = emptyList(),
                graph = RouteConnectionDto.SocialGraph(
                    nodes = listOf(
                        RouteConnectionDto.SocialGraphNode(
                            memberId = member.id,
                            nickname = resolveDisplayNickname(member),
                            me = true,
                        )
                    ),
                    edges = emptyList(),
                ),
            )
        }

        val candidates = others.groupBy { it.member.id }
            .values
            .mapNotNull { routes -> resolveBestRouteConnectionCandidate(anchorRoute, routes) }
            .sortedWith(
                compareByDescending<RouteConnectionCandidate> { it.matchScore }
                    .thenBy { it.totalDistance }
                    .thenBy { it.member.id }
            )
            .take(normalizedLimit)

        val recommendations = candidates.map { candidate ->
            RouteConnectionDto.MemberRecommendation(
                memberId = candidate.member.id,
                nickname = resolveDisplayNickname(candidate.member),
                routeId = candidate.route.id,
                title = candidate.route.title,
                sourceStationId = candidate.route.sourceStation.id,
                sourceStationName = candidate.route.sourceStation.name,
                destinationStationId = candidate.route.destinationStation.id,
                destinationStationName = candidate.route.destinationStation.name,
                sourceDistance = candidate.sourceDistance,
                destinationDistance = candidate.destinationDistance,
                totalDistance = candidate.totalDistance,
                matchScore = candidate.matchScore,
                estimatedMinutes = estimateRouteMinutes(candidate.totalDistance, transferCount = 0),
                reason = buildRouteConnectionReason(candidate),
            )
        }

        val groups = candidates.groupBy { candidate ->
            "${candidate.route.sourceStation.id}-${candidate.route.destinationStation.id}"
        }
            .values
            .sortedByDescending { groupCandidates -> groupCandidates.size }
            .take(normalizedGroupLimit)
            .map { groupCandidates ->
                val first = groupCandidates.first()
                RouteConnectionDto.RouteGroup(
                    groupId = "${first.route.sourceStation.id}-${first.route.destinationStation.id}",
                    sourceStationId = first.route.sourceStation.id,
                    sourceStationName = first.route.sourceStation.name,
                    destinationStationId = first.route.destinationStation.id,
                    destinationStationName = first.route.destinationStation.name,
                    memberCount = groupCandidates.size,
                    members = groupCandidates.sortedByDescending { it.matchScore }.map { candidate ->
                        RouteConnectionDto.RouteGroupMember(
                            memberId = candidate.member.id,
                            nickname = resolveDisplayNickname(candidate.member),
                            matchScore = candidate.matchScore,
                            totalDistance = candidate.totalDistance,
                        )
                    },
                )
            }

        val graphNodes = buildList {
            add(
                RouteConnectionDto.SocialGraphNode(
                    memberId = member.id,
                    nickname = resolveDisplayNickname(member),
                    me = true,
                )
            )
            recommendations.forEach { recommendation ->
                add(
                    RouteConnectionDto.SocialGraphNode(
                        memberId = recommendation.memberId,
                        nickname = recommendation.nickname,
                        me = false,
                    )
                )
            }
        }

        val graphEdges = recommendations.map { recommendation ->
            RouteConnectionDto.SocialGraphEdge(
                fromMemberId = member.id,
                toMemberId = recommendation.memberId,
                score = recommendation.matchScore,
                label = "${recommendation.sourceDistance}/${recommendation.destinationDistance}",
            )
        }

        return RouteConnectionDto.Response(
            generatedAt = ZonedDateTime.now(ZoneId.of(DEFAULT_TIMEZONE)).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
            matchingPolicy = RouteConnectionDto.MatchingPolicy(
                sourceMaxDistance = ROUTE_CONNECTION_SOURCE_MAX_DISTANCE,
                destinationMaxDistance = ROUTE_CONNECTION_DESTINATION_MAX_DISTANCE,
                totalMaxDistance = ROUTE_CONNECTION_TOTAL_MAX_DISTANCE,
            ),
            anchorRoute = RouteConnectionDto.AnchorRoute(
                sourceStationId = anchorRoute.sourceStationId,
                sourceStationName = anchorRoute.sourceStationName,
                destinationStationId = anchorRoute.destinationStationId,
                destinationStationName = anchorRoute.destinationStationName,
            ),
            recommendations = recommendations,
            groups = groups,
            graph = RouteConnectionDto.SocialGraph(
                nodes = graphNodes,
                edges = graphEdges,
            ),
        )
    }

    override fun getTodayCommuteCoach(targetArrivalAt: String?, timezone: String?): CommuteCoachDto.Response {
        val zoneId = parseTimezoneOrDefault(timezone)
        val now = ZonedDateTime.now(zoneId)
        val normalizedTargetTime = parseTargetArrivalTimeOrDefault(targetArrivalAt)
        val targetArrivalDateTime = resolveTargetArrivalDateTime(now, normalizedTargetTime)

        val recommendations = getFavoriteRouteRecommendations(limit = 3).routes
        if (recommendations.isEmpty()) {
            return CommuteCoachDto.Response(
                generatedAt = now.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                targetArrivalAt = normalizedTargetTime.format(commuteTimeFormatter),
                safeDepartureAt = null,
                departureInMinutes = null,
                riskLevel = CommuteCoachDto.RiskLevel.HIGH,
                riskReasons = listOf("출근 코치 계산에 필요한 즐겨찾기 경로가 없습니다."),
                walkingMeta = null,
                primaryRoute = null,
                alternativeRoutes = emptyList(),
                guidanceMessage = "즐겨찾는 역을 2개 이상 등록하면 출근 코치를 제공할 수 있어요.",
            )
        }

        val primaryRoute = recommendations.first()
        val alternativeRoutes = recommendations.drop(1).take(2)
        val walkingMeta = resolveCommuteWalkingMeta(member = getCurrentMember(), route = primaryRoute)
        val requiredMinutes = calculateRequiredMinutes(
            summary = primaryRoute.summary,
            walkingMinutes = walkingMeta.totalWalkingMinutes,
        )
        val safeDepartureDateTime = targetArrivalDateTime.minusMinutes(requiredMinutes.toLong())
        val departureInMinutes = Duration.between(now, safeDepartureDateTime).toMinutes().toInt()
        val minutesUntilTarget = Duration.between(now, targetArrivalDateTime).toMinutes().toInt()
        val riskLevel = calculateRiskLevel(minutesUntilTarget, primaryRoute.summary.estimatedMinutes, requiredMinutes)
        val riskReasons = buildRiskReasons(
            route = primaryRoute,
            targetArrivalAt = targetArrivalDateTime,
            minutesUntilTarget = minutesUntilTarget,
            requiredMinutes = requiredMinutes,
            walkingMeta = walkingMeta,
        )

        return CommuteCoachDto.Response(
            generatedAt = now.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
            targetArrivalAt = normalizedTargetTime.format(commuteTimeFormatter),
            safeDepartureAt = safeDepartureDateTime.format(commuteTimeFormatter),
            departureInMinutes = departureInMinutes,
            riskLevel = riskLevel,
            riskReasons = riskReasons,
            walkingMeta = walkingMeta,
            primaryRoute = primaryRoute,
            alternativeRoutes = alternativeRoutes,
            guidanceMessage = resolveGuidanceMessage(riskLevel),
        )
    }

    @Transactional
    override fun createFavoriteRoute(command: CreateFavoriteRouteCommand): FavoriteRouteDto.Route {
        if (command.sourceStationId == command.destinationStationId) {
            throw BusinessException(ResponseCode.INVALID_FAVORITE_ROUTE_REQUEST)
        }

        val member = getCurrentMember()
        val favoriteStations = memberStationReader.getByMember(member)
        val favoriteStationIds = favoriteStations.map { it.station.id }.toSet()

        if (command.sourceStationId !in favoriteStationIds || command.destinationStationId !in favoriteStationIds) {
            throw BusinessException(ResponseCode.NOT_EXIST_FAVORITE_ROUTE_STATION)
        }

        if (memberStationRouteReader.countByMember(member) >= MAX_FAVORITE_ROUTE_COUNT) {
            throw BusinessException(ResponseCode.EXCEED_MAXIMUM_FAVORITE_ROUTE_COUNT)
        }

        val existsForward = memberStationRouteReader.existsByMemberAndPair(
            member = member,
            sourceStationId = command.sourceStationId,
            destinationStationId = command.destinationStationId,
        )
        val existsReverse = memberStationRouteReader.existsByMemberAndPair(
            member = member,
            sourceStationId = command.destinationStationId,
            destinationStationId = command.sourceStationId,
        )
        if (existsForward || existsReverse) {
            throw BusinessException(ResponseCode.DUPLICATE_FAVORITE_ROUTE)
        }

        val sourceStation = stationReader.getById(command.sourceStationId)
        val destinationStation = stationReader.getById(command.destinationStationId)
        val saved = memberStationRouteWriter.save(
            MemberStationRouteEntity(
                member = member,
                sourceStation = sourceStation,
                destinationStation = destinationStation,
                title = command.title,
            )
        )

        return buildRoute(
            sourceStationId = saved.sourceStation.id,
            sourceStationName = saved.sourceStation.name,
            destinationStationId = saved.destinationStation.id,
            destinationStationName = saved.destinationStation.name,
            favoriteStationIds = favoriteStationIds,
            routeType = FavoriteRouteDto.RouteType.CUSTOM,
            routeId = saved.id,
            title = saved.title,
        )
    }

    @Transactional
    override fun deleteFavoriteRoute(routeId: Long): FavoriteRouteDto.DeleteResponse {
        val member = getCurrentMember()
        val target = memberStationRouteReader.getById(routeId)
        if (target.member.id != member.id) {
            throw BusinessException(ResponseCode.INVALID_AUTH)
        }
        memberStationRouteWriter.delete(target)
        return FavoriteRouteDto.DeleteResponse(routeId = routeId)
    }

    override fun getArticleHistories(limit: Int): GetArticleHistoryDto.Response {
        val memberId = RequestUtils.getAttribute(RequestUtils.Attribute.MEMBER_ID)!!.toLong()
        val normalizedLimit = limit.coerceIn(1, 100)

        val likedArticles = articleLikeReader.findAllByMemberId(memberId)
            .mapNotNull { like -> buildArticleHistoryOrNull(like.articleType, like.articleId, like.createdAt) }
            .sortedByDescending { it.reactedAt }
            .take(normalizedLimit)

        val bookmarkedArticles = articleBookmarkReader.findAllByMemberId(memberId)
            .mapNotNull { bookmark ->
                buildArticleHistoryOrNull(bookmark.articleType, bookmark.articleId, bookmark.createdAt)
            }
            .sortedByDescending { it.reactedAt }
            .take(normalizedLimit)

        return GetArticleHistoryDto.Response(
            likedArticles = likedArticles,
            bookmarkedArticles = bookmarkedArticles
        )
    }

    override fun searchMembers(command: SearchMemberCommand): SearchMemberDto.Response {
        val members = memberReader.searchMembers(command).map {
            SearchMemberDto.SearchMemberResponse(
                id = it.id,
                nickname = it.nickname
            )
        }

        return SearchMemberDto.Response.of(members)
    }

    @Transactional
    override fun updateFcmToken(fcmToken: String) {
        val member = memberReader.getMember(RequestUtils.getAttribute(RequestUtils.Attribute.MEMBER_ID)!!.toLong())
        val tokenEntity = member.fcmToken
        if (tokenEntity == null) {
            member.fcmToken = fcmTokenWriter.save(FcmTokenEntity(member = member, token = fcmToken))
        } else {
            tokenEntity.token = fcmToken
        }
    }

    private fun getCurrentMember(): MemberEntity {
        return memberReader.getMember(RequestUtils.getAttribute(RequestUtils.Attribute.MEMBER_ID)!!.toLong())
    }

    private data class AnchorRoute(
        val sourceStationId: Long,
        val sourceStationName: String,
        val destinationStationId: Long,
        val destinationStationName: String,
    )

    private fun resolveAnchorRoute(member: MemberEntity): AnchorRoute? {
        val customRoute = memberStationRouteReader.findAllByMember(member).firstOrNull()
        if (customRoute != null) {
            return AnchorRoute(
                sourceStationId = customRoute.sourceStation.id,
                sourceStationName = customRoute.sourceStation.name,
                destinationStationId = customRoute.destinationStation.id,
                destinationStationName = customRoute.destinationStation.name,
            )
        }

        val favorites = memberStationReader.getByMember(member)
        if (favorites.size < 2) {
            return null
        }

        val source = favorites.first().station
        val destination = favorites.drop(1).first().station
        return AnchorRoute(
            sourceStationId = source.id,
            sourceStationName = source.name,
            destinationStationId = destination.id,
            destinationStationName = destination.name,
        )
    }

    private fun resolveBestRouteConnectionCandidate(
        anchorRoute: AnchorRoute,
        routes: List<MemberStationRouteEntity>,
    ): RouteConnectionCandidate? {
        return routes.mapNotNull { route ->
            val sourceDistance = calculateStationDistance(anchorRoute.sourceStationId, route.sourceStation.id)
            val destinationDistance =
                calculateStationDistance(anchorRoute.destinationStationId, route.destinationStation.id)

            if (sourceDistance == null || destinationDistance == null) {
                return@mapNotNull null
            }

            val totalDistance = sourceDistance + destinationDistance
            if (sourceDistance > ROUTE_CONNECTION_SOURCE_MAX_DISTANCE ||
                destinationDistance > ROUTE_CONNECTION_DESTINATION_MAX_DISTANCE ||
                totalDistance > ROUTE_CONNECTION_TOTAL_MAX_DISTANCE
            ) {
                return@mapNotNull null
            }

            RouteConnectionCandidate(
                member = route.member,
                route = route,
                sourceDistance = sourceDistance,
                destinationDistance = destinationDistance,
                totalDistance = totalDistance,
                matchScore = calculateRouteConnectionScore(sourceDistance, destinationDistance, totalDistance),
            )
        }.maxByOrNull { candidate -> candidate.matchScore }
    }

    private fun calculateStationDistance(sourceStationId: Long, destinationStationId: Long): Int? {
        return runCatching {
            findShortestPath(sourceStationId, destinationStationId).metric.stops
        }.getOrNull()
    }

    private fun calculateRouteConnectionScore(
        sourceDistance: Int,
        destinationDistance: Int,
        totalDistance: Int,
    ): Int {
        val score = 100 - sourceDistance * 25 - destinationDistance * 25 - totalDistance * 10
        return score.coerceIn(1, 100)
    }

    private fun buildRouteConnectionReason(candidate: RouteConnectionCandidate): String {
        val sourceReason = if (candidate.sourceDistance == 0) {
            "출발역 동일"
        } else {
            "출발역 ${candidate.sourceDistance}정거장 차이"
        }
        val destinationReason = if (candidate.destinationDistance == 0) {
            "도착역 동일"
        } else {
            "도착역 ${candidate.destinationDistance}정거장 차이"
        }
        return "$sourceReason · $destinationReason"
    }

    private fun resolveDisplayNickname(member: MemberEntity): String {
        val normalized = member.nickname?.trim().orEmpty()
        if (normalized.isNotEmpty()) {
            return normalized
        }
        return "아하철러-${member.id}"
    }

    private fun buildRoute(
        sourceStationId: Long,
        sourceStationName: String,
        destinationStationId: Long,
        destinationStationName: String,
        favoriteStationIds: Set<Long>,
        routeType: FavoriteRouteDto.RouteType,
        routeId: Long?,
        title: String?,
    ): FavoriteRouteDto.Route {
        val path = findShortestPath(sourceStationId, destinationStationId)
        val nodes = path.nodes.mapIndexed { index, stationId ->
            FavoriteRouteDto.Node(
                stationId = stationId,
                stationName = path.stationNamesById[stationId]
                    ?: if (stationId == sourceStationId) sourceStationName else destinationStationName,
                order = index,
                favorite = stationId in favoriteStationIds,
            )
        }
        val edges = path.edges.map {
            FavoriteRouteDto.Edge(
                fromStationId = it.fromStationId,
                toStationId = it.toStationId,
                subwayLineId = it.subwayLineId,
                subwayLineName = it.subwayLineName,
            )
        }

        return FavoriteRouteDto.Route(
            routeId = routeId,
            routeType = routeType,
            title = title,
            sourceStationId = sourceStationId,
            sourceStationName = sourceStationName,
            destinationStationId = destinationStationId,
            destinationStationName = destinationStationName,
            nodes = nodes,
            edges = edges,
            summary = FavoriteRouteDto.Summary(
                totalStops = path.metric.stops,
                transferCount = path.metric.transfers,
                estimatedMinutes = estimateRouteMinutes(path.metric.stops, path.metric.transfers),
            ),
        )
    }

    private fun estimateRouteMinutes(totalStops: Int, transferCount: Int): Int {
        return totalStops * 2 + transferCount * 4
    }

    private fun parseTimezoneOrDefault(timezone: String?): ZoneId {
        val normalized = timezone?.trim().takeUnless { it.isNullOrEmpty() } ?: DEFAULT_TIMEZONE
        return runCatching { ZoneId.of(normalized) }
            .getOrDefault(ZoneId.of(DEFAULT_TIMEZONE))
    }

    private fun parseTargetArrivalTimeOrDefault(targetArrivalAt: String?): LocalTime {
        val normalized = targetArrivalAt?.trim().takeUnless { it.isNullOrEmpty() } ?: return DEFAULT_COMMUTE_ARRIVAL_TIME
        return runCatching { LocalTime.parse(normalized, commuteTimeFormatter) }
            .getOrDefault(DEFAULT_COMMUTE_ARRIVAL_TIME)
    }

    private fun resolveTargetArrivalDateTime(now: ZonedDateTime, targetArrivalTime: LocalTime): ZonedDateTime {
        val candidate = now.withHour(targetArrivalTime.hour)
            .withMinute(targetArrivalTime.minute)
            .withSecond(0)
            .withNano(0)

        return if (candidate.isBefore(now)) {
            candidate.plusDays(1)
        } else {
            candidate
        }
    }

    private fun calculateRequiredMinutes(
        summary: FavoriteRouteDto.Summary,
        walkingMinutes: Int,
    ): Int {
        val estimatedMinutes = summary.estimatedMinutes
        val transferBuffer = 4 + summary.transferCount * 3
        return estimatedMinutes + transferBuffer + walkingMinutes
    }

    private fun calculateRiskLevel(
        minutesUntilTarget: Int,
        estimatedMinutes: Int,
        requiredMinutes: Int,
    ): CommuteCoachDto.RiskLevel {
        return when {
            minutesUntilTarget >= requiredMinutes -> CommuteCoachDto.RiskLevel.LOW
            minutesUntilTarget >= estimatedMinutes -> CommuteCoachDto.RiskLevel.MEDIUM
            else -> CommuteCoachDto.RiskLevel.HIGH
        }
    }

    private fun buildRiskReasons(
        route: FavoriteRouteDto.Route,
        targetArrivalAt: ZonedDateTime,
        minutesUntilTarget: Int,
        requiredMinutes: Int,
        walkingMeta: CommuteCoachDto.WalkingMeta,
    ): List<String> {
        val reasons = mutableListOf<String>()

        reasons.add(
            "도보 시간(출발 ${walkingMeta.source.walkingMinutes}분 + 도착 ${walkingMeta.destination.walkingMinutes}분)을 반영했습니다."
        )

        if (route.summary.transferCount > 0) {
            reasons.add("환승 ${route.summary.transferCount}회 경로라 이동 변동 가능성이 있습니다.")
        }

        val missingMinutes = requiredMinutes - minutesUntilTarget
        if (missingMinutes > 0) {
            reasons.add(
                "목표 도착 시각(${targetArrivalAt.format(commuteTimeFormatter)}) 대비 여유 시간이 ${missingMinutes}분 부족합니다."
            )
        }

        if (route.summary.totalStops >= 12) {
            reasons.add("이동 정거장이 많아 지연 발생 시 영향이 커질 수 있습니다.")
        }

        if (reasons.isEmpty()) {
            reasons.add("현재 조건에서 도착 여유 시간이 충분합니다.")
        }

        return reasons
    }

    private fun resolveCommuteWalkingMeta(
        member: MemberEntity,
        route: FavoriteRouteDto.Route,
    ): CommuteCoachDto.WalkingMeta {
        val memberStations = memberStationReader.getByMember(member)
            .associateBy { it.station.id }

        val sourceLeg = resolveCommuteWalkingLeg(
            stationId = route.sourceStationId,
            stationName = route.sourceStationName,
            memberStation = memberStations[route.sourceStationId],
        )
        val destinationLeg = resolveCommuteWalkingLeg(
            stationId = route.destinationStationId,
            stationName = route.destinationStationName,
            memberStation = memberStations[route.destinationStationId],
        )

        return CommuteCoachDto.WalkingMeta(
            totalWalkingMinutes = sourceLeg.walkingMinutes + destinationLeg.walkingMinutes,
            source = sourceLeg.toDto(),
            destination = destinationLeg.toDto(),
        )
    }

    private fun resolveCommuteWalkingLeg(
        stationId: Long,
        stationName: String,
        memberStation: MemberStationEntity?,
    ): CommuteWalkingLegDecision {
        val walkingMinutes = memberStation?.walkingMinutes?.coerceIn(0, 180)
        if (walkingMinutes != null) {
            return CommuteWalkingLegDecision(
                stationId = stationId,
                stationName = stationName,
                walkingMinutes = walkingMinutes,
                source = CommuteCoachDto.WalkingMinutesSource.USER_PROFILE,
                updatedAt = memberStation.walkingUpdatedAt?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            )
        }

        return CommuteWalkingLegDecision(
            stationId = stationId,
            stationName = stationName,
            walkingMinutes = DEFAULT_COMMUTE_WALKING_MINUTES,
            source = CommuteCoachDto.WalkingMinutesSource.DEFAULT,
            updatedAt = null,
        )
    }

    private fun CommuteWalkingLegDecision.toDto(): CommuteCoachDto.WalkingLeg {
        return CommuteCoachDto.WalkingLeg(
            stationId = stationId,
            stationName = stationName,
            walkingMinutes = walkingMinutes,
            walkingMinutesSource = source,
            walkingMinutesUpdatedAt = updatedAt,
        )
    }

    private fun resolveGuidanceMessage(riskLevel: CommuteCoachDto.RiskLevel): String {
        return when (riskLevel) {
            CommuteCoachDto.RiskLevel.LOW -> "권장 출발 시각에 맞춰 이동하면 안정적으로 도착할 가능성이 높아요."
            CommuteCoachDto.RiskLevel.MEDIUM -> "여유 시간이 크지 않아 환승 구간 지연 여부를 함께 확인해 주세요."
            CommuteCoachDto.RiskLevel.HIGH -> "지금 바로 이동하거나 대체 경로를 우선 확인해 주세요."
        }
    }

    private fun findShortestPath(sourceStationId: Long, destinationStationId: Long): PathResult {
        if (sourceStationId == destinationStationId) {
            return PathResult(
                nodes = listOf(sourceStationId),
                edges = emptyList(),
                metric = RouteMetric(stops = 0, transfers = 0),
                stationNamesById = mapOf(sourceStationId to stationReader.getById(sourceStationId).name),
            )
        }

        val allLineStations = subwayLineStationReader.findAllOrderedForGraph()
        if (allLineStations.isEmpty()) {
            throw BusinessException(ResponseCode.ROUTE_NOT_FOUND)
        }

        val stationNamesById = allLineStations
            .associate { it.station.id to it.station.name }

        if (!stationNamesById.containsKey(sourceStationId) || !stationNamesById.containsKey(destinationStationId)) {
            throw BusinessException(ResponseCode.ROUTE_NOT_FOUND)
        }

        val adjacency = mutableMapOf<Long, MutableList<GraphEdge>>()
        allLineStations
            .groupBy { it.subwayLine.id }
            .forEach { (_, lineStations) ->
                lineStations
                    .windowed(size = 2, step = 1, partialWindows = false)
                    .forEach { pair ->
                        val left = pair[0]
                        val right = pair[1]
                        val forward = GraphEdge(
                            fromStationId = left.station.id,
                            toStationId = right.station.id,
                            subwayLineId = left.subwayLine.id,
                            subwayLineName = left.subwayLine.name,
                        )
                        val backward = GraphEdge(
                            fromStationId = right.station.id,
                            toStationId = left.station.id,
                            subwayLineId = left.subwayLine.id,
                            subwayLineName = left.subwayLine.name,
                        )
                        adjacency.getOrPut(forward.fromStationId) { mutableListOf() }.add(forward)
                        adjacency.getOrPut(backward.fromStationId) { mutableListOf() }.add(backward)
                    }
            }

        val start = RouteState(sourceStationId, null)
        val queue = PriorityQueue<StateWithMetric>()
        val distance = mutableMapOf(start to RouteMetric(stops = 0, transfers = 0))
        val previous = mutableMapOf<RouteState, Prev>()
        queue.add(StateWithMetric(start, RouteMetric(stops = 0, transfers = 0)))

        var endState: RouteState? = null
        var endMetric: RouteMetric? = null

        while (queue.isNotEmpty()) {
            val current = queue.poll()
            val currentBest = distance[current.state] ?: continue
            if (current.metric != currentBest) {
                continue
            }

            if (current.state.stationId == destinationStationId) {
                endState = current.state
                endMetric = current.metric
                break
            }

            adjacency[current.state.stationId].orEmpty().forEach { edge ->
                val nextState = RouteState(stationId = edge.toStationId, lineId = edge.subwayLineId)
                val isTransfer = current.state.lineId != null && current.state.lineId != edge.subwayLineId
                val nextMetric = RouteMetric(
                    stops = current.metric.stops + 1,
                    transfers = current.metric.transfers + if (isTransfer) 1 else 0,
                )
                val known = distance[nextState]
                if (known == null || nextMetric < known) {
                    distance[nextState] = nextMetric
                    previous[nextState] = Prev(previousState = current.state, edge = edge)
                    queue.add(StateWithMetric(nextState, nextMetric))
                }
            }
        }

        if (endState == null || endMetric == null) {
            throw BusinessException(ResponseCode.ROUTE_NOT_FOUND)
        }

        val edgesReversed = mutableListOf<GraphEdge>()
        var cursor = endState
        while (cursor != start) {
            val prev = previous[cursor] ?: break
            edgesReversed.add(prev.edge)
            cursor = prev.previousState
        }

        val edges = edgesReversed.reversed()
        if (edges.isEmpty()) {
            throw BusinessException(ResponseCode.ROUTE_NOT_FOUND)
        }

        val nodes = buildList {
            add(sourceStationId)
            edges.forEach { add(it.toStationId) }
        }

        return PathResult(
            nodes = nodes,
            edges = edges,
            metric = endMetric,
            stationNamesById = stationNamesById,
        )
    }

    private fun isEqualsAlreadyRegisteredStation(
        originMemberStations: List<MemberStationEntity>,
        newBookmarkStationCommands: List<BookmarkStationCommand>
    ): Boolean {
        if (originMemberStations.size != newBookmarkStationCommands.size) {
            return false
        }

        return originMemberStations.indices.all {
            val current = originMemberStations[it]
            val next = newBookmarkStationCommands[it]
            val nextStationId = next.stationId ?: stationReader.getByName(normalizeInput(next.stationName)).id
            current.station.id == nextStationId &&
                current.label == normalizeOptionalInput(next.label ?: "") &&
                isEqualsLocationMeta(current, next.locationMeta)
        }
    }

    private fun saveNewStations(member: MemberEntity, bookmarkStations: List<BookmarkStationCommand>): List<MemberStationEntity> {
        return bookmarkStations
            .map {
                val station = it.stationId?.let { stationId -> stationReader.getById(stationId) }
                    ?: stationReader.getByName(normalizeInput(it.stationName))
                val normalizedLabel = normalizeOptionalInput(it.label ?: "")
                val normalizedLocationMeta = normalizeLocationMeta(it.locationMeta)
                val memberStation = MemberStationEntity(
                    member = member,
                    station = station,
                    label = normalizedLabel,
                    locationName = normalizedLocationMeta?.locationName,
                    roadAddress = normalizedLocationMeta?.roadAddress,
                    jibunAddress = normalizedLocationMeta?.jibunAddress,
                    latitude = normalizedLocationMeta?.latitude,
                    longitude = normalizedLocationMeta?.longitude,
                    walkingMinutes = normalizedLocationMeta?.walkingMinutes,
                    walkingSource = normalizedLocationMeta?.walkingSource,
                    walkingUpdatedAt = if (normalizedLocationMeta?.walkingMinutes != null) LocalDateTime.now() else null,
                )
                memberStationWriter.save(memberStation)
            }
    }

    private fun createBookmarkStationResponse(memberStations: List<MemberStationEntity>): GetBookmarkStationDto.Response {
        val stationInfos = memberStations
            .map {
                val station = it.station
                GetBookmarkStationDto.StationInfo(
                    stationId = station.id,
                    stationName = station.name,
                    label = it.label,
                    locationMeta = toLocationMeta(it),
                    subwayLineInfoList = getSubwayLineInfos(station)
                )
            }

        return GetBookmarkStationDto.Response(stationInfos)
    }

    private fun getSubwayLineInfos(station: StationEntity): List<GetBookmarkStationDto.SubwayLineInfo> {
        val subwayLineStations = subwayLineStationReader.findByStation(station)
        return subwayLineStations.map {
            GetBookmarkStationDto.SubwayLineInfo(
                subwayLineId = it.subwayLine.id,
                subwayLineName = it.subwayLine.name
            )
        }
    }

    private fun toLocationMeta(memberStation: MemberStationEntity): GetBookmarkStationDto.LocationMeta? {
        val hasValue = memberStation.locationName != null ||
            memberStation.roadAddress != null ||
            memberStation.jibunAddress != null ||
            memberStation.latitude != null ||
            memberStation.longitude != null ||
            memberStation.walkingMinutes != null ||
            memberStation.walkingSource != null ||
            memberStation.walkingUpdatedAt != null
        if (!hasValue) {
            return null
        }

        return GetBookmarkStationDto.LocationMeta(
            locationName = memberStation.locationName,
            roadAddress = memberStation.roadAddress,
            jibunAddress = memberStation.jibunAddress,
            latitude = memberStation.latitude,
            longitude = memberStation.longitude,
            walkingMinutes = memberStation.walkingMinutes,
            walkingSource = memberStation.walkingSource,
            walkingUpdatedAt = memberStation.walkingUpdatedAt?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
        )
    }

    private fun validateNickname(nickname: String) {
        if (nickname.length !in NICKNAME_MIN_LENGTH..NICKNAME_MAX_LENGTH) {
            throw BusinessException(ResponseCode.INVALID_NICKNAME_FORMAT)
        }

        if (!NICKNAME_REGEX.matches(nickname)) {
            throw BusinessException(ResponseCode.INVALID_NICKNAME_FORMAT)
        }
    }

    private fun validateDuplicateBookmarkStations(stations: List<BookmarkStationCommand>) {
        val normalizedStationKeys = stations.map { station ->
            station.stationId?.let { "id:$it" } ?: "name:${normalizeInput(station.stationName)}"
        }
        if (normalizedStationKeys.distinct().size != normalizedStationKeys.size) {
            throw BusinessException(ResponseCode.DUPLICATE_BOOKMARK_STATION)
        }
    }

    private fun normalizeLocationMeta(
        locationMeta: BookmarkStationLocationMetaCommand?,
    ): BookmarkStationLocationMetaCommand? {
        if (locationMeta == null) {
            return null
        }
        val locationName = locationMeta.locationName?.let(::normalizeOptionalInput)
        val roadAddress = locationMeta.roadAddress?.let(::normalizeOptionalInput)
        val jibunAddress = locationMeta.jibunAddress?.let(::normalizeOptionalInput)
        val walkingMinutes = locationMeta.walkingMinutes?.coerceIn(0, 180)
        return BookmarkStationLocationMetaCommand(
            locationName = locationName,
            roadAddress = roadAddress,
            jibunAddress = jibunAddress,
            latitude = locationMeta.latitude,
            longitude = locationMeta.longitude,
            walkingMinutes = walkingMinutes,
            walkingSource = locationMeta.walkingSource,
        )
    }

    private fun isEqualsLocationMeta(
        memberStation: MemberStationEntity,
        nextMeta: BookmarkStationLocationMetaCommand?,
    ): Boolean {
        val normalized = normalizeLocationMeta(nextMeta)
        return memberStation.locationName == normalized?.locationName &&
            memberStation.roadAddress == normalized?.roadAddress &&
            memberStation.jibunAddress == normalized?.jibunAddress &&
            memberStation.latitude == normalized?.latitude &&
            memberStation.longitude == normalized?.longitude &&
            memberStation.walkingMinutes == normalized?.walkingMinutes &&
            memberStation.walkingSource == normalized?.walkingSource
    }

    private fun normalizeInput(value: String): String {
        return Normalizer.normalize(value, Normalizer.Form.NFC).trim()
    }

    private fun normalizeOptionalInput(value: String): String? {
        return normalizeInput(value).ifBlank { null }
    }

    private fun buildArticleHistoryOrNull(
        articleType: ArticleType,
        articleId: Long,
        reactedAt: LocalDateTime
    ): GetArticleHistoryDto.ArticleHistory? {
        return when (articleType) {
            ArticleType.COMMUNITY -> {
                val post = communityPostReader.getCommunityPost(articleId)
                if (post.status == CommunityPostType.DELETED) {
                    null
                } else {
                    GetArticleHistoryDto.ArticleHistory(
                        articleType = ArticleType.COMMUNITY,
                        articleId = post.id,
                        title = post.title,
                        contentPreview = post.content.take(120),
                        writer = post.member?.nickname,
                        subwayLineId = post.subwayLineEntity.id,
                        stationId = post.station?.id,
                        articleCreatedAt = formatDateTime(post.createdAt),
                        reactedAt = formatDateTime(reactedAt)
                    )
                }
            }

            ArticleType.COMPLAINT -> {
                val post = complaintPostReader.getComplaintPost(articleId)
                if (post.status == ComplaintPostType.DELETED) {
                    null
                } else {
                    GetArticleHistoryDto.ArticleHistory(
                        articleType = ArticleType.COMPLAINT,
                        articleId = post.id,
                        title = post.content.take(24),
                        contentPreview = post.content.take(120),
                        writer = post.member?.nickname,
                        subwayLineId = post.subwayLine.id,
                        stationId = post.station?.id,
                        articleCreatedAt = formatDateTime(post.createdAt),
                        reactedAt = formatDateTime(reactedAt)
                    )
                }
            }

            ArticleType.LOST -> {
                val post = lostPostReader.getLostPost(articleId)
                if (post.type == LostPostType.DELETED) {
                    null
                } else {
                    GetArticleHistoryDto.ArticleHistory(
                        articleType = ArticleType.LOST,
                        articleId = post.id,
                        title = post.title,
                        contentPreview = post.content.take(120),
                        writer = post.member?.nickname ?: post.createdBy,
                        subwayLineId = post.subwayLine?.id,
                        stationId = post.station?.id,
                        articleCreatedAt = formatDateTime(post.date),
                        reactedAt = formatDateTime(reactedAt)
                    )
                }
            }
        }
    }

    private fun formatDateTime(value: LocalDateTime): String {
        return value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"))
    }

    private fun buildRecentPostActivities(memberId: Long, limit: Int): List<GetMemberProfileDto.PostActivity> {
        val communityPosts = communityPostReader.getRecentCommunityPostsByMemberId(memberId, limit)
            .filter { it.status == CommunityPostType.CREATED }
            .map {
                GetMemberProfileDto.PostActivity(
                    articleType = ArticleType.COMMUNITY,
                    articleId = it.id,
                    title = it.title,
                    contentPreview = it.content.take(120),
                    writer = it.member?.nickname,
                    subwayLineId = it.subwayLineEntity.id,
                    stationId = it.station?.id,
                    createdAt = formatDateTime(it.createdAt),
                )
            }

        val complaintPosts = complaintPostReader.getRecentComplaintPostsByMemberId(memberId, limit)
            .filter { it.status != ComplaintPostType.DELETED }
            .map {
                GetMemberProfileDto.PostActivity(
                    articleType = ArticleType.COMPLAINT,
                    articleId = it.id,
                    title = it.content.take(24),
                    contentPreview = it.content.take(120),
                    writer = it.member?.nickname,
                    subwayLineId = it.subwayLine.id,
                    stationId = it.station?.id,
                    createdAt = formatDateTime(it.createdAt),
                )
            }

        val lostPosts = lostPostReader.getRecentLostPostsByMemberId(memberId, limit)
            .filter { it.type == LostPostType.CREATED }
            .map {
                GetMemberProfileDto.PostActivity(
                    articleType = ArticleType.LOST,
                    articleId = it.id,
                    title = it.title,
                    contentPreview = it.content.take(120),
                    writer = it.member?.nickname ?: it.createdBy,
                    subwayLineId = it.subwayLine?.id,
                    stationId = it.station?.id,
                    createdAt = formatDateTime(it.createdAt),
                )
            }

        return (communityPosts + complaintPosts + lostPosts)
            .sortedByDescending { it.createdAt }
            .take(limit)
    }

    private fun buildRecentCommentActivities(
        memberId: Long,
        limit: Int,
        enforcePublicPolicy: Boolean,
    ): List<GetMemberProfileDto.CommentActivity> {
        return commentReader.getRecentCommentsByMemberId(memberId, limit)
            .filter { it.status == CommentType.CREATED }
            .filterNot { enforcePublicPolicy && it.visibility.isPrivate }
            .mapNotNull { comment ->
                val communityPost = comment.communityPost
                if (communityPost != null && communityPost.status == CommunityPostType.CREATED) {
                    return@mapNotNull GetMemberProfileDto.CommentActivity(
                        commentId = comment.id,
                        articleType = ArticleType.COMMUNITY,
                        articleId = communityPost.id,
                        contentPreview = comment.content.take(120),
                        writer = comment.member.nickname,
                        createdAt = formatDateTime(comment.createdAt),
                    )
                }

                val complaintPost = comment.complaintPost
                if (complaintPost != null && complaintPost.status != ComplaintPostType.DELETED) {
                    return@mapNotNull GetMemberProfileDto.CommentActivity(
                        commentId = comment.id,
                        articleType = ArticleType.COMPLAINT,
                        articleId = complaintPost.id,
                        contentPreview = comment.content.take(120),
                        writer = comment.member.nickname,
                        createdAt = formatDateTime(comment.createdAt),
                    )
                }

                val lostPost = comment.lostPost
                if (lostPost != null && lostPost.type == LostPostType.CREATED) {
                    return@mapNotNull GetMemberProfileDto.CommentActivity(
                        commentId = comment.id,
                        articleType = ArticleType.LOST,
                        articleId = lostPost.id,
                        contentPreview = comment.content.take(120),
                        writer = comment.member.nickname,
                        createdAt = formatDateTime(comment.createdAt),
                    )
                }

                null
            }
            .sortedByDescending { it.createdAt }
            .take(limit)
    }

    private fun maskEmail(email: String?): String? {
        val value = email?.trim()?.takeIf { it.isNotEmpty() } ?: return null
        val parts = value.split("@")
        if (parts.size != 2) {
            return value
        }

        val local = parts[0]
        val domain = parts[1]
        if (local.isEmpty() || domain.isEmpty()) {
            return value
        }

        val visibleCount = if (local.length <= 2) 1 else 2
        val maskedCount = maxOf(local.length - visibleCount, 1)
        val maskedLocal = local.take(visibleCount) + "*".repeat(maskedCount)
        return "$maskedLocal@$domain"
    }
}
