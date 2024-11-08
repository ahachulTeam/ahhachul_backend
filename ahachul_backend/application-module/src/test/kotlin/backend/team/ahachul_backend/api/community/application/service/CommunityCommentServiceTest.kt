package backend.team.ahachul_backend.api.community.application.service

import backend.team.ahachul_backend.api.community.adapter.web.`in`.dto.comment.CreateCommunityCommentCommand
import backend.team.ahachul_backend.api.community.adapter.web.`in`.dto.comment.DeleteCommunityCommentCommand
import backend.team.ahachul_backend.api.community.adapter.web.`in`.dto.comment.GetCommunityCommentsCommand
import backend.team.ahachul_backend.api.community.adapter.web.`in`.dto.comment.UpdateCommunityCommentCommand
import backend.team.ahachul_backend.api.community.adapter.web.out.CommunityCommentRepository
import backend.team.ahachul_backend.api.community.adapter.web.out.CommunityPostRepository
import backend.team.ahachul_backend.api.community.application.port.`in`.CommunityCommentUseCase
import backend.team.ahachul_backend.api.community.domain.entity.CommunityPostEntity
import backend.team.ahachul_backend.api.community.domain.model.CommunityCategoryType
import backend.team.ahachul_backend.api.community.domain.model.CommunityCommentType
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
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class CommunityCommentServiceTest(
    @Autowired val communityCommentRepository: CommunityCommentRepository,
    @Autowired val communityCommentUseCase: CommunityCommentUseCase,
    @Autowired val communityPostRepository: CommunityPostRepository,
    @Autowired val memberRepository: MemberRepository,
    @Autowired val subwayLineRepository: SubwayLineRepository
): CommonServiceTestConfig() {

    private lateinit var subwayLine: SubwayLineEntity

    @BeforeEach
    fun setup() {
        val member = memberRepository.save(
            MemberEntity(
            nickname = "nickname",
            provider = ProviderType.KAKAO,
            providerUserId = "providerUserId",
            email = "email",
            gender = GenderType.MALE,
            ageRange = "20",
            status = MemberStatusType.ACTIVE
        )
        )
        member.id.let { RequestUtils.setAttribute("memberId", it) }
        subwayLine = subwayLineRepository.save(SubwayLineEntity(name = "1호선", regionType = RegionType.METROPOLITAN))
    }

    @Test
    @DisplayName("커뮤니티 코멘트 생성")
    fun 커뮤니티_코멘트_생성() {
        // given
        val post = communityPostRepository.save(
            CommunityPostEntity(
                title = "제목",
                content = "내용",
                categoryType = CommunityCategoryType.FREE,
                subwayLineEntity = subwayLine
            )
        )

        val createCommunityCommentCommand = CreateCommunityCommentCommand(
            postId = post.id,
            upperCommentId = null,
            content = "내용"
        )

        // when
        val result = communityCommentUseCase.createCommunityComment(createCommunityCommentCommand)

        // then
        assertThat(result.id).isNotNull()
        assertThat(result.content).isEqualTo("내용")
        assertThat(result.upperCommentId).isNull()

        val communityComment = communityCommentRepository.findById(result.id).get()

        assertThat(communityComment.id).isEqualTo(result.id)
        assertThat(communityComment.status).isEqualTo(CommunityCommentType.CREATED)
    }

    @Test
    @DisplayName("커뮤니티 코멘트 수정")
    fun 커뮤니티_코멘트_수정() {
        // given
        val post = communityPostRepository.save(
            CommunityPostEntity(
                title = "제목",
                content = "내용",
                categoryType = CommunityCategoryType.FREE,
                subwayLineEntity = subwayLine
            )
        )
        val createCommunityCommentCommand = CreateCommunityCommentCommand(
            postId = post.id,
            upperCommentId = null,
            content = "내용"
        )
        val communityComment = communityCommentUseCase.createCommunityComment(createCommunityCommentCommand)

        val updateCommunityCommentCommand = UpdateCommunityCommentCommand(
            id = communityComment.id,
            content = "수정된 내용"
        )

        // when
        val result = communityCommentUseCase.updateCommunityComment(updateCommunityCommentCommand)

        // then
        assertThat(result.id).isEqualTo(communityComment.id)
        assertThat(result.content).isEqualTo("수정된 내용")
    }

    @Test
    @DisplayName("커뮤니티 코멘트 삭제")
    fun 커뮤니티_코멘트_삭제() {
        // given
        val post = communityPostRepository.save(
            CommunityPostEntity(
                title = "제목",
                content = "내용",
                categoryType = CommunityCategoryType.FREE,
                subwayLineEntity = subwayLine
            )
        )
        val createCommunityCommentCommand = CreateCommunityCommentCommand(
            postId = post.id,
            upperCommentId = null,
            content = "내용"
        )
        val communityCommentRes = communityCommentUseCase.createCommunityComment(createCommunityCommentCommand)

        val deleteCommunityCommentCommand = DeleteCommunityCommentCommand(
            id = communityCommentRes.id
        )

        // when
        val result = communityCommentUseCase.deleteCommunityComment(deleteCommunityCommentCommand)

        // then
        assertThat(result.id).isEqualTo(communityCommentRes.id)

        val communityComment = communityCommentRepository.findById(result.id).get()

        assertThat(communityComment.status).isEqualTo(CommunityCommentType.DELETED)
    }

    @Test
    @DisplayName("커뮤니티 코멘트 조회")
    fun 커뮤니티_코멘트_조회() {
        // given
        val post = communityPostRepository.save(
            CommunityPostEntity(
                title = "제목",
                content = "내용",
                categoryType = CommunityCategoryType.FREE,
                subwayLineEntity = subwayLine
            )
        )
        for (i in 1..10) {
            val createCommunityCommentCommand = CreateCommunityCommentCommand(
                postId = post.id,
                upperCommentId = null,
                content = "내용${i}"
            )
            communityCommentUseCase.createCommunityComment(createCommunityCommentCommand)
        }

        val getCommunityCommentsCommand = GetCommunityCommentsCommand(
            postId = post.id
        )

        // when
        val result = communityCommentUseCase.getCommunityComments(getCommunityCommentsCommand)

        // then
        assertThat(result.comments).hasSize(10)
        for (i: Int in 0..9) {
            assertThat(result.comments[i].parentComment).isNotNull
            assertThat(result.comments[i].childComments).isEmpty()
        }
    }

    @Test
    @DisplayName("커뮤니티 자식 코멘트 조회")
    fun 커뮤니티_자식_코멘트_조회() {
        // given
        val post = communityPostRepository.save(
            CommunityPostEntity(
                title = "제목",
                content = "내용",
                categoryType = CommunityCategoryType.FREE,
                subwayLineEntity = subwayLine
            )
        )

        val createCommunityCommentCommand = CreateCommunityCommentCommand(
            postId = post.id,
            upperCommentId = null,
            content = "내용"
        )
        val (upper_comment_id, _, _) = communityCommentUseCase.createCommunityComment(createCommunityCommentCommand)

        val createChildCommunityCommentCommand = CreateCommunityCommentCommand(
            postId = post.id,
            upperCommentId = upper_comment_id,
            content = "내용"
        )
        for (i in 1..4) {
            communityCommentUseCase.createCommunityComment(createChildCommunityCommentCommand)
        }

        val getCommunityCommentsCommand = GetCommunityCommentsCommand(
            postId = post.id
        )

        // when
        val result = communityCommentUseCase.getCommunityComments(getCommunityCommentsCommand)

        // then
        assertThat(result.comments.size).isEqualTo(1)
        assertThat(result.comments[0].parentComment.id).isEqualTo(upper_comment_id)
        assertThat(result.comments[0].childComments).hasSize(4)
    }
}
