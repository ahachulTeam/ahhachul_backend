package backend.team.ahachul_backend.api.member.application.service

import backend.team.ahachul_backend.api.common.application.port.out.StationReader
import backend.team.ahachul_backend.api.common.application.port.out.SubwayLineStationReader
import backend.team.ahachul_backend.api.common.domain.entity.StationEntity
import backend.team.ahachul_backend.api.member.adapter.web.`in`.dto.*
import backend.team.ahachul_backend.api.member.application.command.BookmarkStationCommand
import backend.team.ahachul_backend.api.member.application.command.SearchMemberCommand
import backend.team.ahachul_backend.api.member.application.port.`in`.MemberUseCase
import backend.team.ahachul_backend.api.member.application.command.BookmarkStationCommands
import backend.team.ahachul_backend.api.member.application.port.`in`.command.CheckNicknameCommand
import backend.team.ahachul_backend.api.member.application.port.`in`.command.UpdateMemberCommand
import backend.team.ahachul_backend.api.member.application.port.out.FcmTokenWriter
import backend.team.ahachul_backend.api.member.application.port.out.MemberReader
import backend.team.ahachul_backend.api.member.application.port.out.MemberStationReader
import backend.team.ahachul_backend.api.member.application.port.out.MemberStationWriter
import backend.team.ahachul_backend.api.member.domain.entity.FcmTokenEntity
import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity
import backend.team.ahachul_backend.api.member.domain.entity.MemberStationEntity
import backend.team.ahachul_backend.common.utils.RequestUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class MemberService(
    private val memberReader: MemberReader,
    private val stationReader: StationReader,
    private val memberStationWriter: MemberStationWriter,
    private val memberStationReader: MemberStationReader,
    private val subwayLineStationReader: SubwayLineStationReader,
    private val fcmTokenWriter: FcmTokenWriter
) : MemberUseCase {

    override fun getMember(): GetMemberDto.Response {
        val member = memberReader.getMember(RequestUtils.getAttribute(RequestUtils.Attribute.MEMBER_ID)!!.toLong())
        return GetMemberDto.Response.of(member)
    }

    @Transactional
    override fun updateMember(command: UpdateMemberCommand): UpdateMemberDto.Response {
        val member = memberReader.getMember(RequestUtils.getAttribute(RequestUtils.Attribute.MEMBER_ID)!!.toLong())
        command.nickname?.let { member.changeNickname(it) }
        command.gender?.let { member.changeGender(it) }
        command.ageRange?.let { member.changeAgeRange(it) }
        return UpdateMemberDto.Response.of(
            nickname = member.nickname,
            gender = member.gender,
            ageRange = member.ageRange
        )
    }

    override fun checkNickname(command: CheckNicknameCommand): CheckNicknameDto.Response {
        return CheckNicknameDto.Response.of(
            available = !memberReader.existMember(command.nickname)
        )
    }

    @Transactional
    override fun bookmarkStation(command: BookmarkStationCommands): GetBookmarkStationDto.Response {
        val member = memberReader.getMember(RequestUtils.getAttribute(RequestUtils.Attribute.MEMBER_ID)!!.toLong())
        val bookmarkStations = memberStationReader.getByMember(member)

        if (isEqualsAlreadyRegisteredStation(bookmarkStations, command.stations)) {
            return createBookmarkStationResponse(bookmarkStations)
        }

        if (bookmarkStations.isNotEmpty()) {
            memberStationWriter.deleteAllByMember(member)
        }

        val savedMemberStations = saveNewStations(member, command.stations)
        return createBookmarkStationResponse(savedMemberStations)
    }

    override fun getBookmarkStation(): GetBookmarkStationDto.Response {
        val member = memberReader.getMember(RequestUtils.getAttribute(RequestUtils.Attribute.MEMBER_ID)!!.toLong())

        val bookmarkStations = memberStationReader.getByMember(member)

        return createBookmarkStationResponse(bookmarkStations)
    }

    override fun searchMembers(command: SearchMemberCommand): SearchMemberDto.Response {
        val members = memberReader.searchMembers(command).map {
            SearchMemberDto.SearchMemberResponse(
                id = it.id,
                nickname = it.nickname
            )
        }

        return SearchMemberDto.Response.of(members)
    }

    @Transactional
    override fun updateFcmToken(fcmToken: String) {
        val member = memberReader.getMember(RequestUtils.getAttribute(RequestUtils.Attribute.MEMBER_ID)!!.toLong())
        val tokenEntity = member.fcmToken
        if (tokenEntity == null) {
            member.fcmToken = fcmTokenWriter.save(FcmTokenEntity(member = member, token = fcmToken))
        } else {
            tokenEntity.token = fcmToken
        }
    }

    private fun isEqualsAlreadyRegisteredStation(
        originMemberStations: List<MemberStationEntity>,
        newBookmarkStationCommands: List<BookmarkStationCommand>
    ): Boolean {
        if (originMemberStations.size != newBookmarkStationCommands.size) {
            return false
        }

        return originMemberStations.indices.all {
            originMemberStations[it].isEquals(
                newBookmarkStationCommands[it].stationName,
                newBookmarkStationCommands[it].label
            )
        }
    }

    private fun saveNewStations(
        member: MemberEntity,
        bookmarkStations: List<BookmarkStationCommand>
    ): List<MemberStationEntity> {
        return bookmarkStations
            .map {
                val memberStation = MemberStationEntity(
                    member = member,
                    station = stationReader.getByName(it.stationName),
                    label = it.label
                )
                memberStationWriter.save(memberStation)
            }
    }

    private fun createBookmarkStationResponse(memberStations: List<MemberStationEntity>): GetBookmarkStationDto.Response {
        val stationInfos = memberStations
            .map {
                val station = it.station
                GetBookmarkStationDto.StationInfo(
                    stationId = station.id,
                    stationName = station.name,
                    label = it.label,
                    subwayLineInfoList = getSubwayLineInfos(station)
                )
            }

        return GetBookmarkStationDto.Response(stationInfos)
    }

    private fun getSubwayLineInfos(station: StationEntity): List<GetBookmarkStationDto.SubwayLineInfo> {
        val subwayLineStations = subwayLineStationReader.findByStation(station)
        return subwayLineStations.map {
            GetBookmarkStationDto.SubwayLineInfo(
                subwayLineId = it.subwayLine.id,
                subwayLineName = it.subwayLine.name
            )
        }
    }
}

