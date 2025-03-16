package backend.team.ahachul_backend.api.member.application.port.out

import backend.team.ahachul_backend.api.member.domain.entity.FcmTokenEntity

interface FcmTokenWriter {

    fun save(entity: FcmTokenEntity): FcmTokenEntity
}