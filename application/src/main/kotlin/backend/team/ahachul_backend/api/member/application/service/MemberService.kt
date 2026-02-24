package backend.team.ahachul_backend.api.member.application.service

import backend.team.ahachul_backend.api.article.application.port.out.ArticleBookmarkReader
import backend.team.ahachul_backend.api.article.application.port.out.ArticleLikeReader
import backend.team.ahachul_backend.api.article.domain.model.ArticleType
import backend.team.ahachul_backend.api.common.application.port.out.StationReader
import backend.team.ahachul_backend.api.common.application.port.out.SubwayLineStationReader
import backend.team.ahachul_backend.api.common.domain.entity.StationEntity
import backend.team.ahachul_backend.api.comment.application.port.out.CommentReader
import backend.team.ahachul_backend.api.comment.domain.model.CommentType
import backend.team.ahachul_backend.api.community.application.port.out.CommunityPostReader
import backend.team.ahachul_backend.api.community.domain.model.CommunityPostType
import backend.team.ahachul_backend.api.complaint.application.port.out.ComplaintPostReader
import backend.team.ahachul_backend.api.complaint.domain.model.ComplaintPostType
import backend.team.ahachul_backend.api.lost.application.port.out.LostPostReader
import backend.team.ahachul_backend.api.lost.domain.model.LostPostType
import backend.team.ahachul_backend.api.member.adapter.web.`in`.dto.*
import backend.team.ahachul_backend.api.member.application.command.BookmarkStationCommand
import backend.team.ahachul_backend.api.member.application.command.SearchMemberCommand
import backend.team.ahachul_backend.api.member.application.port.`in`.MemberUseCase
import backend.team.ahachul_backend.api.member.application.command.BookmarkStationCommands
import backend.team.ahachul_backend.api.member.application.port.`in`.command.CheckNicknameCommand
import backend.team.ahachul_backend.api.member.application.port.`in`.command.UpdateMemberCommand
import backend.team.ahachul_backend.api.member.application.port.`in`.command.UpdateMemberVisibilityCommand
import backend.team.ahachul_backend.api.member.application.port.out.FcmTokenWriter
import backend.team.ahachul_backend.api.member.application.port.out.MemberReader
import backend.team.ahachul_backend.api.member.application.port.out.MemberStationReader
import backend.team.ahachul_backend.api.member.application.port.out.MemberStationWriter
import backend.team.ahachul_backend.api.member.domain.entity.FcmTokenEntity
import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity
import backend.team.ahachul_backend.api.member.domain.entity.MemberStationEntity
import backend.team.ahachul_backend.common.exception.BusinessException
import backend.team.ahachul_backend.common.response.ResponseCode
import backend.team.ahachul_backend.common.utils.RequestUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.text.Normalizer
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
@Transactional(readOnly = true)
class MemberService(
    private val memberReader: MemberReader,
    private val stationReader: StationReader,
    private val memberStationWriter: MemberStationWriter,
    private val memberStationReader: MemberStationReader,
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
        private val NICKNAME_REGEX = Regex("^[가-힣a-zA-Z0-9_]+$")
    }

    override fun getMember(): GetMemberDto.Response {
        val member = memberReader.getMember(RequestUtils.getAttribute(RequestUtils.Attribute.MEMBER_ID)!!.toLong())
        return GetMemberDto.Response.of(member)
    }

    override fun getMemberVisibility(): MemberVisibilityDto.Response {
        val member = memberReader.getMember(RequestUtils.getAttribute(RequestUtils.Attribute.MEMBER_ID)!!.toLong())
        return MemberVisibilityDto.Response.from(member)
    }

    @Transactional
    override fun updateMemberVisibility(command: UpdateMemberVisibilityCommand): MemberVisibilityDto.Response {
        val member = memberReader.getMember(RequestUtils.getAttribute(RequestUtils.Attribute.MEMBER_ID)!!.toLong())
        command.profilePublic?.let { member.changeProfilePublic(it) }
        command.emailPublic?.let { member.changeEmailPublic(it) }
        command.genderAgePublic?.let { member.changeGenderAgePublic(it) }
        command.postsPublic?.let { member.changeActivityPostsPublic(it) }
        command.commentsPublic?.let { member.changeActivityCommentsPublic(it) }
        return MemberVisibilityDto.Response.from(member)
    }

    override fun getMemberProfile(nickname: String, asPublic: Boolean, limit: Int): GetMemberProfileDto.Response {
        val targetMember = memberReader.getMemberByNickname(normalizeInput(nickname))
        val loginMemberId = RequestUtils.getAttribute(RequestUtils.Attribute.MEMBER_ID)?.toLongOrNull()
        val isMine = loginMemberId != null && loginMemberId == targetMember.id
        val enforcePublicPolicy = asPublic || !isMine
        val normalizedLimit = limit.coerceIn(1, 50)

        val profileVisible = !enforcePublicPolicy || targetMember.isProfilePublic()
        val postsVisible = !enforcePublicPolicy || targetMember.isActivityPostsPublic()
        val commentsVisible = !enforcePublicPolicy || targetMember.isActivityCommentsPublic()
        val emailVisible = profileVisible && (!enforcePublicPolicy || targetMember.isEmailPublic())
        val genderAgeVisible = profileVisible && (!enforcePublicPolicy || targetMember.isGenderAgePublic())

        val postActivities = if (postsVisible) {
            buildRecentPostActivities(targetMember.id, normalizedLimit)
        } else {
            emptyList()
        }

        val commentActivities = if (commentsVisible) {
            buildRecentCommentActivities(targetMember.id, normalizedLimit)
        } else {
            emptyList()
        }

        return GetMemberProfileDto.Response(
            memberId = targetMember.id,
            nickname = targetMember.nickname,
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
        return UpdateMemberDto.Response.of(
                nickname = member.nickname,
                gender = member.gender,
                ageRange = member.ageRange
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

    private fun isEqualsAlreadyRegisteredStation(
        originMemberStations: List<MemberStationEntity>,
        newBookmarkStationCommands: List<BookmarkStationCommand>
    ): Boolean {
        if (originMemberStations.size != newBookmarkStationCommands.size) {
            return false
        }

        return originMemberStations.indices.all {
            originMemberStations[it].isEquals(
                normalizeInput(newBookmarkStationCommands[it].stationName),
                newBookmarkStationCommands[it].label
            )
        }
    }

    private fun saveNewStations(member: MemberEntity, bookmarkStations: List<BookmarkStationCommand>): List<MemberStationEntity> {
        return bookmarkStations
            .map {
                val memberStation = MemberStationEntity(
                    member = member,
                    station = stationReader.getByName(normalizeInput(it.stationName)),
                    label = it.label
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

    private fun validateNickname(nickname: String) {
        if (nickname.length !in NICKNAME_MIN_LENGTH..NICKNAME_MAX_LENGTH) {
            throw BusinessException(ResponseCode.INVALID_NICKNAME_FORMAT)
        }

        if (!NICKNAME_REGEX.matches(nickname)) {
            throw BusinessException(ResponseCode.INVALID_NICKNAME_FORMAT)
        }
    }

    private fun validateDuplicateBookmarkStations(stations: List<BookmarkStationCommand>) {
        val normalizedStationNames = stations.map { normalizeInput(it.stationName) }
        if (normalizedStationNames.distinct().size != normalizedStationNames.size) {
            throw BusinessException(ResponseCode.DUPLICATE_BOOKMARK_STATION)
        }
    }

    private fun normalizeInput(value: String): String {
        return Normalizer.normalize(value, Normalizer.Form.NFC).trim()
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

    private fun buildRecentCommentActivities(memberId: Long, limit: Int): List<GetMemberProfileDto.CommentActivity> {
        return commentReader.getRecentCommentsByMemberId(memberId, limit)
            .filter { it.status == CommentType.CREATED }
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
