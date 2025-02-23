package backend.team.ahachul_backend.api.lost.application.service

import backend.team.ahachul_backend.api.lost.adapter.web.out.CategoryRepository
import backend.team.ahachul_backend.api.lost.adapter.web.out.LostPostRepository
import backend.team.ahachul_backend.api.lost.application.port.`in`.LostPostUseCase
import backend.team.ahachul_backend.api.lost.application.service.command.`in`.CreateLostPostCommand
import backend.team.ahachul_backend.api.lost.application.service.command.`in`.SearchLostPostCommand
import backend.team.ahachul_backend.api.lost.application.service.command.`in`.UpdateLostPostCommand
import backend.team.ahachul_backend.api.lost.application.service.command.`in`.UpdateLostPostStatusCommand
import backend.team.ahachul_backend.api.lost.domain.entity.CategoryEntity
import backend.team.ahachul_backend.api.lost.domain.entity.LostPostEntity
import backend.team.ahachul_backend.api.lost.domain.model.*
import backend.team.ahachul_backend.api.member.adapter.web.out.MemberRepository
import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity
import backend.team.ahachul_backend.api.member.domain.model.GenderType
import backend.team.ahachul_backend.api.member.domain.model.MemberStatusType
import backend.team.ahachul_backend.api.member.domain.model.ProviderType
import backend.team.ahachul_backend.common.domain.entity.SubwayLineEntity
import backend.team.ahachul_backend.common.exception.CommonException
import backend.team.ahachul_backend.common.domain.model.RegionType
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


class LostPostServiceTest(
    @Autowired val lostPostUseCase: LostPostUseCase,
    @Autowired val lostPostRepository: LostPostRepository,
    @Autowired val memberRepository: MemberRepository,
    @Autowired val subwayLineRepository: SubwayLineRepository,
    @Autowired val categoryRepository: CategoryRepository
): CommonServiceTestConfig() {

    lateinit var member: MemberEntity
    lateinit var subwayLine: SubwayLineEntity
    lateinit var category: CategoryEntity

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
        member.id.let { RequestUtils.setAttribute(RequestUtils.Attribute.MEMBER_ID, it)}
        subwayLine = createSubwayLine("1호선")
        category = createCategory("휴대폰")
    }

    @Test
    @DisplayName("유실물 상세 조회 테스트")
    fun getLostPost() {
        // given
        val createCommand = createLostPostCommand(subwayLine.id, "내용", "휴대폰")
        val entity = lostPostUseCase.createLostPost(createCommand)

        // when
        val response = lostPostUseCase.getLostPost(entity.id)

        // then
        assertThat(response.title).isEqualTo("지갑 주인 찾아요")
        assertThat(response.content).isEqualTo("내용")
        assertThat(response.writer).isEqualTo("nickname")
        assertThat(response.subwayLineId).isEqualTo(subwayLine.id)
        assertThat(response.status).isEqualTo(LostStatus.PROGRESS)
        assertThat(response.lostType).isEqualTo(LostType.ACQUIRE)
    }

    @Test
    @DisplayName("유실물 전체 조회 페이징 테스트 - 필터링 X")
    fun searchLostPostPaging() {
        // given
        for(i: Int in 1.. 5) {
            val createCommand = createLostPostCommand(subwayLine.id, "유실물$i", "휴대폰")
            lostPostUseCase.createLostPost(createCommand)
        }

        // when
        val searchCommand1 = createSearchLostPostCommand(null, subwayLine.id, null)
        val response1 = lostPostUseCase.searchLostPosts(searchCommand1)

        val searchCommand2 = createSearchLostPostCommand(response1.pageToken, subwayLine.id, null)
        val response2 = lostPostUseCase.searchLostPosts(searchCommand2)

        // then
        assertThat(response1.hasNext).isEqualTo(true)
        assertThat(response1.data.size).isEqualTo(3)
        assertThat(response1.data)
            .extracting("content")
            .usingRecursiveComparison()
            .isEqualTo((5 downTo 3).map { "유실물$it" })

        assertThat(response2.hasNext).isEqualTo(false)
        assertThat(response2.data.size).isEqualTo(2)
        assertThat(response2.data)
            .extracting("content")
            .usingRecursiveComparison()
            .isEqualTo((2 downTo 1).map { "유실물$it" })
    }

    @Test
    @DisplayName("유실물 전체 조회 테스트 - 노선 필터링")
    fun searchLostPostsBySubwayLine() {
        // given
        val subwayLine1 = createSubwayLine("1호선")
        val subwayLine2 = createSubwayLine("2호선")

        for(i: Int in 1.. 5) {
            val createCommand1 = createLostPostCommand(subwayLine1.id, "유실물$i", "휴대폰")
            val createCommand2 = createLostPostCommand(subwayLine2.id, "유실물$i", "휴대폰")

            lostPostUseCase.createLostPost(createCommand1)
            lostPostUseCase.createLostPost(createCommand2)
        }

        val searchCommand = createSearchLostPostCommand(null, subwayLine1.id, null)

        // when
        val response = lostPostUseCase.searchLostPosts(searchCommand)

        // then
        assertThat(response.data.size).isEqualTo(3)
        assertThat(response.data)
            .extracting("subwayLineId")
            .usingRecursiveComparison()
            .isEqualTo((1.. 3).map {subwayLine1.id}.toList())
    }

    @Test
    @DisplayName("유실물 저장 테스트")
    fun createLostPost() {
        //given
        val createCommand = createLostPostCommand(subwayLine.id, "내용", "휴대폰")

        // when
        val response = lostPostUseCase.createLostPost(createCommand)
        assertThat(response.id).isNotNull

        // then
        val entity = lostPostRepository.findById(response.id).get()

        assertThat(entity.title).isEqualTo("지갑 주인 찾아요")
        assertThat(entity.content).isEqualTo("내용")
        assertThat(entity.lostType).isEqualTo(LostType.ACQUIRE)
        assertThat(entity.type).isEqualTo(LostPostType.CREATED)
    }

    @Test
    @DisplayName("유실물 수정 테스트 - 권한이 있는 경우")
    fun updateLostPost() {
        // given
        val createCommand = createLostPostCommand(subwayLine.id, "내용", "휴대폰")
        val entity = lostPostUseCase.createLostPost(createCommand)
        createCategory("지갑")

        val updateCommand = UpdateLostPostCommand(
            id = entity.id,
            title = null,
            content = "수정한 내용",
            subwayLineId = subwayLine.id,
            status = LostStatus.COMPLETE,
            categoryName = "지갑"
        )

        // when
        val response = lostPostUseCase.updateLostPost(updateCommand)

        // then
        assertThat(response.id).isNotNull
        assertThat(response.content).isEqualTo("수정한 내용")
        assertThat(response.subwayLine).isEqualTo(subwayLine.id)
        assertThat(response.categoryName).isEqualTo("지갑")
        assertThat(response.status).isEqualTo(LostStatus.COMPLETE)
    }

    @Test
    @DisplayName("유실물 수정 테스트 - 권한이 없는 경우")
    fun updateLostPostUnAuthorized() {
        // given
        val createCommand = createLostPostCommand(subwayLine.id, "내용", "휴대폰")
        val entity = lostPostUseCase.createLostPost(createCommand)

        val updateCommand = UpdateLostPostCommand(
            id = entity.id,
            title = null,
            content = "수정한 내용",
            subwayLineId = null,
            status = LostStatus.COMPLETE,
            categoryName = null
        )

        // when, then
        RequestUtils.setAttribute(RequestUtils.Attribute.MEMBER_ID, member.id + 1)

        assertThatThrownBy {
            lostPostUseCase.updateLostPost(updateCommand)
        }
            .isExactlyInstanceOf(CommonException::class.java)
            .hasMessage(ResponseCode.INVALID_AUTH.message)
    }

    @Test
    @DisplayName("유실물 상태 수정 테스트")
    fun updateLostPostStatus() {
        // given
        val createCommand = createLostPostCommand(subwayLine.id, "내용", "휴대폰")
        val entity = lostPostUseCase.createLostPost(createCommand)

        val updateCommand = UpdateLostPostStatusCommand(
            id = entity.id,
            status = LostStatus.COMPLETE
        )

        // when
        val response = lostPostUseCase.updateLostPostStatus(updateCommand)

        // then
        assertThat(response.id).isEqualTo(entity.id)
    }

    @Test
    @DisplayName("유실물 상태 수정 Lost112 에러")
    fun updateLostPostStatusError() {
        // given
        val entity = lostPostRepository.save(
            LostPostEntity(
                title = "제목",
                content = "내용",
                subwayLine = subwayLine,
                lostType = LostType.LOST,
                category = category,
                origin = LostOrigin.LOST112
            )
        )

        val updateCommand = UpdateLostPostStatusCommand(
            id = entity.id,
            status = LostStatus.COMPLETE
        )

        // when & then
        assertThatThrownBy {
            lostPostUseCase.updateLostPostStatus(updateCommand)
        }
            .isExactlyInstanceOf(CommonException::class.java)
            .hasMessage(ResponseCode.BAD_REQUEST.message)
    }

    @Test
    @DisplayName("유실물 삭제 테스트 - 권한이 있는 경우")
    fun deleteLostPost() {
        // given
        val createCommand = createLostPostCommand(subwayLine.id, "내용", "휴대폰")
        val entity = lostPostUseCase.createLostPost(createCommand)

        // when
        val response = lostPostUseCase.deleteLostPost(entity.id)

        // then
        assertThat(response.id).isEqualTo(entity.id)
    }

    @Test
    @DisplayName("유실물 삭제 테스트 - 권한이 없는 경우")
    fun deleteLostPostUnAuthorized() {
        // given
        val createCommand = createLostPostCommand(subwayLine.id, "내용", "휴대폰")
        val entity = lostPostUseCase.createLostPost(createCommand)

        // when, then
        RequestUtils.setAttribute(RequestUtils.Attribute.MEMBER_ID, member.id + 1)

        assertThatThrownBy {
            lostPostUseCase.deleteLostPost(entity.id)
        }
            .isExactlyInstanceOf(CommonException::class.java)
            .hasMessage(ResponseCode.INVALID_AUTH.message)
    }

    @Test
    @DisplayName("같은 카테고리의 유실물을 추천해야 한다.")
    fun getSameCategoryRecommendLostPost() {
        // given
        val lostPostIds: MutableList<Long> = mutableListOf()

        for(i: Int in 1.. 8) {
            val createCommand = createLostPostCommand(subwayLine.id, "유실물$i", "휴대폰")
            lostPostIds.add(lostPostUseCase.createLostPost(createCommand).id)
        }

        createCategory("지갑")
        for(i: Int in 1.. 4) {
            val createCommand = createLostPostCommand(subwayLine.id, "유실물$i", "지갑")
            lostPostUseCase.createLostPost(createCommand)
        }

        // when
        val response = lostPostUseCase.getLostPost(lostPostIds[0])
        val recommendPosts = response.recommendPosts
        assertThat(recommendPosts.map { it.id }.toSet().size).isEqualTo(12)
        assertThat(recommendPosts.map { it.id }.containsAll(lostPostIds))
    }


    @Test
    @DisplayName("추천되는 유실물에는 중복이 없어야 한다.")
    fun getRecommendLostPost() {
        // given
        var lostPostId: Long = 0

        for(i: Int in 1.. 8) {
            val createCommand = createLostPostCommand(subwayLine.id, "유실물$i", "휴대폰")
            lostPostId = lostPostUseCase.createLostPost(createCommand).id
        }

        createCategory("지갑")
        for(i: Int in 1.. 3) {
            val createCommand = createLostPostCommand(subwayLine.id, "유실물$i", "지갑")
            lostPostUseCase.createLostPost(createCommand)
        }

        createCategory("컴퓨터")
        for(i: Int in 1.. 2) {
            val createCommand = createLostPostCommand(subwayLine.id, "유실물$i", "컴퓨터")
            lostPostUseCase.createLostPost(createCommand)
        }

        // when
        val response = lostPostUseCase.getLostPost(lostPostId)
        val recommendPosts = response.recommendPosts
        assertThat(recommendPosts.map { it.id }.toSet().size).isEqualTo(12)
    }

    @Test
    @DisplayName("제목이나 내용에 검색 키워드가 포함된 유실물을 반환한다.")
    fun searchLostPostByKeyword() {
        // given
        val categoryName = "휴대폰"
        val createCommand1 = createLostPostCommand(subwayLine.id, "오늘 1호선에서 지갑을 주웠어요", categoryName)
        val createCommand2 = createLostPostCommand(subwayLine.id, "2호선에서 분실물 주웠는데 찾아가세요", categoryName)

        val entity1 = lostPostUseCase.createLostPost(createCommand1)
        val entity2 = lostPostUseCase.createLostPost(createCommand2)
        val searchCommand = createSearchLostPostCommand(null, subwayLine.id, "지갑")

        // when
        val response = lostPostUseCase.searchLostPosts(searchCommand)

        // then
        assertThat(response.data.size).isEqualTo(2)
        assertThat(response.data[0].id).isEqualTo(entity2.id)
        assertThat(response.data[1].id).isEqualTo(entity1.id)
    }
    
    private fun createLostPostCommand(subwayLineId: Long, content: String, categoryName: String): CreateLostPostCommand {
        return CreateLostPostCommand(
            title = "지갑 주인 찾아요",
            content = content,
            subwayLine = subwayLineId,
            lostType = LostType.ACQUIRE,
            categoryName = categoryName,
        )
    }

    private fun createSubwayLine(name: String): SubwayLineEntity {
        return subwayLineRepository.save(
            SubwayLineEntity(
                name = name,
                regionType = RegionType.METROPOLITAN
            )
        )
    }

    private fun createCategory(name: String): CategoryEntity {
        return categoryRepository.save(
            CategoryEntity(
                name = name
            )
        )
    }

    private fun createSearchLostPostCommand(pageToken: String?, subwayLineId:Long, keyword:String?): SearchLostPostCommand {
        return SearchLostPostCommand(
            lostType = LostType.ACQUIRE,
            subwayLineId = subwayLineId,
            keyword = keyword,
            category = "휴대폰",
            pageToken = pageToken,
            pageSize = 3
        )
    }
}
