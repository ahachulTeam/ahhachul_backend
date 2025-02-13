package backend.team.ahachul_backend.api.community.application.service

import backend.team.ahachul_backend.api.community.adapter.web.out.CommunityPostHashTagRepository
import backend.team.ahachul_backend.api.community.adapter.web.out.CommunityPostRepository
import backend.team.ahachul_backend.api.community.application.command.`in`.*
import backend.team.ahachul_backend.api.community.application.port.`in`.CommunityPostUseCase
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
import backend.team.ahachul_backend.common.exception.CommonException
import backend.team.ahachul_backend.common.persistence.HashTagRepository
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
import org.springframework.data.domain.Sort
import java.time.LocalDateTime

class CommunityPostServiceTest(
    @Autowired val communityPostRepository: CommunityPostRepository,
    @Autowired val communityPostUseCase: CommunityPostUseCase,

    @Autowired val communityPostHashTagRepository: CommunityPostHashTagRepository,
    @Autowired val hashTagRepository: HashTagRepository,
    @Autowired val memberRepository: MemberRepository,
    @Autowired val subwayLineRepository: SubwayLineRepository,
): CommonServiceTestConfig() {

    var member: MemberEntity? = null
    private lateinit var subwayLine: SubwayLineEntity

    @BeforeEach
    fun setup() {
        member = memberRepository.save(
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
        member!!.id.let { RequestUtils.setAttribute("memberId", it) }
        subwayLine = subwayLineRepository.save(SubwayLineEntity(name = "1호선", regionType = RegionType.METROPOLITAN))
    }

    @Test
    @DisplayName("커뮤니티 게시글 작성")
    fun createCommunityPost() {
        // given
        val command = CreateCommunityPostCommand(
            title = "제목",
            content = "내용",
            categoryType = CommunityCategoryType.FREE,
            subwayLineId = subwayLine.id,
            hashTags = arrayListOf("여행", "취미")
        )

        // when
        val result = communityPostUseCase.createCommunityPost(command)

        // then
        assertThat(result.id).isNotNull()
        assertThat(result.title).isEqualTo("제목")
        assertThat(result.content).isEqualTo("내용")
        assertThat(result.categoryType).isEqualTo(CommunityCategoryType.FREE)
        assertThat(result.region).isEqualTo(RegionType.METROPOLITAN)

        val communityPost = communityPostRepository.findById(result.id).get()

        assertThat(communityPost.member!!.id).isEqualTo(member!!.id)

        assertThat(hashTagRepository.findByName("여행")).isNotNull
        assertThat(hashTagRepository.findByName("취미")).isNotNull

        assertThat(communityPostHashTagRepository.findAll()).hasSize(2)
    }

    @Test
    @DisplayName("커뮤니티 게시글 수정")
    fun updateCommunityPost() {
        // given
        val createCommand = CreateCommunityPostCommand(
            title = "제목",
            content = "내용",
            categoryType = CommunityCategoryType.FREE,
            subwayLineId = subwayLine.id
        )
        val (postId, _, _, _, _) = communityPostUseCase.createCommunityPost(createCommand)

        val updateCommand = UpdateCommunityPostCommand(
            id = postId,
            "수정된 제목",
            "수정된 내용",
            CommunityCategoryType.ISSUE
        )

        // when
        val result = communityPostUseCase.updateCommunityPost(updateCommand)

        // then
        assertThat(result.id).isEqualTo(postId)
        assertThat(result.title).isEqualTo("수정된 제목")
        assertThat(result.content).isEqualTo("수정된 내용")
        assertThat(result.categoryType).isEqualTo(CommunityCategoryType.ISSUE)
    }

    @Test
    @DisplayName("커뮤니티 게시글 수정 - 권한이 없는 경우")
    fun updateCommunityPostWithNotAuth() {
        // given
        val createCommand = CreateCommunityPostCommand(
            title = "제목",
            content = "내용",
            categoryType = CommunityCategoryType.FREE,
            subwayLineId = subwayLine.id
        )
        val (postId, _, _, _, _) = communityPostUseCase.createCommunityPost(createCommand)

        RequestUtils.setAttribute("memberId", 2)

        val updateCommand = UpdateCommunityPostCommand(
            id = postId,
            "수정된 제목",
            "수정된 내용",
            CommunityCategoryType.ISSUE
        )

        // when, then
        assertThatThrownBy {
            communityPostUseCase.updateCommunityPost(updateCommand)
        }
            .isExactlyInstanceOf(CommonException::class.java)
            .hasMessage("권한이 없습니다.")
    }

    @Test
    @DisplayName("커뮤니티 게시글 삭제")
    fun 커뮤니티_게시글_삭제() {
        // given
        val createCommand = CreateCommunityPostCommand(
            title = "제목",
            content = "내용",
            categoryType = CommunityCategoryType.FREE,
            subwayLineId = subwayLine.id
        )
        val (postId, _, _, _, _) = communityPostUseCase.createCommunityPost(createCommand)

        val deleteCommand = DeleteCommunityPostCommand(postId)

        // when, then
        val result = communityPostRepository.findById(postId).get()

        assertThat(result.status).isEqualTo(CommunityPostType.CREATED)
        communityPostUseCase.deleteCommunityPost(deleteCommand)
        assertThat(result.status).isEqualTo(CommunityPostType.DELETED)
    }

    @Test
    @DisplayName("커뮤니티 게시글 단 건 조회")
    fun 커뮤니티_게시글_단건_조회() {
        // given
        val createCommand = CreateCommunityPostCommand(
            title = "제목",
            content = "내용",
            categoryType = CommunityCategoryType.FREE,
            subwayLineId = subwayLine.id,
            hashTags = arrayListOf("여행", "취미")
        )
        val (postId, _, _, _, _) = communityPostUseCase.createCommunityPost(createCommand)

        val getCommunityPostCommand = GetCommunityPostCommand(
            id = postId
        )

        // when
        val result = communityPostUseCase.getCommunityPost(getCommunityPostCommand)

        // then
        assertThat(result.id).isEqualTo(postId)
        assertThat(result.title).isEqualTo(result.title)
        assertThat(result.content).isEqualTo(result.content)
        assertThat(result.categoryType).isEqualTo(CommunityCategoryType.FREE)
        assertThat(result.regionType).isEqualTo(RegionType.METROPOLITAN)
        assertThat(result.writer).isEqualTo(member?.nickname)
        assertThat(result.hashTags).containsExactly("여행", "취미")
    }

    @Test
    @DisplayName("커뮤니티 삭제된 게시글 조회시 예외 발생")
    fun 커뮤니티_게시글_삭제_예외_발생() {
        // given
        val createCommand = CreateCommunityPostCommand(
            title = "제목",
            content = "내용",
            categoryType = CommunityCategoryType.FREE,
            subwayLineId = subwayLine.id
        )

        val (postId, _, _, _, _) = communityPostUseCase.createCommunityPost(createCommand)
        communityPostUseCase.deleteCommunityPost(DeleteCommunityPostCommand(postId))

        // when, then
        assertThatThrownBy {
            communityPostUseCase.getCommunityPost(
                GetCommunityPostCommand(
                    id = postId
                )
            )
        }
            .isExactlyInstanceOf(CommonException::class.java)
            .hasMessage(ResponseCode.POST_NOT_FOUND.message)
    }

    @Test
    @DisplayName("커뮤니티 조회수 증가")
    fun 커뮤니티_조회수_증가() {
        // given
        val createCommand = CreateCommunityPostCommand(
            title = "제목",
            content = "내용",
            categoryType = CommunityCategoryType.FREE,
            subwayLineId = subwayLine.id
        )
        val (postId, _, _, _, _) = communityPostUseCase.createCommunityPost(createCommand)

        val getCommunityPostCommand = GetCommunityPostCommand(
            id = postId
        )

        // when, then
        var result = communityPostUseCase.getCommunityPost(getCommunityPostCommand)
        assertThat(result.viewCnt).isEqualTo(1)

        result = communityPostUseCase.getCommunityPost(getCommunityPostCommand)
        assertThat(result.viewCnt).isEqualTo(2)

        communityPostUseCase.getCommunityPost(getCommunityPostCommand)
        result = communityPostUseCase.getCommunityPost(getCommunityPostCommand)
        assertThat(result.viewCnt).isEqualTo(4)
    }

    @Test
    @DisplayName("커뮤니티 게시글 내용 조회")
    fun 커뮤니티_게시글_내용_조회() {
        // given
        val createCommand = CreateCommunityPostCommand(
            title = "지하철 제목",
            content = "지하철 내용",
            categoryType = CommunityCategoryType.FREE,
            subwayLineId = subwayLine.id
        )
        val createCommand2 = CreateCommunityPostCommand(
            title = "지하철 안와요",
            content = "지하철이 왜 안와",
            categoryType = CommunityCategoryType.ISSUE,
            subwayLineId = subwayLine.id
        )
        val communityPost = communityPostUseCase.createCommunityPost(createCommand)
        val communityPost2 = communityPostUseCase.createCommunityPost(createCommand2)

        val verifyNameCommand = SearchCommunityPostCommand(
            categoryType = null,
            subwayLineId = null,
            content = "제",
            hashTag = null,
            writer = null,
            sort = Sort.unsorted(),
            pageToken = null,
            pageSize = 2
        )
        val verifyNameCommand2 = SearchCommunityPostCommand(
            categoryType = null,
            subwayLineId = null,
            content = "지하철",
            hashTag = null,
            writer = null,
            sort = Sort.unsorted(),
            pageToken = null,
            pageSize = 1
        )
        val verifyNameCommand3 = SearchCommunityPostCommand(
            categoryType = null,
            subwayLineId = null,
            content = "지하철",
            hashTag = null,
            writer = null,
            sort = Sort.unsorted(),
            pageToken = null,
            pageSize = 2
        )
        val verifyOrderCommand = SearchCommunityPostCommand(
            categoryType = null,
            subwayLineId = null,
            content = null,
            hashTag = null,
            writer = null,
            sort = Sort.unsorted(),
            pageToken = null,
            pageSize = 2
        )

        // when, then
        var result = communityPostUseCase.searchCommunityPosts(verifyNameCommand)
        assertThat(result.hasNext).isFalse()
        assertThat(result.data).hasSize(1)
        assertThat(result.data.first().id).isEqualTo(communityPost.id)

        result = communityPostUseCase.searchCommunityPosts(verifyNameCommand2)
        assertThat(result.hasNext).isTrue()
        assertThat(result.data).hasSize(1)
        assertThat(result.data.first().id).isEqualTo(communityPost2.id)

        result = communityPostUseCase.searchCommunityPosts(verifyNameCommand3)
        assertThat(result.hasNext).isFalse()
        assertThat(result.data).hasSize(2)

        result = communityPostUseCase.searchCommunityPosts(verifyOrderCommand)
        assertThat(result.data).hasSize(2)
        assertThat(result.data.map { it.createdAt })
            .isEqualTo(result.data.map { it.createdAt }.sortedDescending())

    }

     @Test
     @DisplayName("커뮤니티 게시글 카테고리 조회")
     fun 커뮤니티_게시글_카테고리_조회() {
         // given
         val createCommand = CreateCommunityPostCommand(
             title = "지하철 제목",
             content = "지하철 내용",
             categoryType = CommunityCategoryType.FREE,
             subwayLineId = subwayLine.id
         )
         val createCommand2 = CreateCommunityPostCommand(
             title = "지하철 안와요",
             content = "지하철이 왜 안와",
             categoryType = CommunityCategoryType.ISSUE,
             subwayLineId = subwayLine.id
         )
         val communityPost = communityPostUseCase.createCommunityPost(createCommand)
         val communityPost2 = communityPostUseCase.createCommunityPost(createCommand2)


         val verifyCategoryCommand = SearchCommunityPostCommand(
             categoryType = CommunityCategoryType.FREE,
             subwayLineId = null,
             content = null,
             hashTag = null,
             writer = null,
             sort = Sort.unsorted(),
             pageToken = null,
             pageSize = 2
         )
         val verifyCategoryCommand2 = SearchCommunityPostCommand(
             categoryType = CommunityCategoryType.ISSUE,
             subwayLineId = null,
             content = null,
             hashTag = null,
             writer = null,
             sort = Sort.unsorted(),
             pageToken = null,
             pageSize = 2
         )

         // when, then
         var result = communityPostUseCase.searchCommunityPosts(verifyCategoryCommand)
         assertThat(result.data).hasSize(1)
         assertThat(result.data.first().id).isEqualTo(communityPost.id)

         result = communityPostUseCase.searchCommunityPosts(verifyCategoryCommand2)
         assertThat(result.data).hasSize(1)
         assertThat(result.data.first().id).isEqualTo(communityPost2.id)
     }

    @Test
    @DisplayName("커뮤니티 게시글 해시태그 조회")
    fun 커뮤니티_게시글_해시태그_조회() {
        // given
        val createCommand = CreateCommunityPostCommand(
            title = "제목",
            content = "내용",
            categoryType = CommunityCategoryType.FREE,
            subwayLineId = subwayLine.id,
            hashTags = arrayListOf("여행", "취미")
        )
        val createCommand2 = CreateCommunityPostCommand(
            title = "제목",
            content = "내용",
            categoryType = CommunityCategoryType.FREE,
            subwayLineId = subwayLine.id,
            hashTags = arrayListOf("바이킹", "취미")
        )
        val communityPost = communityPostUseCase.createCommunityPost(createCommand)
        communityPostUseCase.createCommunityPost(createCommand2)

        val verifyHashTagCommand = SearchCommunityPostCommand(
            categoryType = null,
            subwayLineId = null,
            content = null,
            hashTag = "여행",
            writer = null,
            sort = Sort.unsorted(),
            pageToken = null,
            pageSize = 2
        )
        val verifyHashTagCommand2 = SearchCommunityPostCommand(
            categoryType = null,
            subwayLineId = null,
            content = null,
            hashTag = "취미",
            writer = null,
            sort = Sort.unsorted(),
            pageToken = null,
            pageSize = 2
        )

        // when, then
        var result = communityPostUseCase.searchCommunityPosts(verifyHashTagCommand)
        assertThat(result.data).hasSize(1)
        assertThat(result.data.first().id).isEqualTo(communityPost.id)

        result = communityPostUseCase.searchCommunityPosts(verifyHashTagCommand2)
        assertThat(result.data).hasSize(2)
    }

    @Test
    @DisplayName("커뮤니티 게시글 작성자 조회")
    fun 커뮤니티_게시글_작성자_조회() {
        // given
        val createCommand = CreateCommunityPostCommand(
            title = "제목",
            content = "내용",
            categoryType = CommunityCategoryType.FREE,
            subwayLineId = subwayLine.id
        )
        val createCommand2 = CreateCommunityPostCommand(
            title = "제목",
            content = "내용",
            categoryType = CommunityCategoryType.FREE,
            subwayLineId = subwayLine.id
        )

        communityPostUseCase.createCommunityPost(createCommand)
        communityPostUseCase.createCommunityPost(createCommand2)

        val verifyWriterCommand = SearchCommunityPostCommand(
            categoryType = null,
            subwayLineId = null,
            content = null,
            hashTag = null,
            writer = "nickname",
            sort = Sort.unsorted(),
            pageToken = null,
            pageSize = 2
        )

        // when, then
        val result = communityPostUseCase.searchCommunityPosts(verifyWriterCommand)
        assertThat(result.data).hasSize(2)
    }

    @Test
    @DisplayName("커뮤니티_게시글_조회_페이징")
    fun 커뮤니티_게시글_조회_페이징() {
        // given
        for(i: Int in 1.. 5) {
            val createCommand = CreateCommunityPostCommand(
                title = "제목$i",
                content = "내용$i",
                categoryType = CommunityCategoryType.FREE,
                subwayLineId = subwayLine.id
            )

            communityPostUseCase.createCommunityPost(createCommand)
        }

        // when
        val searchCommand1 = SearchCommunityPostCommand(
            categoryType = null,
            subwayLineId = null,
            content = null,
            hashTag = null,
            writer = null,
            sort = Sort.unsorted(),
            pageToken = null,
            pageSize = 3
        )

        val response1 = communityPostUseCase.searchCommunityPosts(searchCommand1)

        val searchCommand2 = SearchCommunityPostCommand(
            categoryType = null,
            subwayLineId = null,
            content = null,
            hashTag = null,
            writer = null,
            sort = Sort.unsorted(),
            pageToken = response1.pageToken,
            pageSize = 3
        )

        val response2 = communityPostUseCase.searchCommunityPosts(searchCommand2)

        // then
        assertThat(response1.hasNext).isEqualTo(true)
        assertThat(response1.data.size).isEqualTo(3)
        assertThat(response1.data)
            .extracting("content")
            .usingRecursiveComparison()
            .isEqualTo((5 downTo 3).map { "내용$it" })

        assertThat(response2.hasNext).isEqualTo(false)
        assertThat(response2.data.size).isEqualTo(2)
        assertThat(response2.data)
            .extracting("content")
            .usingRecursiveComparison()
            .isEqualTo((2 downTo 1).map { "내용$it" })
    }

    @Test
    @DisplayName("커뮤니티 인기 게시글 조회")
    fun 커뮤니티_인기_게시글_조회() {
        // given
        for (i: Int in 1..10) {
            communityPostUseCase.createCommunityPost(
                CreateCommunityPostCommand(
                    title = "지하철 제목$i",
                    content = "지하철 내용$i",
                    categoryType = CommunityCategoryType.FREE,
                    subwayLineId = subwayLine.id
                )
            )
        }

        // when
        val findCommunityPost = communityPostRepository.findAll().first()

        findCommunityPost.hotPostYn = YNType.Y
        findCommunityPost.hotPostSelectedDate = LocalDateTime.now()

        val result = communityPostUseCase.searchCommunityHotPosts(
            SearchCommunityHotPostCommand(
                subwayLineId = null,
                content = null,
                hashTag = null,
                writer = null,
                sort = Sort.unsorted(),
                pageToken = null,
                pageSize = 10
            )
        )

        // then
        assertThat(result.data).hasSize(1)
        assertThat(result.data.first().id).isEqualTo(findCommunityPost.id)
    }
}
