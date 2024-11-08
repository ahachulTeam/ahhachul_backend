package backend.team.ahachul_backend.api.member.adapter.web.`in`.dto

import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity
import backend.team.ahachul_backend.api.member.domain.model.GenderType

class GetMemberDto {
    data class Response(
            val memberId: Long,
            val nickname: String?,
            val email: String?,
            val gender: GenderType?,
            val ageRange: String?
    ) {
        companion object {
            fun of(memberEntity: MemberEntity): Response {
                return Response(
                        memberId = memberEntity.id,
                        nickname = memberEntity.nickname,
                        email = memberEntity.email,
                        gender = memberEntity.gender,
                        ageRange = memberEntity.ageRange
                )
            }
        }
    }
}