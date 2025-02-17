package backend.team.ahachul_backend.api.member.application.port.out

import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity
import backend.team.ahachul_backend.api.member.domain.entity.MemberStationEntity

interface MemberStationWriter {

    fun deleteAllByMember(member: MemberEntity)

    fun save(entity: MemberStationEntity): MemberStationEntity
}
