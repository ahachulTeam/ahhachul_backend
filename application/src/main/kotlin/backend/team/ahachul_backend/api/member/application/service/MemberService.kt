package backend.team.ahachul_backend.api.member.application.service

import backend.team.ahachul_backend.api.common.application.port.out.StationReader
import backend.team.ahachul_backend.api.common.application.port.out.SubwayLineStationReader
import backend.team.ahachul_backend.api.common.domain.entity.StationEntity
import backend.team.ahachul_backend.api.member.adapter.web.`in`.dto.*
import backend.team.ahachul_backend.api.member.application.port.`in`.MemberUseCase
import backend.team.ahachul_backend.api.member.application.port.`in`.command.BookmarkStationCommand
import backend.team.ahachul_backend.api.member.application.port.`in`.command.CheckNicknameCommand
import backend.team.ahachul_backend.api.member.application.port.`in`.command.UpdateMemberCommand
import backend.team.ahachul_backend.api.member.application.port.out.MemberReader
import backend.team.ahachul_backend.api.member.application.port.out.MemberStationReader
import backend.team.ahachul_backend.api.member.application.port.out.MemberStationWriter
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
    private val subwayLineStationReader: SubwayLineStationReader
) : MemberUseCase {

    override fun getMember(): GetMemberDto.Response {
        val member = memberReader.getMember(RequestUtils.getAttribute("memberId")!!.toLong())
        return GetMemberDto.Response.of(member)
    }

    @Transactional
    override fun updateMember(command: UpdateMemberCommand): UpdateMemberDto.Response {
        val member = memberReader.getMember(RequestUtils.getAttribute("memberId")!!.toLong())
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
    override fun bookmarkStation(command: BookmarkStationCommand): BookmarkStationDto.Response {
        val member = memberReader.getMember(RequestUtils.getAttribute("memberId")!!.toLong())
        val bookmarkStations = command.stationNames
        val originMemberStations = memberStationReader.getByMember(member)

        originMemberStations.forEach {
            val stationName = it.station.name
            if (isAlreadyExists(bookmarkStations, stationName)) {
                bookmarkStations.remove(stationName)
            } else {
                memberStationWriter.delete(it.id)
            }
        }

        val bookmarkStationIds = saveNewStations(member, bookmarkStations)
        return BookmarkStationDto.Response(bookmarkStationIds)
    }

    private fun isAlreadyExists(newNames: List<String>, originName: String): Boolean {
        return newNames.contains(originName)
    }

    private fun saveNewStations(member: MemberEntity, bookmarkStations: List<String>): List<Long> {
        return bookmarkStations
            .map { stationReader.getByName(it) }
            .map { station ->
                val memberStation = MemberStationEntity(
                        member = member,
                        station = station
                )
                memberStationWriter.save(memberStation).id
            }
    }

    override fun getBookmarkStation(): GetBookmarkStationDto.Response {
        val member = memberReader.getMember(RequestUtils.getAttribute("memberId")!!.toLong())

        val bookmarkStations = memberStationReader.getByMember(member)
        val stationInfos = bookmarkStations
            .map {
                val station = it.station
                GetBookmarkStationDto.StationInfo(
                        stationId = station.id,
                        stationName = station.name,
                        subwayLineInfoList = getSubwayLineInfos(station)
                )
            }

        return GetBookmarkStationDto.Response(stationInfos)
    }

    private fun getSubwayLineInfos(station: StationEntity): List<GetBookmarkStationDto.SubwayLineInfo>{
        val subwayLineStations = subwayLineStationReader.findByStation(station)
        return subwayLineStations.map {
            GetBookmarkStationDto.SubwayLineInfo(
                    subwayLineId = it.subwayLine.id,
                    subwayLineName = it.subwayLine.name
            )
        }
    }
}

