package backend.team.ahachul_backend.api.comment.application.service

import backend.team.ahachul_backend.api.comment.adapter.web.out.CommentRepository
import backend.team.ahachul_backend.api.comment.application.command.CreateCommentCommand
import backend.team.ahachul_backend.api.comment.application.command.DeleteCommentCommand
import backend.team.ahachul_backend.api.comment.application.command.GetCommentsCommand
import backend.team.ahachul_backend.api.comment.application.command.UpdateCommentCommand
import backend.team.ahachul_backend.api.comment.application.port.`in`.CommentLikeUseCase
import backend.team.ahachul_backend.api.comment.application.port.`in`.CommentUseCase
import backend.team.ahachul_backend.api.comment.domain.model.CommentType
import backend.team.ahachul_backend.api.comment.domain.model.CommentVisibility
import backend.team.ahachul_backend.api.comment.domain.model.PostType
import backend.team.ahachul_backend.api.community.adapter.web.out.CommunityPostRepository
import backend.team.ahachul_backend.api.community.domain.entity.CommunityPostEntity
import backend.team.ahachul_backend.api.community.domain.model.CommunityCategoryType
import backend.team.ahachul_backend.api.lost.adapter.web.out.CategoryRepository
import backend.team.ahachul_backend.api.lost.adapter.web.out.LostPostRepository
import backend.team.ahachul_backend.api.lost.domain.entity.CategoryEntity
import backend.team.ahachul_backend.api.lost.domain.entity.LostPostEntity
import backend.team.ahachul_backend.api.lost.domain.model.LostType
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
import org.springframework.data.domain.Sort
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class CommentServiceTest(
    @Autowired val commentRepository: CommentRepository,
    @Autowired val commentUseCase: CommentUseCase,
    @Autowired val commentLikeUseCase: CommentLikeUseCase,
    @Autowired val memberRepository: MemberRepository,
    @Autowired val subwayLineRepository: SubwayLineRepository,
    @Autowired val categoryRepository: CategoryRepository,
    @Autowired val communityPostRepository: CommunityPostRepository,
    @Autowired val lostPostRepository: LostPostRepository,
) : CommonServiceTestConfig() {

    private lateinit var subwayLine: SubwayLineEntity
    private lateinit var category: CategoryEntity
    private lateinit var communityPost: CommunityPostEntity
    private lateinit var lostPost: LostPostEntity
    private val memberIds: MutableList<Long> = ArrayList()
    private val membersCount: Int = 5

    private fun loginWithMemberId(memberId: Long) {
        RequestUtils.setAttribute("memberId", memberId)
    }
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
        lateinit var postWriter: MemberEntity

        for (i in 1..membersCount) {
            val anotherMember = memberRepository.save(
                MemberEntity(
                    nickname = "nickname${i}",
                    provider = ProviderType.KAKAO,
                    providerUserId = "providerUserId${i}",
                    email = "email${i}",
                    gender = GenderType.MALE,
                    ageRange = "20",
                    status = MemberStatusType.ACTIVE
                )
            )

            if (i == 1) {
                postWriter = anotherMember
            }

            memberIds.add(anotherMember.id)
        }
        loginWithMemberId(memberIds[0])

        subwayLine = subwayLineRepository.save(SubwayLineEntity(name = "1호선", regionType = RegionType.METROPOLITAN))
        category = categoryRepository.save(CategoryEntity(name = "휴대폰"))
        communityPost = communityPostRepository.save(
            CommunityPostEntity(
                title = "제목",
                content = "내용",
                member = postWriter,
                categoryType = CommunityCategoryType.FREE,
                subwayLineEntity = subwayLine
            )
        )
        lostPost = lostPostRepository.save(
            LostPostEntity(
                title = "제목",
                content = "내용",
                member = postWriter,
                subwayLine = subwayLine,
                lostType = LostType.LOST,
                category = category
            )
        )
    }

    @Test
    @DisplayName("커뮤니티 코멘트 생성")
    fun 커뮤니티_코멘트_생성() {
        // given
        val createCommentCommand = CreateCommentCommand(
            postId = communityPost.id,
            postType = PostType.COMMUNITY,
            upperCommentId = null,
            content = "내용",
            visibility = CommentVisibility.PUBLIC
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
        assertThat(comment.communityPost).isEqualTo(communityPost)
        assertThat(comment.lostPost).isNull()
        assertThat(comment.visibility).isEqualTo(CommentVisibility.PUBLIC)
    }

    @Test
    @DisplayName("유실물 코멘트 생성")
    fun 유실물_코멘트_생성() {
        // given
        val createCommentCommand = CreateCommentCommand(
            postId = lostPost.id,
            postType = PostType.LOST,
            upperCommentId = null,
            content = "내용",
            visibility = CommentVisibility.PRIVATE
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
        assertThat(comment.lostPost).isEqualTo(lostPost)
        assertThat(comment.communityPost).isNull()
        assertThat(comment.visibility).isEqualTo(CommentVisibility.PRIVATE)
    }

    @Test
    @DisplayName("코멘트 수정")
    fun 코멘트_수정() {
        // given
        val createCommentCommand = CreateCommentCommand(
            postId = communityPost.id,
            postType = PostType.COMMUNITY,
            upperCommentId = null,
            content = "내용",
            visibility = CommentVisibility.PUBLIC
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
        val createCommentCommand = CreateCommentCommand(
            postId = communityPost.id,
            postType = PostType.COMMUNITY,
            upperCommentId = null,
            content = "내용",
            visibility = CommentVisibility.PUBLIC
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
    @DisplayName("커뮤니티 코멘트 조회")
    fun 커뮤니티_코멘트_조회() {
        // given
        for (i in 1..10) {
            val createCommentCommand = CreateCommentCommand(
                postId = communityPost.id,
                postType = PostType.COMMUNITY,
                upperCommentId = null,
                content = "내용${i}",
                visibility = CommentVisibility.PUBLIC
            )
            commentUseCase.createComment(createCommentCommand)
        }

        val getCommentsCommand = GetCommentsCommand(
            postId = communityPost.id,
            PostType.COMMUNITY,
            Sort.unsorted()
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
    @DisplayName("유실물 코멘트 조회")
    fun 유실물_코멘트_조회() {
        // given
        for (i in 1..10) {
            val createCommentCommand = CreateCommentCommand(
                postId = lostPost.id,
                postType = PostType.LOST,
                upperCommentId = null,
                content = "내용${i}",
                visibility = CommentVisibility.PUBLIC
            )
            commentUseCase.createComment(createCommentCommand)
        }

        val getCommentsCommand = GetCommentsCommand(
            postId = lostPost.id,
            PostType.LOST,
            Sort.unsorted()
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
    @DisplayName("커뮤니티 코멘트 조회 - 비밀댓글")
    fun 커뮤니티_코멘트_조회_비밀댓글() {
        // given
        val content = "content"

        val createParentCommentCommand = CreateCommentCommand(
            postId = communityPost.id,
            postType = PostType.COMMUNITY,
            upperCommentId = null,
            content = content,
            visibility = CommentVisibility.PRIVATE
        )
        val createChildCommentCommand = CreateCommentCommand(
            postId = communityPost.id,
            postType = PostType.COMMUNITY,
            upperCommentId = null,
            content = content,
            visibility = CommentVisibility.PRIVATE
        )

        loginWithMemberId(memberIds[1])
        commentUseCase.createComment(createParentCommentCommand)
        loginWithMemberId(memberIds[0])
        commentUseCase.createComment(createChildCommentCommand)

        val getCommentsCommand = GetCommentsCommand(
            postId = communityPost.id,
            PostType.COMMUNITY,
            Sort.unsorted()
        )

        // when
        loginWithMemberId(memberIds[0])
        val postWriterResult = commentUseCase.getComments(getCommentsCommand)
        loginWithMemberId(memberIds[1])
        val commentWriterResult = commentUseCase.getComments(getCommentsCommand)
        loginWithMemberId(memberIds[2])
        val anotherMemberResult = commentUseCase.getComments(getCommentsCommand)

        // then
        assertThat(postWriterResult.comments[0].parentComment.isPrivate).isTrue()
        assertThat(postWriterResult.comments[0].parentComment.content).isEqualTo(content)
        assertThat(commentWriterResult.comments[0].parentComment.isPrivate).isTrue()
        assertThat(commentWriterResult.comments[0].parentComment.content).isEqualTo(content)
        assertThat(anotherMemberResult.comments[0].parentComment.isPrivate).isTrue()
        assertThat(anotherMemberResult.comments[0].parentComment.content).isEmpty()
    }

    @Test
    @DisplayName("유실물 코멘트 조회 - 비밀댓글")
    fun 유실물_코멘트_조회_비밀댓글() {
        // given
        val content = "content"

        val createParentCommentCommand = CreateCommentCommand(
            postId = lostPost.id,
            postType = PostType.LOST,
            upperCommentId = null,
            content = content,
            visibility = CommentVisibility.PRIVATE
        )
        val createChildCommentCommand = CreateCommentCommand(
            postId = lostPost.id,
            postType = PostType.LOST,
            upperCommentId = null,
            content = content,
            visibility = CommentVisibility.PRIVATE
        )

        loginWithMemberId(memberIds[1])
        commentUseCase.createComment(createParentCommentCommand)
        loginWithMemberId(memberIds[0])
        commentUseCase.createComment(createChildCommentCommand)

        val getCommentsCommand = GetCommentsCommand(
            postId = lostPost.id,
            PostType.LOST,
            Sort.unsorted()
        )

        // when
        loginWithMemberId(memberIds[0])
        val postWriterResult = commentUseCase.getComments(getCommentsCommand)
        loginWithMemberId(memberIds[1])
        val commentWriterResult = commentUseCase.getComments(getCommentsCommand)
        loginWithMemberId(memberIds[2])
        val anotherMemberResult = commentUseCase.getComments(getCommentsCommand)

        // then
        assertThat(postWriterResult.comments[0].parentComment.isPrivate).isTrue()
        assertThat(postWriterResult.comments[0].parentComment.content).isEqualTo(content)
        assertThat(commentWriterResult.comments[0].parentComment.isPrivate).isTrue()
        assertThat(commentWriterResult.comments[0].parentComment.content).isEqualTo(content)
        assertThat(anotherMemberResult.comments[0].parentComment.isPrivate).isTrue()
        assertThat(anotherMemberResult.comments[0].parentComment.content).isEmpty()
    }


    @Test
    @DisplayName("자식 코멘트 조회")
    fun 자식_코멘트_조회() {
        // given
        val createCommentCommand = CreateCommentCommand(
            postId = communityPost.id,
            postType = PostType.COMMUNITY,
            upperCommentId = null,
            content = "내용",
            visibility = CommentVisibility.PUBLIC
        )
        val (upper_comment_id, _, _) = commentUseCase.createComment(createCommentCommand)

        val createChildCommentCommand = CreateCommentCommand(
            postId = communityPost.id,
            postType = PostType.COMMUNITY,
            upperCommentId = upper_comment_id,
            content = "내용",
            visibility = CommentVisibility.PUBLIC
        )
        for (i in 1..4) {
            commentUseCase.createComment(createChildCommentCommand)
        }

        val getCommentsCommand = GetCommentsCommand(
            postId = communityPost.id,
            PostType.COMMUNITY,
            Sort.unsorted()
        )

        // when
        val result = commentUseCase.getComments(getCommentsCommand)

        // then
        assertThat(result.comments.size).isEqualTo(1)
        assertThat(result.comments[0].parentComment.id).isEqualTo(upper_comment_id)
        assertThat(result.comments[0].childComments).hasSize(4)
    }

    @Test
    @DisplayName("코멘트 좋아요 수 조회")
    fun 코멘트_좋아요_수_조회() {
        // given
        for (i in 1..10) {
            val createCommentCommand = CreateCommentCommand(
                postId = communityPost.id,
                postType = PostType.COMMUNITY,
                upperCommentId = null,
                content = "내용${i}",
                visibility = CommentVisibility.PUBLIC
            )
            val createComment = commentUseCase.createComment(createCommentCommand)
            commentLikeUseCase.like(createComment.id)
        }

        val getCommentsCommand = GetCommentsCommand(
            postId = communityPost.id,
            PostType.COMMUNITY,
            Sort.unsorted()
        )

        // when
        val result = commentUseCase.getComments(getCommentsCommand)

        // then
        assertThat(result.comments).hasSize(10)
        for (i: Int in 0..9) {
            assertThat(result.comments[i].parentComment.likeCnt).isEqualTo(1L)
        }
    }

    @Test
    @DisplayName("코멘트 좋아요 수 정렬")
    fun 코멘트_좋아요_수_정렬() {
        // given
        for (i in 1..10) {
            val createCommentCommand = CreateCommentCommand(
                postId = communityPost.id,
                postType = PostType.COMMUNITY,
                upperCommentId = null,
                content = "내용${i}",
                visibility = CommentVisibility.PUBLIC
            )
            val createComment = commentUseCase.createComment(createCommentCommand)
            if (i > 5) {
                commentLikeUseCase.like(createComment.id)
            }
        }

        val getCommentsCommand = GetCommentsCommand(
            postId = communityPost.id,
            PostType.COMMUNITY,
            Sort.by("likes").descending()
        )

        // when
        val result = commentUseCase.getComments(getCommentsCommand)

        // then
        assertThat(result.comments).hasSize(10)
        for (i: Int in 0..9) {
            if (i < 5) {
                assertThat(result.comments[i].parentComment.likeCnt).isEqualTo(1L)
            } else {
                assertThat(result.comments[i].parentComment.likeCnt).isEqualTo(0L)
            }
        }
    }

}
