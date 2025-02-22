package backend.team.ahachul_backend.api.report.application.service

import backend.team.ahachul_backend.api.lost.adapter.web.out.CategoryRepository
import backend.team.ahachul_backend.api.lost.adapter.web.out.LostPostRepository
import backend.team.ahachul_backend.api.lost.application.service.command.`in`.CreateLostPostCommand
import backend.team.ahachul_backend.api.lost.domain.entity.CategoryEntity
import backend.team.ahachul_backend.api.lost.domain.entity.LostPostEntity
import backend.team.ahachul_backend.api.lost.domain.model.LostPostType
import backend.team.ahachul_backend.api.lost.domain.model.LostType
import backend.team.ahachul_backend.api.member.adapter.web.out.MemberRepository
import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity
import backend.team.ahachul_backend.api.member.domain.model.GenderType
import backend.team.ahachul_backend.api.member.domain.model.MemberStatusType
import backend.team.ahachul_backend.api.member.domain.model.ProviderType
import backend.team.ahachul_backend.api.report.application.port.`in`.ReportUseCase
import backend.team.ahachul_backend.common.domain.entity.SubwayLineEntity
import backend.team.ahachul_backend.common.domain.model.RegionType
import backend.team.ahachul_backend.common.exception.DomainException
import backend.team.ahachul_backend.common.persistence.SubwayLineRepository
import backend.team.ahachul_backend.common.response.ResponseCode
import backend.team.ahachul_backend.common.utils.RequestUtils
import backend.team.ahachul_backend.config.controller.CommonServiceTestConfig
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class LostPostReportServiceTest(
    @Autowired val lostPostReportService: ReportUseCase,
    @Autowired val memberRepository: MemberRepository,
    @Autowired val lostPostRepository: LostPostRepository,
    @Autowired val subwayLineRepository: SubwayLineRepository,
    @Autowired val categoryRepository: CategoryRepository
): CommonServiceTestConfig() {

    private var subwayLine: SubwayLineEntity? = null
    private var category: CategoryEntity? = null
    private var member: MemberEntity? = null
    private var otherMember: MemberEntity? = null
    private var manager: MemberEntity? = null

    @BeforeEach
    fun setup() {
        member = memberRepository.save(createMember("닉네임1"))
        otherMember = memberRepository.save(createMember("닉네임2"))
        manager = memberRepository.save(createMember("관리자"))
        member!!.id.let { RequestUtils.setAttribute("memberId", it) }
        subwayLine = createSubwayLine()
        category = categoryRepository.save(CategoryEntity(name = "핸드폰"))
    }

    @Test
    @DisplayName("신고 저장 테스트")
    fun saveReport() {
        // given
        val target = lostPostRepository.save(createLostPost())

        // when
        val result = lostPostReportService.save(target.id)

        // then
        Assertions.assertThat(result.targetId).isEqualTo(target.id)
        Assertions.assertThat(result.sourceMemberId).isEqualTo(member!!.id)
        Assertions.assertThat(result.targetMemberId).isEqualTo(otherMember!!.id)
    }

    @Test
    @DisplayName("본인이 작성한 게시물 신고 테스트")
    fun checkSameTargetReport() {
        // given
        val target = lostPostRepository.save(createLostPost())

        // when, then
        otherMember!!.id.let { RequestUtils.setAttribute("memberId", it) }

        Assertions.assertThatThrownBy {
            lostPostReportService.save(target.id)
        }
            .isExactlyInstanceOf(DomainException::class.java)
            .hasMessage(ResponseCode.INVALID_REPORT_REQUEST.message)
    }

    @Test
    @DisplayName("중복으로 같은 게시물 신고 테스트")
    fun checkDuplicateReport() {
        // given
        val target = lostPostRepository.save(createLostPost())
        val result = lostPostReportService.save(target.id)
        Assertions.assertThat(target.lostPostReports.size).isEqualTo(1)

        // when, then
        Assertions.assertThatThrownBy {
            lostPostReportService.save(target.id)
        }
            .isExactlyInstanceOf(DomainException::class.java)
            .hasMessage(ResponseCode.DUPLICATE_REPORT_REQUEST.message)
    }

    @Test
    @DisplayName("유실물 게시물 블락 처리 테스트")
    fun blockLostPost() {
        // given
        val target = lostPostRepository.save(createLostPost())    // 신고 대상
        val otherMember2 = memberRepository.save(createMember("닉네임3"))
        val otherMember3 = memberRepository.save(createMember("닉네임4"))
        val otherMember4 = memberRepository.save(createMember("닉네임4"))
        val otherMember5 = memberRepository.save(createMember("닉네임4"))

        // when
        lostPostReportService.save(target.id)

        RequestUtils.setAttribute("memberId", otherMember2.id)
        lostPostReportService.save(target.id)

        RequestUtils.setAttribute("memberId", otherMember3.id)
        lostPostReportService.save(target.id)

        RequestUtils.setAttribute("memberId", otherMember4.id)
        lostPostReportService.save(target.id)

        RequestUtils.setAttribute("memberId", otherMember5.id)
        lostPostReportService.save(target.id)

        // then
        Assertions.assertThat(target.lostPostReports.size).isEqualTo(5)
        Assertions.assertThat(target.type).isEqualTo(LostPostType.BLOCKED)
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

    private fun createLostPost(): LostPostEntity {
        return LostPostEntity.of(
            command = CreateLostPostCommand(
                title = "지갑",
                content = "내용",
                subwayLine = subwayLine!!.id,
                lostType = LostType.LOST,
                categoryName = "지갑"
            ),
            member = otherMember!!,
            subwayLine = subwayLine!!,
            category = category!!
        )
    }
}
