package backend.team.ahachul_backend.api.complaint.application.service

import backend.team.ahachul_backend.api.complaint.adapter.out.ComplaintPostRepository
import backend.team.ahachul_backend.api.complaint.application.command.`in`.CreateComplaintPostCommand
import backend.team.ahachul_backend.api.complaint.application.command.`in`.SearchComplaintPostCommand
import backend.team.ahachul_backend.api.complaint.application.command.`in`.UpdateComplaintPostCommand
import backend.team.ahachul_backend.api.complaint.application.command.`in`.UpdateComplaintPostStatusCommand
import backend.team.ahachul_backend.api.complaint.application.port.`in`.ComplaintPostUseCase
import backend.team.ahachul_backend.api.complaint.domain.entity.ComplaintPostEntity
import backend.team.ahachul_backend.api.complaint.domain.model.ComplaintPostType
import backend.team.ahachul_backend.api.complaint.domain.model.ComplaintType
import backend.team.ahachul_backend.api.complaint.domain.model.ShortContentType
import backend.team.ahachul_backend.api.member.adapter.web.out.MemberRepository
import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity
import backend.team.ahachul_backend.api.member.domain.model.GenderType
import backend.team.ahachul_backend.api.member.domain.model.MemberStatusType
import backend.team.ahachul_backend.api.member.domain.model.ProviderType
import backend.team.ahachul_backend.common.domain.entity.SubwayLineEntity
import backend.team.ahachul_backend.common.domain.model.RegionType
import backend.team.ahachul_backend.common.persistence.SubwayLineRepository
import backend.team.ahachul_backend.common.utils.RequestUtils
import backend.team.ahachul_backend.config.controller.CommonServiceTestConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class ComplaintPostServiceTest(
    @Autowired val complaintPostUseCase: ComplaintPostUseCase,
    @Autowired val complaintPostRepository: ComplaintPostRepository,
    @Autowired val memberRepository: MemberRepository,
    @Autowired val subwayLineRepository: SubwayLineRepository,
) : CommonServiceTestConfig() {

    lateinit var member: MemberEntity
    lateinit var subwayLine: SubwayLineEntity

    @BeforeEach
    fun setUp() {
        member = memberRepository.save(
            MemberEntity(
                nickname = "nickname",
                provider = ProviderType.GOOGLE,
                providerUserId = "providerUserId",
                email = "email",
                gender = GenderType.MALE,
                ageRange = "20",
                status = MemberStatusType.ACTIVE
            )
        )
        member.id.let { RequestUtils.setAttribute("memberId", it)}
        subwayLine = subwayLineRepository.save(
            SubwayLineEntity(
                name = "1호선",
                regionType = RegionType.METROPOLITAN
            )
        )
    }

    @Test
    fun 민원_조회_지하철_노선() {
        // given
        val createComplaintPostCommand = CreateComplaintPostCommand(
            complaintType = ComplaintType.ENVIRONMENTAL_COMPLAINT,
            shortContentType = ShortContentType.SELF,
            content = "내용",
            phoneNumber = null,
            trainNo = null,
            location = null,
            subwayLine = subwayLine.id,
            imageFiles = listOf(),
        )

        val complaintPost = ComplaintPostEntity.of(createComplaintPostCommand, member, subwayLine)
        complaintPostRepository.save(complaintPost)

        // when
        val searchComplaintPostCommand = SearchComplaintPostCommand(
            subwayLineId = subwayLine.id,
            keyword = null,
            pageToken = null,
            pageSize = 10
        )
        val searchComplaintPosts = complaintPostUseCase.searchComplaintPosts(searchComplaintPostCommand)

        // then
        assertThat(searchComplaintPosts.data).hasSize(1)
        assertThat(searchComplaintPosts.data[0].id).isEqualTo(complaintPost.id)
        assertThat(searchComplaintPosts.data[0].content).isEqualTo(complaintPost.content)
    }

    @Test
    fun 민원_조회_키워드() {
        // given
        val createComplaintPostCommand1 = CreateComplaintPostCommand(
            complaintType = ComplaintType.ENVIRONMENTAL_COMPLAINT,
            shortContentType = ShortContentType.SELF,
            content = "내용",
            phoneNumber = null,
            trainNo = null,
            location = null,
            subwayLine = subwayLine.id,
            imageFiles = listOf(),
        )

        val createComplaintPostCommand2 = CreateComplaintPostCommand(
            complaintType = ComplaintType.ENVIRONMENTAL_COMPLAINT,
            shortContentType = ShortContentType.SELF,
            content = "123",
            phoneNumber = null,
            trainNo = null,
            location = null,
            subwayLine = subwayLine.id,
            imageFiles = listOf(),
        )

        val complaintPost1 = ComplaintPostEntity.of(createComplaintPostCommand1, member, subwayLine)
        val complaintPost2 = ComplaintPostEntity.of(createComplaintPostCommand2, member, subwayLine)
        complaintPostRepository.save(complaintPost1)
        complaintPostRepository.save(complaintPost2)

        // when
        val searchComplaintPostCommand = SearchComplaintPostCommand(
            subwayLineId = null,
            keyword = "용",
            pageToken = null,
            pageSize = 10
        )
        val searchComplaintPosts = complaintPostUseCase.searchComplaintPosts(searchComplaintPostCommand)

        // then
        assertThat(searchComplaintPosts.data).hasSize(1)
        assertThat(searchComplaintPosts.data[0].id).isEqualTo(complaintPost1.id)
        assertThat(searchComplaintPosts.data[0].content).isEqualTo(complaintPost1.content)
    }

    @Test
    fun 민원_상세_조회() {
        // given
        val createComplaintPostCommand = CreateComplaintPostCommand(
            complaintType = ComplaintType.ENVIRONMENTAL_COMPLAINT,
            shortContentType = ShortContentType.SELF,
            content = "내용",
            phoneNumber = null,
            trainNo = null,
            location = null,
            subwayLine = subwayLine.id,
            imageFiles = listOf(),
        )

        val complaintPost = ComplaintPostEntity.of(createComplaintPostCommand, member, subwayLine)
        complaintPostRepository.save(complaintPost)

        // when
        val getComplaintPost = complaintPostUseCase.getComplaintPost(complaintPost.id)

        // then
        assertThat(getComplaintPost.id).isEqualTo(complaintPost.id)
        assertThat(getComplaintPost.content).isEqualTo(complaintPost.content)
        assertThat(getComplaintPost.complaintType).isEqualTo(complaintPost.complaintType)
        assertThat(getComplaintPost.shortContentType).isEqualTo(complaintPost.shortContentType)
    }

    @Test
    fun 민원_생성() {
        // given
        val createComplaintPostCommand = CreateComplaintPostCommand(
            complaintType = ComplaintType.ENVIRONMENTAL_COMPLAINT,
            shortContentType = ShortContentType.SELF,
            content = "내용",
            phoneNumber = null,
            trainNo = null,
            location = null,
            subwayLine = subwayLine.id,
            imageFiles = listOf(),
        )

        // when
        val createComplaintPost = complaintPostUseCase.createComplaintPost(createComplaintPostCommand)

        // then
        assertThat(createComplaintPost.id).isNotNull()
        assertThat(createComplaintPost.images).isEmpty()
    }

    @Test
    fun 민원_수정() {
        // given
        val createComplaintPostCommand = CreateComplaintPostCommand(
            complaintType = ComplaintType.ENVIRONMENTAL_COMPLAINT,
            shortContentType = ShortContentType.SELF,
            content = "내용",
            phoneNumber = null,
            trainNo = null,
            location = null,
            subwayLine = subwayLine.id,
            imageFiles = listOf(),
        )

        val complaintPost = ComplaintPostEntity.of(createComplaintPostCommand, member, subwayLine)
        complaintPostRepository.save(complaintPost)

        // when
        val updateComplaintPostCommand = UpdateComplaintPostCommand(
            id = complaintPost.id,
            shortContentType = ShortContentType.SELF,
            content = "내용 수정",
            phoneNumber = null,
            trainNo = null,
            location = null,
            complaintType = null,
            subwayLineId = null,
            status = null,
            imageFiles = null,
            removeFileIds = null,
        )
        complaintPostUseCase.updateComplaintPost(updateComplaintPostCommand)
        val updatedComplaintPost = complaintPostRepository.findById(updateComplaintPostCommand.id).get()

        // then
        assertThat(updatedComplaintPost.shortContentType).isEqualTo(updateComplaintPostCommand.shortContentType)
        assertThat(updatedComplaintPost.content).isEqualTo(updateComplaintPostCommand.content)
    }

    @Test
    fun 민원_상태_수정() {
        // given
        val createComplaintPostCommand = CreateComplaintPostCommand(
            complaintType = ComplaintType.ENVIRONMENTAL_COMPLAINT,
            shortContentType = ShortContentType.SELF,
            content = "내용",
            phoneNumber = null,
            trainNo = null,
            location = null,
            subwayLine = subwayLine.id,
            imageFiles = listOf(),
        )

        val complaintPost = ComplaintPostEntity.of(createComplaintPostCommand, member, subwayLine)
        complaintPostRepository.save(complaintPost)

        // when
        val updateComplaintPostStatusCommand = UpdateComplaintPostStatusCommand(
            id = complaintPost.id,
            status = ComplaintPostType.IN_PROGRESS,
        )
        complaintPostUseCase.updateComplaintPostStatus(updateComplaintPostStatusCommand)
        val updatedComplaintPost = complaintPostRepository.findById(updateComplaintPostStatusCommand.id).get()

        // then
        assertThat(updatedComplaintPost.status).isEqualTo(updateComplaintPostStatusCommand.status)
    }

    @Test
    fun 민원_삭제() {
        // given
        val createComplaintPostCommand = CreateComplaintPostCommand(
            complaintType = ComplaintType.ENVIRONMENTAL_COMPLAINT,
            shortContentType = ShortContentType.SELF,
            content = "내용",
            phoneNumber = null,
            trainNo = null,
            location = null,
            subwayLine = subwayLine.id,
            imageFiles = listOf(),
        )

        val complaintPost = ComplaintPostEntity.of(createComplaintPostCommand, member, subwayLine)
        complaintPostRepository.save(complaintPost)

        // when
        complaintPostUseCase.deleteComplaintPost(complaintPost.id)
        val deleteComplaintPost = complaintPostRepository.findById(complaintPost.id).get()

        // then
        assertThat(deleteComplaintPost.status).isEqualTo(ComplaintPostType.DELETED)
    }
}