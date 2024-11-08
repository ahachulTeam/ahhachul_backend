package backend.team.ahachul_backend.api.member.application.port.out

import backend.team.ahachul_backend.api.member.domain.entity.MemberStationEntity

interface MemberStationWriter {

    fun delete(id: Long)

    fun save(entity: MemberStationEntity): MemberStationEntity
}
