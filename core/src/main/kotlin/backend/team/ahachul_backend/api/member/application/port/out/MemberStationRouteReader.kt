package backend.team.ahachul_backend.api.member.application.port.out

import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity
import backend.team.ahachul_backend.api.member.domain.entity.MemberStationRouteEntity

interface MemberStationRouteReader {

    fun getById(id: Long): MemberStationRouteEntity

    fun findAllByMember(member: MemberEntity): List<MemberStationRouteEntity>

    fun findAllByMemberIdNot(memberId: Long): List<MemberStationRouteEntity>

    fun countByMember(member: MemberEntity): Long

    fun existsByMemberAndPair(member: MemberEntity, sourceStationId: Long, destinationStationId: Long): Boolean
}
