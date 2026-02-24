package backend.team.ahachul_backend.api.member.application.port.out

import backend.team.ahachul_backend.api.member.domain.entity.MemberStationRouteEntity

interface MemberStationRouteWriter {

    fun save(entity: MemberStationRouteEntity): MemberStationRouteEntity

    fun delete(entity: MemberStationRouteEntity)
}
