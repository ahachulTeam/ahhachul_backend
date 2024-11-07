package backend.team.ahachul_backend.api.member.adapter.web.out

import backend.team.ahachul_backend.api.member.application.port.out.MemberStationReader
import backend.team.ahachul_backend.api.member.application.port.out.MemberStationWriter
import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity
import backend.team.ahachul_backend.api.member.domain.entity.MemberStationEntity
import org.springframework.stereotype.Component

@Component
class MemberStationPersistence (
    private val memberStationRepository: MemberStationRepository
): MemberStationReader, MemberStationWriter {

    override fun delete(id: Long) {
        memberStationRepository.deleteById(id)
    }

    override fun save(entity: MemberStationEntity): MemberStationEntity {
        return memberStationRepository.save(entity)
    }

    override fun getByMember(member: MemberEntity): List<MemberStationEntity> {
        return memberStationRepository.findAllByMember(member)
    }

}
