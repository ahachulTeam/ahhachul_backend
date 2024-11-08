package backend.team.ahachul_backend.api.member.application.port.out

import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity

interface MemberWriter {

    fun save(memberEntity: MemberEntity): MemberEntity
}