package backend.team.ahachul_backend.api.comment.application.service

import backend.team.ahachul_backend.api.comment.adapter.web.out.CommentRepository
import backend.team.ahachul_backend.api.comment.application.command.CreateCommentCommand
import backend.team.ahachul_backend.api.comment.application.command.DeleteCommentCommand
import backend.team.ahachul_backend.api.comment.application.command.GetCommentsCommand
import backend.team.ahachul_backend.api.comment.application.command.UpdateCommentCommand
import backend.team.ahachul_backend.api.comment.domain.model.CommentType
import backend.team.ahachul_backend.api.community.adapter.web.out.CommunityPostRepository
import backend.team.ahachul_backend.api.comment.application.port.`in`.CommentUseCase
import backend.team.ahachul_backend.api.community.domain.entity.CommunityPostEntity
import backend.team.ahachul_backend.api.community.domain.model.CommunityCategoryType
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
class CommentServiceTest(
    @Autowired val commentRepository: CommentRepository,
    @Autowired val commentUseCase: CommentUseCase,
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
    @DisplayName("코멘트 생성")
    fun 코멘트_생성() {
        // given
        val post = communityPostRepository.save(
            CommunityPostEntity(
                title = "제목",
                content = "내용",
                categoryType = CommunityCategoryType.FREE,
                subwayLineEntity = subwayLine
            )
        )

        val createCommentCommand = CreateCommentCommand(
            postId = post.id,
            upperCommentId = null,
            content = "내용"
        )

        // when
        val result = commentUseCase.createComment(createCommentCommand)

        // then
        assertThat(result.id).isNotNull()
        assertThat(result.content).isEqualTo("내용")
        assertThat(result.upperCommentId).isNull()

        val comment = commentRepository.findById(result.id).get()

        assertThat(comment.id).isEqualTo(result.id)
        assertThat(comment.status).isEqualTo(CommentType.CREATED)
    }

    @Test
    @DisplayName("코멘트 수정")
    fun 코멘트_수정() {
        // given
        val post = communityPostRepository.save(
            CommunityPostEntity(
                title = "제목",
                content = "내용",
                categoryType = CommunityCategoryType.FREE,
                subwayLineEntity = subwayLine
            )
        )
        val createCommentCommand = CreateCommentCommand(
            postId = post.id,
            upperCommentId = null,
            content = "내용"
        )
        val comment = commentUseCase.createComment(createCommentCommand)

        val updateCommentCommand = UpdateCommentCommand(
            id = comment.id,
            content = "수정된 내용"
        )

        // when
        val result = commentUseCase.updateComment(updateCommentCommand)

        // then
        assertThat(result.id).isEqualTo(comment.id)
        assertThat(result.content).isEqualTo("수정된 내용")
    }

    @Test
    @DisplayName("코멘트 삭제")
    fun 코멘트_삭제() {
        // given
        val post = communityPostRepository.save(
            CommunityPostEntity(
                title = "제목",
                content = "내용",
                categoryType = CommunityCategoryType.FREE,
                subwayLineEntity = subwayLine
            )
        )
        val createCommentCommand = CreateCommentCommand(
            postId = post.id,
            upperCommentId = null,
            content = "내용"
        )
        val commentRes = commentUseCase.createComment(createCommentCommand)

        val deleteCommentCommand = DeleteCommentCommand(
            id = commentRes.id
        )

        // when
        val result = commentUseCase.deleteComment(deleteCommentCommand)

        // then
        assertThat(result.id).isEqualTo(commentRes.id)

        val comment = commentRepository.findById(result.id).get()

        assertThat(comment.status).isEqualTo(CommentType.DELETED)
    }

    @Test
    @DisplayName("코멘트 조회")
    fun 코멘트_조회() {
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
            val createCommentCommand = CreateCommentCommand(
                postId = post.id,
                upperCommentId = null,
                content = "내용${i}"
            )
            commentUseCase.createComment(createCommentCommand)
        }

        val getCommentsCommand = GetCommentsCommand(
            postId = post.id
        )

        // when
        val result = commentUseCase.getComments(getCommentsCommand)

        // then
        assertThat(result.comments).hasSize(10)
        for (i: Int in 0..9) {
            assertThat(result.comments[i].parentComment).isNotNull
            assertThat(result.comments[i].childComments).isEmpty()
        }
    }

    @Test
    @DisplayName("자식 코멘트 조회")
    fun 자식_코멘트_조회() {
        // given
        val post = communityPostRepository.save(
            CommunityPostEntity(
                title = "제목",
                content = "내용",
                categoryType = CommunityCategoryType.FREE,
                subwayLineEntity = subwayLine
            )
        )

        val createCommentCommand = CreateCommentCommand(
            postId = post.id,
            upperCommentId = null,
            content = "내용"
        )
        val (upper_comment_id, _, _) = commentUseCase.createComment(createCommentCommand)

        val createChildCommentCommand = CreateCommentCommand(
            postId = post.id,
            upperCommentId = upper_comment_id,
            content = "내용"
        )
        for (i in 1..4) {
            commentUseCase.createComment(createChildCommentCommand)
        }

        val getCommentsCommand = GetCommentsCommand(
            postId = post.id
        )

        // when
        val result = commentUseCase.getComments(getCommentsCommand)

        // then
        assertThat(result.comments.size).isEqualTo(1)
        assertThat(result.comments[0].parentComment.id).isEqualTo(upper_comment_id)
        assertThat(result.comments[0].childComments).hasSize(4)
    }
}
