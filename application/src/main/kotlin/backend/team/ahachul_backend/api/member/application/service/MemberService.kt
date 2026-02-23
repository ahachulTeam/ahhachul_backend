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
import backend.team.ahachul_backend.common.exception.BusinessException
import backend.team.ahachul_backend.common.response.ResponseCode
import backend.team.ahachul_backend.common.utils.RequestUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.text.Normalizer

@Service
@Transactional(readOnly = true)
class MemberService(
    private val memberReader: MemberReader,
    private val stationReader: StationReader,
    private val memberStationWriter: MemberStationWriter,
    private val memberStationReader: MemberStationReader,
    private val subwayLineStationReader: SubwayLineStationReader,
    private val fcmTokenWriter: FcmTokenWriter,
    private val authLogoutCacheUtils: AuthLogoutCacheUtils
) : MemberUseCase {
    companion object {
        private const val NICKNAME_MIN_LENGTH = 2
        private const val NICKNAME_MAX_LENGTH = 10
        private val NICKNAME_REGEX = Regex("^[가-힣a-zA-Z0-9_]+$")
    }

    override fun getMember(): GetMemberDto.Response {
        val member = memberReader.getMember(RequestUtils.getAttribute(RequestUtils.Attribute.MEMBER_ID)!!.toLong())
        return GetMemberDto.Response.of(member)
    }

    @Transactional
    override fun updateMember(command: UpdateMemberCommand): UpdateMemberDto.Response {
        val member = memberReader.getMember(RequestUtils.getAttribute(RequestUtils.Attribute.MEMBER_ID)!!.toLong())
        command.nickname?.let {
            val normalizedNickname = normalizeInput(it)
            validateNickname(normalizedNickname)

            val isChangedNickname = member.nickname != normalizedNickname
            if (
                isChangedNickname &&
                memberReader.existMemberByNicknameExceptMemberId(normalizedNickname, member.id)
            ) {
                throw BusinessException(ResponseCode.DUPLICATE_NICKNAME)
            }

            member.changeNickname(normalizedNickname)
        }
        command.gender?.let { member.changeGender(it) }
        command.ageRange?.let { member.changeAgeRange(it) }
        return UpdateMemberDto.Response.of(
                nickname = member.nickname,
                gender = member.gender,
                ageRange = member.ageRange
        )
    }

    @Transactional
    override fun deleteMember(request: DeleteMemberDto.Request) {
        val member = memberReader.getMember(RequestUtils.getAttribute(RequestUtils.Attribute.MEMBER_ID)!!.toLong())
        member.delete()

        authLogoutCacheUtils.logout(request.accessToken)
    }

    override fun checkNickname(command: CheckNicknameCommand): CheckNicknameDto.Response {
        val normalizedNickname = normalizeInput(command.nickname)
        validateNickname(normalizedNickname)

        return CheckNicknameDto.Response.of(
            available = !memberReader.existMember(normalizedNickname)
        )
    }

    @Transactional
    override fun bookmarkStation(command: BookmarkStationCommands): GetBookmarkStationDto.Response {
        validateDuplicateBookmarkStations(command.stations)

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
                normalizeInput(newBookmarkStationCommands[it].stationName),
                newBookmarkStationCommands[it].label
            )
        }
    }

    private fun saveNewStations(member: MemberEntity, bookmarkStations: List<BookmarkStationCommand>): List<MemberStationEntity> {
        return bookmarkStations
            .map {
                val memberStation = MemberStationEntity(
                    member = member,
                    station = stationReader.getByName(normalizeInput(it.stationName)),
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

    private fun validateNickname(nickname: String) {
        if (nickname.length !in NICKNAME_MIN_LENGTH..NICKNAME_MAX_LENGTH) {
            throw BusinessException(ResponseCode.INVALID_NICKNAME_FORMAT)
        }

        if (!NICKNAME_REGEX.matches(nickname)) {
            throw BusinessException(ResponseCode.INVALID_NICKNAME_FORMAT)
        }
    }

    private fun validateDuplicateBookmarkStations(stations: List<BookmarkStationCommand>) {
        val normalizedStationNames = stations.map { normalizeInput(it.stationName) }
        if (normalizedStationNames.distinct().size != normalizedStationNames.size) {
            throw BusinessException(ResponseCode.DUPLICATE_BOOKMARK_STATION)
        }
    }

    private fun normalizeInput(value: String): String {
        return Normalizer.normalize(value, Normalizer.Form.NFC).trim()
    }
}
