package backend.team.ahachul_backend.admin.application.service

import backend.team.ahachul_backend.api.community.application.command.`in`.CreateCommunityPostCommand
import backend.team.ahachul_backend.api.community.adapter.web.out.CommunityPostRepository
import backend.team.ahachul_backend.api.community.domain.entity.CommunityPostEntity
import backend.team.ahachul_backend.api.community.domain.model.CommunityCategoryType
import backend.team.ahachul_backend.api.member.adapter.web.out.MemberRepository
import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity
import backend.team.ahachul_backend.api.member.domain.model.GenderType
import backend.team.ahachul_backend.api.member.domain.model.MemberStatusType
import backend.team.ahachul_backend.api.member.domain.model.ProviderType
import backend.team.ahachul_backend.api.report.application.port.`in`.command.ActionReportCommand
import backend.team.ahachul_backend.api.report.application.service.CommunityPostReportService
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


class AdminServiceTest(
    @Autowired val adminService: AdminService,
    @Autowired val communityPostReportService:CommunityPostReportService,
    @Autowired val memberRepository: MemberRepository,
    @Autowired val communityPostRepository: CommunityPostRepository,
    @Autowired val subwayLineRepository: SubwayLineRepository
): CommonServiceTestConfig() {

    private var subwayLine: SubwayLineEntity? = null
    private var member: MemberEntity? = null
    private var otherMember: MemberEntity? = null
    private var manager: MemberEntity? = null

    @BeforeEach
    fun setup() {
        member = memberRepository.save(createMember("닉네임1"))
        otherMember = memberRepository.save(createMember("닉네임2"))
        manager = memberRepository.save(createMember("관리자"))
        member!!.id.let { RequestUtils.setAttribute(RequestUtils.Attribute.MEMBER_ID, it) }
        subwayLine = createSubwayLine()
    }

    @Test
    @DisplayName("특정 신고 횟수를 넘지 못했을 때는 블락 불가능 테스트")
    fun invalidConditionToBlock() {
        // given
        val target = communityPostRepository.save(createCommunityPost())
        communityPostReportService.save(target.id)
        val command = ActionReportCommand(target.member!!.id, "post")

        // when, then
        Assertions.assertThatThrownBy {
            adminService.actionOnReport(command)
        }
            .isExactlyInstanceOf(DomainException::class.java)
            .hasMessage(ResponseCode.INVALID_CONDITION_TO_BLOCK_MEMBER.message)
    }

    @Test
    @DisplayName("관리자가 유저를 중복으로 블락하지 못하는 테스트")
    fun duplicateBlockAction() {
        // given
        val target = communityPostRepository.save(createCommunityPost())  // 신고 대상
        val otherMember2 = memberRepository.save(createMember("닉네임3"))
        val otherMember3 = memberRepository.save(createMember("닉네임4"))

        // when
        communityPostReportService.save(target.id)

        RequestUtils.setAttribute(RequestUtils.Attribute.MEMBER_ID, otherMember2.id)
        communityPostReportService.save(target.id)

        RequestUtils.setAttribute(RequestUtils.Attribute.MEMBER_ID, otherMember3.id)
        communityPostReportService.save(target.id)

        val command = ActionReportCommand(target.member!!.id, "post")
        adminService.actionOnReport(command)

        // then
        Assertions.assertThat(target.member!!.status).isEqualTo(MemberStatusType.SUSPENDED)
        Assertions.assertThatThrownBy {
            adminService.actionOnReport(command)
        }
            .isExactlyInstanceOf(DomainException::class.java)
            .hasMessage(ResponseCode.INVALID_REPORT_ACTION.message)
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
