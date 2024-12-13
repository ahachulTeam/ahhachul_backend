package backend.team.ahachul_backend.api.report.application.service

import backend.team.ahachul_backend.api.community.application.command.`in`.CreateCommunityPostCommand
import backend.team.ahachul_backend.api.community.adapter.web.out.CommunityPostRepository
import backend.team.ahachul_backend.api.community.domain.entity.CommunityPostEntity
import backend.team.ahachul_backend.api.community.domain.model.CommunityCategoryType
import backend.team.ahachul_backend.api.community.domain.model.CommunityPostType
import backend.team.ahachul_backend.api.member.adapter.web.out.MemberRepository
import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity
import backend.team.ahachul_backend.api.member.domain.model.GenderType
import backend.team.ahachul_backend.api.member.domain.model.MemberStatusType
import backend.team.ahachul_backend.api.member.domain.model.ProviderType
import backend.team.ahachul_backend.api.report.application.port.`in`.ReportUseCase
import backend.team.ahachul_backend.common.domain.entity.SubwayLineEntity
import backend.team.ahachul_backend.common.exception.DomainException
import backend.team.ahachul_backend.common.domain.model.RegionType
import backend.team.ahachul_backend.common.persistence.SubwayLineRepository
import backend.team.ahachul_backend.common.response.ResponseCode
import backend.team.ahachul_backend.common.utils.RequestUtils
import backend.team.ahachul_backend.config.controller.CommonServiceTestConfig
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class CommunityPostReportServiceTest(
    @Autowired val communityPostReportService: ReportUseCase,
    @Autowired val memberRepository: MemberRepository,
    @Autowired val communityPostRepository: CommunityPostRepository,
    @Autowired val subwayLineRepository: SubwayLineRepository
): CommonServiceTestConfig() {

    private var subwayLine: SubwayLineEntity? = null
    private var member: MemberEntity? = null
    private var otherMember: MemberEntity? = null

    @BeforeEach
    fun setup() {
        member = memberRepository.save(createMember("닉네임1"))
        member!!.id.let { RequestUtils.setAttribute(RequestUtils.Attribute.MEMBER_ID, it) }
        otherMember = memberRepository.save(createMember("닉네임2"))
        subwayLine = createSubwayLine()
    }

    @Test
    @DisplayName("신고 저장")
    fun saveReport() {
        // given
        val target = communityPostRepository.save(createCommunityPost())

        // when
        val result = communityPostReportService.save(target.id)

        // then
        Assertions.assertThat(result.targetId).isEqualTo(target.id)
        Assertions.assertThat(result.sourceMemberId).isEqualTo(member!!.id)
        Assertions.assertThat(result.targetMemberId).isEqualTo(otherMember!!.id)
    }

    @Test
    @DisplayName("본인이 작성한 게시물 신고 테스트")
    fun checkSameTargetReport() {
        // given
        val target = communityPostRepository.save(createCommunityPost())

        // when, then
        otherMember!!.id.let { RequestUtils.setAttribute(RequestUtils.Attribute.MEMBER_ID, it) }

        Assertions.assertThatThrownBy {
            communityPostReportService.save(target.id)
        }
            .isExactlyInstanceOf(DomainException::class.java)
            .hasMessage(ResponseCode.INVALID_REPORT_REQUEST.message)
    }

    @Test
    @DisplayName("중복으로 같은 게시물 신고 테스트")
    fun checkDuplicateReport() {
        // given
        val target = communityPostRepository.save(createCommunityPost())
        communityPostReportService.save(target.id)

        // when, then
        Assertions.assertThatThrownBy {
            communityPostReportService.save(target.id)
        }
            .isExactlyInstanceOf(DomainException::class.java)
            .hasMessage(ResponseCode.DUPLICATE_REPORT_REQUEST.message)
    }

    @Test
    @DisplayName("유실물 게시물 블락 처리 테스트")
    fun blockLostPost() {
        // given
        val target = communityPostRepository.save(createCommunityPost())    // 신고 대상
        val otherMember2 = memberRepository.save(createMember("닉네임3"))
        val otherMember3 = memberRepository.save(createMember("닉네임4"))
        val otherMember4 = memberRepository.save(createMember("닉네임4"))
        val otherMember5 = memberRepository.save(createMember("닉네임4"))

        // when
        communityPostReportService.save(target.id)

        RequestUtils.setAttribute(RequestUtils.Attribute.MEMBER_ID, otherMember2.id)
        communityPostReportService.save(target.id)

        RequestUtils.setAttribute(RequestUtils.Attribute.MEMBER_ID, otherMember3.id)
        communityPostReportService.save(target.id)

        RequestUtils.setAttribute(RequestUtils.Attribute.MEMBER_ID, otherMember4.id)
        communityPostReportService.save(target.id)

        RequestUtils.setAttribute(RequestUtils.Attribute.MEMBER_ID, otherMember5.id)
        communityPostReportService.save(target.id)

        // then
        Assertions.assertThat(target.communityPostReports.size).isEqualTo(5)
        Assertions.assertThat(target.status).isEqualTo(CommunityPostType.BLOCKED)
    }

    private fun createMember(nickname: String): MemberEntity {
        return MemberEntity(
            nickname = nickname,
            provider = ProviderType.KAKAO,
            providerUserId = "providerUserId",
            email = "email",
            gender = GenderType.MALE,
            ageRange = "20",
            status = MemberStatusType.ACTIVE
        )
    }

    private fun createSubwayLine(): SubwayLineEntity {
        return subwayLineRepository.save(
            SubwayLineEntity(
                name = "임시 호선",
                regionType = RegionType.METROPOLITAN
            )
        )
    }

    private fun createCommunityPost(): CommunityPostEntity {
        return CommunityPostEntity.of(
            CreateCommunityPostCommand(
                title = "제목",
                content = "내용",
                categoryType = CommunityCategoryType.FREE,
                subwayLineId = subwayLine!!.id),
            otherMember!!,
            subwayLine!!
        )
    }
}
