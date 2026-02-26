package backend.team.ahachul_backend.api.member.adapter.web.out

import backend.team.ahachul_backend.api.member.application.port.out.MemberStationRouteReader
import backend.team.ahachul_backend.api.member.application.port.out.MemberStationRouteWriter
import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity
import backend.team.ahachul_backend.api.member.domain.entity.MemberStationRouteEntity
import backend.team.ahachul_backend.common.exception.AdapterException
import backend.team.ahachul_backend.common.response.ResponseCode
import org.springframework.stereotype.Component

@Component
class MemberStationRoutePersistence(
    private val memberStationRouteRepository: MemberStationRouteRepository,
) : MemberStationRouteReader, MemberStationRouteWriter {

    override fun getById(id: Long): MemberStationRouteEntity {
        return memberStationRouteRepository.findById(id).orElseThrow {
            AdapterException(ResponseCode.INVALID_DOMAIN)
        }
    }

    override fun findAllByMember(member: MemberEntity): List<MemberStationRouteEntity> {
        return memberStationRouteRepository.findAllByMemberOrderByCreatedAtDesc(member)
    }

    override fun findAllByMemberIdNot(memberId: Long): List<MemberStationRouteEntity> {
        return memberStationRouteRepository.findAllByMemberIdNotOrderByCreatedAtDesc(memberId)
    }

    override fun countByMember(member: MemberEntity): Long {
        return memberStationRouteRepository.countByMember(member)
    }

    override fun existsByMemberAndPair(
        member: MemberEntity,
        sourceStationId: Long,
        destinationStationId: Long,
    ): Boolean {
        return memberStationRouteRepository.existsByMemberAndSourceStationIdAndDestinationStationId(
            member = member,
            sourceStationId = sourceStationId,
            destinationStationId = destinationStationId,
        )
    }

    override fun save(entity: MemberStationRouteEntity): MemberStationRouteEntity {
        return memberStationRouteRepository.save(entity)
    }

    override fun delete(entity: MemberStationRouteEntity) {
        memberStationRouteRepository.delete(entity)
    }
}
