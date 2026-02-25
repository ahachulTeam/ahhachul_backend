package backend.team.ahachul_backend.bootstrap

import backend.team.ahachul_backend.api.comment.adapter.web.out.CommentRepository
import backend.team.ahachul_backend.api.comment.domain.entity.CommentEntity
import backend.team.ahachul_backend.api.comment.domain.model.CommentVisibility
import backend.team.ahachul_backend.api.common.adapter.web.out.StationRepository
import backend.team.ahachul_backend.api.common.domain.entity.StationEntity
import backend.team.ahachul_backend.api.community.adapter.web.out.CommunityPostRepository
import backend.team.ahachul_backend.api.community.domain.entity.CommunityPostEntity
import backend.team.ahachul_backend.api.community.domain.model.CommunityCategoryType
import backend.team.ahachul_backend.api.complaint.adapter.out.ComplaintPostRepository
import backend.team.ahachul_backend.api.complaint.domain.entity.ComplaintPostEntity
import backend.team.ahachul_backend.api.complaint.domain.model.ComplaintType
import backend.team.ahachul_backend.api.complaint.domain.model.ShortContentType
import backend.team.ahachul_backend.api.lost.adapter.web.out.LostPostRepository
import backend.team.ahachul_backend.api.lost.domain.entity.LostPostEntity
import backend.team.ahachul_backend.api.lost.domain.model.LostType
import backend.team.ahachul_backend.api.member.adapter.web.out.MemberRepository
import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity
import backend.team.ahachul_backend.api.member.domain.model.MemberStatusType
import backend.team.ahachul_backend.api.member.domain.model.ProviderType
import backend.team.ahachul_backend.common.domain.entity.SubwayLineEntity
import backend.team.ahachul_backend.common.persistence.SubwayLineRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Profile
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Profile("local")
@Component
class LocalMockContentSeeder(
    private val memberRepository: MemberRepository,
    private val subwayLineRepository: SubwayLineRepository,
    private val stationRepository: StationRepository,
    private val communityPostRepository: CommunityPostRepository,
    private val complaintPostRepository: ComplaintPostRepository,
    private val lostPostRepository: LostPostRepository,
    private val commentRepository: CommentRepository,
) {
    @EventListener(ApplicationReadyEvent::class)
    @Transactional
    fun seed() {
        val member = resolveSeedMember()
        val subwayLine = subwayLineRepository.findById(2L).orElseGet {
            subwayLineRepository.findAll().firstOrNull()
        }

        if (subwayLine == null) {
            logger.warn("[local-seed] subway line 데이터가 없어 목데이터 생성을 건너뜁니다.")
            return
        }

        val station = stationRepository.findById(65L).orElseGet {
            stationRepository.findAll().firstOrNull()
        }

        seedCommunityPosts(member, subwayLine, station)
        seedComplaintPosts(member, subwayLine, station)
        seedLostPosts(member, subwayLine, station)
    }

    private fun seedCommunityPosts(member: MemberEntity, subwayLine: SubwayLineEntity, station: StationEntity?) {
        if (communityPostRepository.count() > 0L) {
            return
        }

        val posts = communityPostRepository.saveAll(
            listOf(
                CommunityPostEntity(
                    title = "$SEED_PREFIX 출근시간 2호선 혼잡도 공유",
                    content = "서초-강남 구간 혼잡도가 높습니다. 우회 동선을 댓글로 공유해주세요.",
                    categoryType = CommunityCategoryType.ISSUE,
                    member = member,
                    subwayLineEntity = subwayLine,
                    station = station,
                ),
                CommunityPostEntity(
                    title = "$SEED_PREFIX 오늘 지연 정보 모아보기",
                    content = "지연 체감 구간/시간대를 정리해서 올려주세요. 증빙용 링크도 환영합니다.",
                    categoryType = CommunityCategoryType.INSIGHT,
                    member = member,
                    subwayLineEntity = subwayLine,
                    station = station,
                ),
            ),
        )

        commentRepository.saveAll(
            listOf(
                buildCommunityComment(member, posts[0], "지금 강남역 방면이 특히 붐비네요."),
                buildCommunityComment(member, posts[0], "열차 간격이 조금 늘어난 것 같습니다."),
                buildCommunityComment(member, posts[1], "출근시간 기준 08:20~08:50가 가장 혼잡했습니다."),
            ),
        )
        logger.info("[local-seed] community posts={}, comments={} 생성", posts.size, 3)
    }

    private fun seedComplaintPosts(member: MemberEntity, subwayLine: SubwayLineEntity, station: StationEntity?) {
        if (complaintPostRepository.count() > 0L) {
            return
        }

        val posts = complaintPostRepository.saveAll(
            listOf(
                ComplaintPostEntity(
                    complaintType = ComplaintType.ENVIRONMENTAL_COMPLAINT,
                    shortContentType = ShortContentType.WASTE,
                    content = "승강장 벤치 주변 청소가 필요합니다.",
                    phoneNumber = null,
                    trainNo = "2245",
                    location = 3,
                    member = member,
                    subwayLine = subwayLine,
                    station = station,
                ),
                ComplaintPostEntity(
                    complaintType = ComplaintType.ANNOUNCEMENT,
                    shortContentType = ShortContentType.NOT_HEARD,
                    content = "안내방송 음량이 작아 하차 안내를 듣기 어렵습니다.",
                    phoneNumber = null,
                    trainNo = "2311",
                    location = 6,
                    member = member,
                    subwayLine = subwayLine,
                    station = station,
                ),
            ),
        )

        commentRepository.saveAll(
            listOf(
                buildComplaintComment(member, posts[0], "동일 위치에서 반복적으로 발생 중입니다."),
                buildComplaintComment(member, posts[1], "안내방송 품질 개선이 필요해 보입니다."),
            ),
        )
        logger.info("[local-seed] complaint posts={}, comments={} 생성", posts.size, 2)
    }

    private fun seedLostPosts(member: MemberEntity, subwayLine: SubwayLineEntity, station: StationEntity?) {
        if (lostPostRepository.count() > 0L) {
            return
        }

        val posts = lostPostRepository.saveAll(
            listOf(
                LostPostEntity(
                    member = member,
                    subwayLine = subwayLine,
                    station = station,
                    category = null,
                    title = "$SEED_PREFIX 검정 카드지갑 분실",
                    content = "서초역 인근에서 검정 카드지갑을 잃어버렸습니다. 보신 분 제보 부탁드립니다.",
                    lostType = LostType.LOST,
                ),
                LostPostEntity(
                    member = member,
                    subwayLine = subwayLine,
                    station = station,
                    category = null,
                    title = "$SEED_PREFIX 파란 우산 습득",
                    content = "열차 4-2 칸에서 파란 우산을 습득했습니다. 주인을 찾습니다.",
                    lostType = LostType.ACQUIRE,
                ),
            ),
        )

        commentRepository.saveAll(
            listOf(
                buildLostComment(member, posts[0], "역무실에도 접수해두시면 찾기 쉬울 거예요."),
                buildLostComment(member, posts[1], "습득장소와 시간을 조금 더 자세히 알려주실 수 있나요?"),
            ),
        )
        logger.info("[local-seed] lost posts={}, comments={} 생성", posts.size, 2)
    }

    private fun resolveSeedMember(): MemberEntity {
        memberRepository.findAll().firstOrNull()?.let { return it }

        return memberRepository.save(
            MemberEntity(
                nickname = "로컬시드유저",
                providerUserId = "local-seed-user",
                provider = ProviderType.GOOGLE,
                email = "local.seed@ahhachul.dev",
                gender = null,
                ageRange = "20",
                status = MemberStatusType.ACTIVE,
            ),
        )
    }

    private fun buildCommunityComment(
        member: MemberEntity,
        post: CommunityPostEntity,
        content: String,
    ): CommentEntity {
        return CommentEntity(
            content = content,
            imageUrls = null,
            visibility = CommentVisibility.PUBLIC,
            upperComment = null,
            communityPost = post,
            lostPost = null,
            complaintPost = null,
            member = member,
        )
    }

    private fun buildComplaintComment(
        member: MemberEntity,
        post: ComplaintPostEntity,
        content: String,
    ): CommentEntity {
        return CommentEntity(
            content = content,
            imageUrls = null,
            visibility = CommentVisibility.PUBLIC,
            upperComment = null,
            communityPost = null,
            lostPost = null,
            complaintPost = post,
            member = member,
        )
    }

    private fun buildLostComment(
        member: MemberEntity,
        post: LostPostEntity,
        content: String,
    ): CommentEntity {
        return CommentEntity(
            content = content,
            imageUrls = null,
            visibility = CommentVisibility.PUBLIC,
            upperComment = null,
            communityPost = null,
            lostPost = post,
            complaintPost = null,
            member = member,
        )
    }

    companion object {
        private val logger = LoggerFactory.getLogger(LocalMockContentSeeder::class.java)
        private const val SEED_PREFIX = "[LOCAL-SEED]"
    }
}
