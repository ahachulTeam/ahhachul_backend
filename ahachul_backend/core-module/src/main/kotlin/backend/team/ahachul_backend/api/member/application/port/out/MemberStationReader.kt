package backend.team.ahachul_backend.api.member.application.port.out

import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity
import backend.team.ahachul_backend.api.member.domain.entity.MemberStationEntity

interface MemberStationReader {

    fun getByMember(member: MemberEntity): List<MemberStationEntity>
}
