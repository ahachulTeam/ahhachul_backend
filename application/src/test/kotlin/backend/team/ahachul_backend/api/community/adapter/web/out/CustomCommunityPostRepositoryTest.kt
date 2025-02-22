package backend.team.ahachul_backend.api.community.adapter.web.out

import backend.team.ahachul_backend.api.community.domain.entity.CommunityPostEntity
import backend.team.ahachul_backend.api.community.domain.entity.CommunityPostLikeEntity
import backend.team.ahachul_backend.api.community.domain.model.CommunityCategoryType
import backend.team.ahachul_backend.api.community.domain.model.CommunityPostType
import backend.team.ahachul_backend.api.member.adapter.web.out.MemberRepository
import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity
import backend.team.ahachul_backend.api.member.domain.model.GenderType
import backend.team.ahachul_backend.api.member.domain.model.MemberStatusType
import backend.team.ahachul_backend.api.member.domain.model.ProviderType
import backend.team.ahachul_backend.common.domain.entity.SubwayLineEntity
import backend.team.ahachul_backend.common.domain.model.RegionType
import backend.team.ahachul_backend.common.domain.model.YNType
import backend.team.ahachul_backend.common.persistence.SubwayLineRepository
import backend.team.ahachul_backend.config.controller.CommonServiceTestConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class CustomCommunityPostRepositoryTest (
    @Autowired val customCommunityPostRepository: CustomCommunityPostRepository,
    @Autowired val memberRepository: MemberRepository,
    @Autowired val communityPostRepository: CommunityPostRepository,
    @Autowired val subwayLineRepository: SubwayLineRepository,
    @Autowired val communityPostLikeRepository: CommunityPostLikeRepository,
): CommonServiceTestConfig() {

    @Test
    @DisplayName("게시글 조회. 좋아요 수, 좋아요 가능 한지 등 포함")
    fun 게시글_조회() {
        // given
        val member = memberRepository.save(
            MemberEntity(
                providerUserId = "providerUserId",
                provider = ProviderType.GOOGLE,
                status = MemberStatusType.ACTIVE,
                nickname = "닉네임",
                email = "이메일",
                gender = GenderType.FEMALE,
                ageRange = "20"
            )
        )
        val subwayLine = subwayLineRepository.save(
            SubwayLineEntity(
                name = "1호선",
                regionType = RegionType.METROPOLITAN
            )
        )
        val communityPost = communityPostRepository.save(
            CommunityPostEntity(
                title = "제목",
                content = "내용",
                categoryType = CommunityCategoryType.ISSUE,
                views = 0,
                status = CommunityPostType.CREATED,
                regionType = RegionType.METROPOLITAN,
                subwayLineEntity = subwayLine,
                member = member
            )
        )

        for (i in 1..3) {
            communityPostLikeRepository.save(
                CommunityPostLikeEntity(
                    likeYn = YNType.Y,
                    communityPost = communityPost,
                    member = member
                )
            )
        }
        for (i in 1..2) {
            communityPostLikeRepository.save(
                CommunityPostLikeEntity(
                    likeYn = YNType.N,
                    communityPost = communityPost,
                    member = member
                )
            )
        }

        // when, then
        val hasAuth = customCommunityPostRepository.getByCustom(communityPost.id, memberId = member.id.toString())!!
        assertThat(hasAuth.id).isEqualTo(communityPost.id)
        assertThat(hasAuth.title).isEqualTo(communityPost.title)
        assertThat(hasAuth.content).isEqualTo(communityPost.content)
        assertThat(hasAuth.categoryType).isEqualTo(communityPost.categoryType)
        assertThat(hasAuth.likeCnt).isEqualTo(3)
        assertThat(hasAuth.hateCnt).isEqualTo(2)
        assertThat(hasAuth.likeYn).isEqualTo(true)
        assertThat(hasAuth.hateYn).isEqualTo(true)
        assertThat(hasAuth.regionType).isEqualTo(communityPost.regionType)
        assertThat(hasAuth.createdBy).isEqualTo(communityPost.createdBy)
        assertThat(hasAuth.writer).isEqualTo(member.nickname)

        val notHasAuth = customCommunityPostRepository.getByCustom(communityPost.id, memberId = null)!!
        assertThat(notHasAuth.id).isEqualTo(communityPost.id)
        assertThat(notHasAuth.likeYn).isEqualTo(false)
        assertThat(notHasAuth.hateYn).isEqualTo(false)
    }
}
