package backend.team.ahachul_backend.api.member.application.service

import backend.team.ahachul_backend.api.common.adapter.web.out.StationRepository
import backend.team.ahachul_backend.api.common.adapter.web.out.SubwayLineStationRepository
import backend.team.ahachul_backend.api.common.domain.entity.StationEntity
import backend.team.ahachul_backend.api.common.domain.entity.SubwayLineStationEntity
import backend.team.ahachul_backend.api.member.adapter.web.`in`.dto.DeleteMemberDto
import backend.team.ahachul_backend.api.member.adapter.web.out.MemberRepository
import backend.team.ahachul_backend.api.member.adapter.web.out.MemberStationRepository
import backend.team.ahachul_backend.api.member.adapter.web.out.MemberStationRouteRepository
import backend.team.ahachul_backend.api.member.application.command.BookmarkStationCommand
import backend.team.ahachul_backend.api.member.application.command.BookmarkStationCommands
import backend.team.ahachul_backend.api.member.application.command.SearchMemberCommand
import backend.team.ahachul_backend.api.member.application.port.`in`.MemberUseCase
import backend.team.ahachul_backend.api.member.application.port.`in`.command.CheckNicknameCommand
import backend.team.ahachul_backend.api.member.application.port.`in`.command.UpdateMemberCommand
import backend.team.ahachul_backend.api.member.application.port.out.MemberReader
import backend.team.ahachul_backend.api.member.application.port.out.MemberWriter
import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity
import backend.team.ahachul_backend.api.member.domain.entity.MemberStationEntity
import backend.team.ahachul_backend.api.member.domain.entity.MemberStationRouteEntity
import backend.team.ahachul_backend.api.member.domain.model.GenderType
import backend.team.ahachul_backend.api.member.domain.model.MemberStatusType
import backend.team.ahachul_backend.api.member.domain.model.ProviderType
import backend.team.ahachul_backend.common.domain.entity.SubwayLineEntity
import backend.team.ahachul_backend.common.domain.model.RegionType
import backend.team.ahachul_backend.common.exception.AdapterException
import backend.team.ahachul_backend.common.exception.BusinessException
import backend.team.ahachul_backend.common.exception.DomainException
import backend.team.ahachul_backend.common.persistence.SubwayLineRepository
import backend.team.ahachul_backend.common.response.ResponseCode
import backend.team.ahachul_backend.common.utils.JwtUtils
import backend.team.ahachul_backend.common.utils.RequestUtils
import backend.team.ahachul_backend.config.controller.CommonServiceTestConfig
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull

class MemberServiceTest(
    @Autowired val memberWriter: MemberWriter,
    @Autowired val memberReader: MemberReader,
    @Autowired val memberUseCase: MemberUseCase,
    @Autowired val memberRepository: MemberRepository,
    @Autowired val stationRepository: StationRepository,
    @Autowired val memberStationRepository: MemberStationRepository,
    @Autowired val memberStationRouteRepository: MemberStationRouteRepository,
    @Autowired val subwayLineStationRepository: SubwayLineStationRepository,
    @Autowired val subwayLineRepository: SubwayLineRepository,
    @Autowired val jwtUtils: JwtUtils,
    @Autowired val authLogoutCacheUtils: AuthLogoutCacheUtils,
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
            email = "email@mail.com",
            gender = GenderType.MALE,
            ageRange = "20",
            status = MemberStatusType.ACTIVE
        ))
        member!!.id.let { RequestUtils.setAttribute(RequestUtils.Attribute.MEMBER_ID, it) }
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
        assertThat(result.memberId).isEqualTo(RequestUtils.getAttribute(RequestUtils.Attribute.MEMBER_ID)!!.toLong())
        assertThat(result.email).isEqualTo("email@mail.com")
        assertThat(result.maskedEmail).isEqualTo("em***@mail.com")
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
    @DisplayName("사용자 정보 수정 시 닉네임이 중복이면 실패한다")
    fun 사용자_정보_수정_닉네임_중복_실패() {
        // given
        val command = UpdateMemberCommand(
            nickname = "nickname1",
            gender = null,
            ageRange = null
        )

        // when + then
        assertThatThrownBy {
            memberUseCase.updateMember(command)
        }
            .isExactlyInstanceOf(BusinessException::class.java)
            .hasMessage(ResponseCode.DUPLICATE_NICKNAME.message)
    }

    @Test
    @DisplayName("사용자 정보 수정 시 닉네임 형식이 올바르지 않으면 실패한다")
    fun 사용자_정보_수정_닉네임_형식_실패() {
        // given
        val command = UpdateMemberCommand(
            nickname = "닉네임!",
            gender = null,
            ageRange = null
        )

        // when + then
        assertThatThrownBy {
            memberUseCase.updateMember(command)
        }
            .isExactlyInstanceOf(BusinessException::class.java)
            .hasMessage(ResponseCode.INVALID_NICKNAME_FORMAT.message)
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
    @DisplayName("사용자 닉네임 사용 가능 여부 체크 시 닉네임 형식이 올바르지 않으면 실패한다")
    fun 사용자_닉네임_사용가능_여부_체크_닉네임_형식_실패() {
        // given
        val command = CheckNicknameCommand(
            nickname = "a"
        )

        // when + then
        assertThatThrownBy {
            memberUseCase.checkNickname(command)
        }
            .isExactlyInstanceOf(BusinessException::class.java)
            .hasMessage(ResponseCode.INVALID_NICKNAME_FORMAT.message)
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
    fun 즐겨찾는_역_중복_등록이면_실패() {
        // given
        stationRepository.save(StationEntity(name = "시청역"))

        val command = BookmarkStationCommands(
            stations = listOf(
                BookmarkStationCommand("시청역", "집"),
                BookmarkStationCommand(" 시청역 ", "회사"),
            )
        )

        // when + then
        assertThatThrownBy {
            memberUseCase.bookmarkStation(command)
        }
            .isExactlyInstanceOf(BusinessException::class.java)
            .hasMessage(ResponseCode.DUPLICATE_BOOKMARK_STATION.message)
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

    @Test
    @DisplayName("경로 기반 인맥 추천 - 출발/도착 오차 1정거장 이내 경로를 추천한다")
    fun 경로_기반_인맥_추천_조회() {
        // given
        val anAm = stationRepository.save(StationEntity(name = "안암"))
        val boMun = stationRepository.save(StationEntity(name = "보문"))
        val wangSimNi = stationRepository.save(StationEntity(name = "왕십리"))
        val seongSu = stationRepository.save(StationEntity(name = "성수"))
        val line = subwayLineRepository.save(
            SubwayLineEntity(name = "2호선", regionType = RegionType.METROPOLITAN)
        )

        subwayLineStationRepository.save(SubwayLineStationEntity(station = anAm, subwayLine = line))
        subwayLineStationRepository.save(SubwayLineStationEntity(station = boMun, subwayLine = line))
        subwayLineStationRepository.save(SubwayLineStationEntity(station = wangSimNi, subwayLine = line))
        subwayLineStationRepository.save(SubwayLineStationEntity(station = seongSu, subwayLine = line))

        memberStationRouteRepository.save(
            MemberStationRouteEntity(
                member = member!!,
                sourceStation = anAm,
                destinationStation = wangSimNi,
                title = "내 출근",
            )
        )

        val nearMember = memberRepository.save(
            MemberEntity(
                nickname = "nearMate",
                provider = ProviderType.GOOGLE,
                providerUserId = "route-near",
                email = "near@mail.com",
                gender = GenderType.MALE,
                ageRange = "20",
                status = MemberStatusType.ACTIVE
            )
        )
        memberStationRouteRepository.save(
            MemberStationRouteEntity(
                member = nearMember,
                sourceStation = boMun,
                destinationStation = seongSu,
                title = "비슷한 출근",
            )
        )

        val farMember = memberRepository.save(
            MemberEntity(
                nickname = "farMate",
                provider = ProviderType.GOOGLE,
                providerUserId = "route-far",
                email = "far@mail.com",
                gender = GenderType.MALE,
                ageRange = "20",
                status = MemberStatusType.ACTIVE
            )
        )
        memberStationRouteRepository.save(
            MemberStationRouteEntity(
                member = farMember,
                sourceStation = seongSu,
                destinationStation = anAm,
                title = "반대 경로",
            )
        )

        // when
        val result = memberUseCase.getRouteConnectionRecommendations(limit = 10, groupLimit = 5)

        // then
        assertThat(result.anchorRoute).isNotNull
        assertThat(result.recommendations).hasSize(1)
        assertThat(result.recommendations.first().memberId).isEqualTo(nearMember.id)
        assertThat(result.recommendations.first().sourceDistance).isEqualTo(1)
        assertThat(result.recommendations.first().destinationDistance).isEqualTo(1)
        assertThat(result.recommendations.first().totalDistance).isEqualTo(2)
        assertThat(result.groups).hasSize(1)
        assertThat(result.graph.edges).hasSize(1)
    }

    @Test
    @DisplayName("출근 코치 - 즐겨찾기 역이 부족하면 가이드 메시지를 반환한다")
    fun 출근_코치_즐겨찾기_부족_가이드() {
        // when
        val result = memberUseCase.getTodayCommuteCoach("09:00", "Asia/Seoul")

        // then
        assertThat(result.primaryRoute).isNull()
        assertThat(result.alternativeRoutes).isEmpty()
        assertThat(result.guidanceMessage).contains("즐겨찾는 역을 2개 이상 등록")
    }

    @Test
    @DisplayName("출근 코치 - 즐겨찾기 역 기반 추천 경로를 반환한다")
    fun 출근_코치_추천_경로_반환() {
        // given
        val stationA = stationRepository.save(StationEntity(name = "안암"))
        val stationB = stationRepository.save(StationEntity(name = "성수"))
        val line = subwayLineRepository.save(
            SubwayLineEntity(name = "6호선", regionType = RegionType.METROPOLITAN)
        )
        subwayLineStationRepository.save(SubwayLineStationEntity(station = stationA, subwayLine = line))
        subwayLineStationRepository.save(SubwayLineStationEntity(station = stationB, subwayLine = line))
        memberStationRepository.save(MemberStationEntity(member = member!!, station = stationA, label = "집"))
        memberStationRepository.save(MemberStationEntity(member = member!!, station = stationB, label = "회사"))

        // when
        val result = memberUseCase.getTodayCommuteCoach("09:00", "Asia/Seoul")

        // then
        assertThat(result.primaryRoute).isNotNull()
        assertThat(result.primaryRoute!!.sourceStationName).isEqualTo("안암")
        assertThat(result.primaryRoute!!.destinationStationName).isEqualTo("성수")
        assertThat(result.safeDepartureAt).matches("\\d{2}:\\d{2}")
        assertThat(result.departureInMinutes).isNotNull
    }

    @Test
    fun fcmTokenInsert() {
        // given
        val token = "TEST"

        // when
        memberUseCase.updateFcmToken(token)

        // then
        val result = memberRepository.findByIdOrNull(member!!.id)
        assertThat(result!!.fcmToken!!.token).isEqualTo(token)
    }

    @Test
    fun fcmTokenUpdate() {
        // given
        val token = "TEST"

        // when
        memberUseCase.updateFcmToken("wrong")
        memberUseCase.updateFcmToken(token)

        // then
        val result = memberRepository.findByIdOrNull(member!!.id)
        assertThat(result!!.fcmToken!!.token).isEqualTo(token)
    }

    @Test
    @DisplayName("이미 탈퇴된 회원은 털퇴를 할 수 없다")
    fun cannotDeleteUserWithDeleteUser() {
        // given
        val member = memberRepository.save(
            MemberEntity(
                nickname = "deletetNickname",
                provider = ProviderType.KAKAO,
                providerUserId = "providerUserId",
                email = "email",
                gender = GenderType.MALE,
                ageRange = "20",
                status = MemberStatusType.DELETE
            )
        )

        RequestUtils.setAttribute(RequestUtils.Attribute.MEMBER_ID, member.id)
        val token = jwtUtils.createToken(member.id.toString(), 100L)

        // when // then
        assertThatThrownBy {
            memberUseCase.deleteMember(DeleteMemberDto.Request(token))
        }
            .isExactlyInstanceOf(DomainException::class.java)
            .hasMessage(ResponseCode.ALREADY_DELETE_MEMBER.message)
    }

    @Test
    @DisplayName("탈퇴 후, 토큰은 로그아웃된다.")
    fun logoutTokenAfterDeleteUser() {
        // given
        val token = jwtUtils.createToken(member!!.id.toString(), 100L)

        // when
        memberUseCase.deleteMember(DeleteMemberDto.Request(token))

        // then
        val notLogout = authLogoutCacheUtils.isNotLogout(token)
        assertThat(notLogout).isFalse()
    }

    @Test
    @DisplayName("사용자를 탈퇴한다.")
    fun deleteUser() {
        // given
        val token = jwtUtils.createToken(member!!.id.toString(), 100L)

        // when
        memberUseCase.deleteMember(DeleteMemberDto.Request(token))
        val findMember = memberRepository.findById(member!!.id).get()

        // then
        assertThat(findMember.isDeleted()).isTrue()
    }

    @Test
    @DisplayName("탈퇴 후, 사용자 조회가 불가하다.")
    fun cannotSelectUserAfterDeleteUser() {
        // given
        val token = jwtUtils.createToken(member!!.id.toString(), 100L)
        memberUseCase.deleteMember(DeleteMemberDto.Request(token))

        // when & then
        // ID로 조회
        assertThatThrownBy {
            memberReader.getMember(member!!.id)
        }
            .isExactlyInstanceOf(DomainException::class.java)
            .hasMessage(ResponseCode.ALREADY_DELETE_MEMBER.message)

        // providerUserId로 조회
        assertThatThrownBy {
            memberReader.findMember(member!!.providerUserId)
        }
            .isExactlyInstanceOf(DomainException::class.java)
            .hasMessage(ResponseCode.ALREADY_DELETE_MEMBER.message)
    }
}
