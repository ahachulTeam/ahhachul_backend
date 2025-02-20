package backend.team.ahachul_backend.api.member.application.service

import backend.team.ahachul_backend.api.common.application.port.out.StationRepository
import backend.team.ahachul_backend.api.common.application.port.out.SubwayLineStationRepository
import backend.team.ahachul_backend.api.common.domain.entity.StationEntity
import backend.team.ahachul_backend.api.common.domain.entity.SubwayLineStationEntity
import backend.team.ahachul_backend.api.member.adapter.web.out.MemberRepository
import backend.team.ahachul_backend.api.member.adapter.web.out.MemberStationRepository
import backend.team.ahachul_backend.api.member.application.command.BookmarkStationCommand
import backend.team.ahachul_backend.api.member.application.command.BookmarkStationCommands
import backend.team.ahachul_backend.api.member.application.command.SearchMemberCommand
import backend.team.ahachul_backend.api.member.application.port.`in`.MemberUseCase
import backend.team.ahachul_backend.api.member.application.port.`in`.command.CheckNicknameCommand
import backend.team.ahachul_backend.api.member.application.port.`in`.command.UpdateMemberCommand
import backend.team.ahachul_backend.api.member.application.port.out.MemberWriter
import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity
import backend.team.ahachul_backend.api.member.domain.entity.MemberStationEntity
import backend.team.ahachul_backend.api.member.domain.model.GenderType
import backend.team.ahachul_backend.api.member.domain.model.MemberStatusType
import backend.team.ahachul_backend.api.member.domain.model.ProviderType
import backend.team.ahachul_backend.common.domain.entity.SubwayLineEntity
import backend.team.ahachul_backend.common.domain.model.RegionType
import backend.team.ahachul_backend.common.exception.BusinessException
import backend.team.ahachul_backend.common.persistence.SubwayLineRepository
import backend.team.ahachul_backend.common.response.ResponseCode
import backend.team.ahachul_backend.common.utils.RequestUtils
import backend.team.ahachul_backend.config.controller.CommonServiceTestConfig
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired

class MemberServiceTest(
    @Autowired val memberWriter: MemberWriter,
    @Autowired val memberUseCase: MemberUseCase,
    @Autowired val memberRepository: MemberRepository,
    @Autowired val stationRepository: StationRepository,
    @Autowired val memberStationRepository: MemberStationRepository,
    @Autowired val subwayLineStationRepository: SubwayLineStationRepository,
    @Autowired val subwayLineRepository: SubwayLineRepository
) : CommonServiceTestConfig() {

    var member: MemberEntity? = null

    @BeforeEach
    fun setUp() {
        for (i in 1..10) {
            memberWriter.save(
                MemberEntity(
                    nickname = "nickname$i",
                    provider = ProviderType.GOOGLE,
                    providerUserId = "providerUserId$i",
                    email = "email$i",
                    gender = GenderType.MALE,
                    ageRange = "20",
                    status = MemberStatusType.ACTIVE
                )
            )
        }

        member = memberRepository.save(MemberEntity(
            nickname = "nickname",
            provider = ProviderType.KAKAO,
            providerUserId = "providerUserId",
            email = "email",
            gender = GenderType.MALE,
            ageRange = "20",
            status = MemberStatusType.ACTIVE
        ))
        member!!.id.let { RequestUtils.setAttribute("memberId", it) }
    }

    @ParameterizedTest
    @ValueSource(strings = ["nickname", "nick", "name", "ckna"])
    fun 회원_닉네임_일치_조회(keyword: String) {
        //when
        val searchMemberCommand = SearchMemberCommand(nickname = keyword)
        val searchMembers = memberUseCase.searchMembers(searchMemberCommand)

        //then
        assertThat(searchMembers.members.size).isEqualTo(11)
    }

    @ParameterizedTest
    @ValueSource(strings = ["ahhachul", "test"])
    fun 회원_닉네임_불일치_조회(keyword: String) {
        //when
        val searchMemberCommand = SearchMemberCommand(nickname = keyword)
        val searchMembers = memberUseCase.searchMembers(searchMemberCommand)

        //then
        assertThat(searchMembers.members.size).isEqualTo(0)
    }

    @Test
    @DisplayName("사용자 정보 조회")
    fun 사용자_정보_조회() {
        // when
        val result = memberUseCase.getMember()

        // then
        assertThat(result.memberId).isEqualTo(RequestUtils.getAttribute("memberId")!!.toLong())
        assertThat(result.email).isEqualTo("email")
        assertThat(result.gender).isEqualTo(GenderType.MALE)
        assertThat(result.ageRange).isEqualTo("20")
    }

    @Test
    @DisplayName("사용자 정보 수정")
    fun 사용자_정보_수정() {
        // given
        val command = UpdateMemberCommand(
            nickname = "afterNickname",
            gender = GenderType.FEMALE,
            ageRange = "30"
        )

        // when
        memberUseCase.updateMember(command)

        // then
        val result = memberUseCase.getMember()

        assertThat(result.nickname).isEqualTo("afterNickname")
        assertThat(result.gender).isEqualTo(GenderType.FEMALE)
        assertThat(result.ageRange).isEqualTo("30")
    }

    @Test
    @DisplayName("사용자 닉네임 사용 가능 여부 체크")
    fun 사용자_닉네임_사용가능_여부_체크() {
        // given
        val availableCommand = CheckNicknameCommand(
            nickname = "nickname11"
        )
        val unavailableCommand = CheckNicknameCommand(
            nickname = "nickname"
        )

        // when
        val availableResult = memberUseCase.checkNickname(availableCommand)
        val unavailableResult = memberUseCase.checkNickname(unavailableCommand)

        // then
        assertThat(availableResult.available).isEqualTo(true)
        assertThat(unavailableResult.available).isEqualTo(false)
    }

    @Test
    fun 역_즐겨찾기_테스트() {
        // given
        val stations = listOf(
            StationEntity(name = "시청역"),
            StationEntity(name = "발산역"),
            StationEntity(name = "강남역")
        )
        stations.forEach { stationRepository.save(it) }

        val command = BookmarkStationCommands(
            stations = listOf(
                BookmarkStationCommand("시청역", ""),
                BookmarkStationCommand("발산역", "집"),
                BookmarkStationCommand("강남역", "회사"),
            )
        )

        // when
        val result = memberUseCase.bookmarkStation(command)

        // then
        assertThat(result.stationInfoList.size).isEqualTo(3)
    }

    @Test
    fun 즐겨찾는_역_수정_테스트() {
        // given
        val stationList = listOf(
            StationEntity(name = "시청역"),
            StationEntity(name = "발산역"),
            StationEntity(name = "강남역"),
            StationEntity(name = "우장산역")
        )

        stationList.forEach {
            stationRepository.save(it)
        }

        stationList.subList(0, 2).forEach {     // origin bookmark
            memberStationRepository.save(
                MemberStationEntity(
                    member = member!!,
                    station = it,
                    label = ""
                )
            )
        }

        val command = BookmarkStationCommands(
            stations = listOf(
                BookmarkStationCommand("시청역", "집"),
            )
        )

        // when
        val result = memberUseCase.bookmarkStation(command)

        // then
        assertThat(result.stationInfoList.size).isEqualTo(1)  // new bookmark
    }

    @Test
    fun 즐겨찾기_역_수정_시_순서_유지() {
        // given
        val stationList = listOf(
            StationEntity(name = "시청역"),
            StationEntity(name = "발산역"),
            StationEntity(name = "강남역"),
            StationEntity(name = "우장산역")
        )

        stationList.forEach {
            stationRepository.save(it)
        }

        stationList.subList(0, 2).forEach {     // origin bookmark
            memberStationRepository.save(
                MemberStationEntity(
                    member = member!!,
                    station = it,
                    label = ""
                )
            )
        }

        val command = BookmarkStationCommands(
            stations = listOf(
                BookmarkStationCommand("시청역", "회사"),
                BookmarkStationCommand("강남역", "집"),
                BookmarkStationCommand("우장산역", "즐겨찾는 장소"),
                BookmarkStationCommand("발산역", "학교"),
            )
        )

        // when
        memberUseCase.bookmarkStation(command)

        // then
        val result = memberUseCase.getBookmarkStation()
        assertThat(result.stationInfoList.size).isEqualTo(4)

        assertThat(result.stationInfoList)
            .extracting("stationName", "label")
            .containsExactly(
                tuple("시청역", "회사"),
                tuple("강남역", "집"),
                tuple("우장산역", "즐겨찾는 장소"),
                tuple("발산역", "학교"),
            )
    }

    @Test
    fun 이미_등록된_즐겨찾기_역_정보와_동일한_정보로_수정_요청시_변경하지_않는다() {
        // given
        val station1 = StationEntity(name = "시청역")
        val station2 = StationEntity(name = "강남역")

        stationRepository.saveAll(listOf(station1, station2))

        val memberStation1 = MemberStationEntity(
            member = member!!,
            station = station1,
            label = "집"
        )

        val memberStation2 = MemberStationEntity(
            member = member!!,
            station = station2,
            label = "직장"
        )

        memberStationRepository.saveAll(listOf(memberStation1, memberStation2))

        val command = BookmarkStationCommands(
            stations = listOf(
                BookmarkStationCommand("시청역", "집"),
                BookmarkStationCommand("강남역", "직장"),
            )
        )

        // when
        val result = memberUseCase.bookmarkStation(command)

        // then
        assertThat(result.stationInfoList.size).isEqualTo(2)
        assertThat(result.stationInfoList[0].stationId).isEqualTo(memberStation1.station.id)
        assertThat(result.stationInfoList[0].stationName).isEqualTo(memberStation1.station.name)
        assertThat(result.stationInfoList[0].label).isEqualTo(memberStation1.label)
        assertThat(result.stationInfoList[1].stationId).isEqualTo(memberStation2.station.id)
        assertThat(result.stationInfoList[1].stationName).isEqualTo(memberStation2.station.name)
        assertThat(result.stationInfoList[1].label).isEqualTo(memberStation2.label)
    }

    @Test
    fun 즐겨찾는_역이_4개_보다_크면_실패() {
        // when + then
        assertThatThrownBy {
            BookmarkStationCommands(
                stations = listOf(
                    BookmarkStationCommand("시청역", "회사"),
                    BookmarkStationCommand("강남역", "집"),
                    BookmarkStationCommand("우장산역", "즐겨찾는 장소"),
                    BookmarkStationCommand("발산역", "학교"),
                    BookmarkStationCommand("양재역", ""),
                )
            )
        }
            .isExactlyInstanceOf(BusinessException::class.java)
            .hasMessage(ResponseCode.EXCEED_MAXIMUM_STATION_COUNT.message)
    }

    @Test
    fun 즐겨찾는_역_조회_테스트() {
        // given
        val stationList = listOf(StationEntity(name = "시청역"), StationEntity(name = "발산역"))
        val subwayLines = listOf(
            SubwayLineEntity(name = "1호선", regionType = RegionType.METROPOLITAN),
            SubwayLineEntity(name = "5호선", regionType = RegionType.METROPOLITAN)
        )

        for (i in stationList.indices) {
            val stationEntity = stationRepository.save(stationList[i])
            val subwayLineEntity = subwayLineRepository.save(subwayLines[i])
            memberStationRepository.save(
                MemberStationEntity(
                    member = member!!,
                    station = stationEntity,
                    label = ""
                )
            )
            subwayLineStationRepository.save(
                SubwayLineStationEntity(
                    station = stationEntity,
                    subwayLine = subwayLineEntity
                )
            )
        }

        // when
        val result = memberUseCase.getBookmarkStation()

        // then
        assertThat(result.stationInfoList.size).isEqualTo(2)
        assertThat(result.stationInfoList[0].stationName).isEqualTo("시청역")
        assertThat(result.stationInfoList[0].subwayLineInfoList[0].subwayLineName).isEqualTo("1호선")
        assertThat(result.stationInfoList[1].stationName).isEqualTo("발산역")
        assertThat(result.stationInfoList[1].subwayLineInfoList[0].subwayLineName).isEqualTo("5호선")
    }
}
