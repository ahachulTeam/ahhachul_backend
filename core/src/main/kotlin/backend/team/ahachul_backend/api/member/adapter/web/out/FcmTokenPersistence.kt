package backend.team.ahachul_backend.api.member.adapter.web.out

import backend.team.ahachul_backend.api.member.application.port.out.FcmTokenWriter
import backend.team.ahachul_backend.api.member.application.port.out.MemberStationReader
import backend.team.ahachul_backend.api.member.application.port.out.MemberStationWriter
import backend.team.ahachul_backend.api.member.domain.entity.FcmTokenEntity
import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity
import backend.team.ahachul_backend.api.member.domain.entity.MemberStationEntity
import org.springframework.stereotype.Component

@Component
class FcmTokenPersistence(
    private val fcmTokenRepository: FcmTokenRepository
) : FcmTokenWriter {

    override fun save(entity: FcmTokenEntity): FcmTokenEntity {
        return fcmTokenRepository.save(entity)
    }
}
