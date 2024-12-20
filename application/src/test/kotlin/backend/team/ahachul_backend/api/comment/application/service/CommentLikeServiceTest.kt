package backend.team.ahachul_backend.api.comment.application.service

import backend.team.ahachul_backend.api.comment.adapter.web.out.CommentLikeRepository
import backend.team.ahachul_backend.api.comment.adapter.web.out.CommentRepository
import backend.team.ahachul_backend.api.comment.application.port.`in`.CommentLikeUseCase
import backend.team.ahachul_backend.api.comment.domain.entity.CommentEntity
import backend.team.ahachul_backend.api.comment.domain.model.CommentVisibility
import backend.team.ahachul_backend.api.community.adapter.web.out.CommunityPostRepository
import backend.team.ahachul_backend.api.community.domain.entity.CommunityPostEntity
import backend.team.ahachul_backend.api.community.domain.model.CommunityCategoryType
import backend.team.ahachul_backend.api.member.adapter.web.out.MemberRepository
import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity
import backend.team.ahachul_backend.api.member.domain.model.GenderType
import backend.team.ahachul_backend.api.member.domain.model.MemberStatusType
import backend.team.ahachul_backend.api.member.domain.model.ProviderType
import backend.team.ahachul_backend.common.domain.entity.SubwayLineEntity
import backend.team.ahachul_backend.common.domain.model.RegionType
import backend.team.ahachul_backend.common.domain.model.YNType
import backend.team.ahachul_backend.common.exception.CommonException
import backend.team.ahachul_backend.common.persistence.SubwayLineRepository
import backend.team.ahachul_backend.common.response.ResponseCode
import backend.team.ahachul_backend.common.utils.RequestUtils
import backend.team.ahachul_backend.config.controller.CommonServiceTestConfig
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class CommentLikeServiceTest(
    @Autowired val commentLikeRepository: CommentLikeRepository,
    @Autowired val commentLikeUseCase: CommentLikeUseCase,

    @Autowired val commentRepository: CommentRepository,
    @Autowired val memberRepository: MemberRepository,
    @Autowired val subwayLineRepository: SubwayLineRepository,
    @Autowired val communityPostRepository: CommunityPostRepository,
) : CommonServiceTestConfig() {

    private lateinit var subwayLine: SubwayLineEntity
    private lateinit var communityPost: CommunityPostEntity
    private lateinit var communityComment: CommentEntity

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

        communityPost = communityPostRepository.save(
            CommunityPostEntity(
                title = "제목",
                content = "내용",
                categoryType = CommunityCategoryType.FREE,
                subwayLineEntity = subwayLine
            )
        )

        communityComment = commentRepository.save(
            CommentEntity(
                content = "커뮤니티 댓글 내용",
                visibility = CommentVisibility.PUBLIC,
                upperComment = null,
                communityPost = communityPost,
                lostPost = null,
                member = member
            )
        )
    }

    @Test
    @DisplayName("댓글 좋아요")
    fun 댓글_좋아요() {
        //given
        val commentId = communityComment.id

        //when
        commentLikeUseCase.like(commentId)

        //then
        val commentLike = commentLikeRepository.findByCommentIdAndMemberId(commentId, communityComment.member.id)
        assertThat(commentLike?.likeYn).isEqualTo(YNType.Y)
    }

    @Test
    @DisplayName("이미 댓글 좋아요가 있을 경우 예외")
    fun 이미_댓글_좋아요가_있을_경우_예외() {
        //given
        val commentId = communityComment.id
        commentLikeUseCase.like(commentId)

        //when & then
        assertThatThrownBy {
            commentLikeUseCase.like(commentId)
        }
            .isExactlyInstanceOf(CommonException::class.java)
            .hasMessage(ResponseCode.BAD_REQUEST.message)
    }

    @Test
    @DisplayName("댓글 좋아요 취소")
    fun 댓글_좋아요_취소() {
        //given
        val commentId = communityComment.id
        commentLikeUseCase.like(commentId)

        //when
        commentLikeUseCase.notLike(commentId)

        //then
        val commentLike = commentLikeRepository.findById(commentId).orElse(null)
        assertThat(commentLike).isNull()
    }

    @Test
    @DisplayName("댓글 좋아요 취소 시 좋아요 댓글 없을 시 예외")
    fun 댓글_좋아요_취소_시_좋아요_댓글_없을_시_예외() {
        //given
        val commentId = communityComment.id

        //when & then
        assertThatThrownBy {
            commentLikeUseCase.notLike(commentId)
        }
            .isExactlyInstanceOf(CommonException::class.java)
            .hasMessage(ResponseCode.BAD_REQUEST.message)
    }
}