package backend.team.ahachul_backend.api.member.adapter.web.`in`.dto

import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity
import backend.team.ahachul_backend.api.member.domain.model.GenderType

class GetMemberDto {
    data class Response(
            val memberId: Long,
            val nickname: String?,
            val email: String?,
            val maskedEmail: String? = null,
            val gender: GenderType?,
            val ageRange: String?
    ) {
        companion object {
            fun of(memberEntity: MemberEntity): Response {
                return Response(
                        memberId = memberEntity.id,
                        nickname = memberEntity.nickname,
                        email = memberEntity.email,
                        maskedEmail = maskEmail(memberEntity.email),
                        gender = memberEntity.gender,
                        ageRange = memberEntity.ageRange
                )
            }

            private fun maskEmail(email: String?): String? {
                val value = email?.trim()?.takeIf { it.isNotEmpty() } ?: return null
                val parts = value.split("@")
                if (parts.size != 2) {
                    return value
                }

                val local = parts[0]
                val domain = parts[1]
                if (local.isEmpty() || domain.isEmpty()) {
                    return value
                }

                val visibleCount = if (local.length <= 2) 1 else 2
                val maskedCount = maxOf(local.length - visibleCount, 1)
                val maskedLocal = local.take(visibleCount) + "*".repeat(maskedCount)
                return "$maskedLocal@$domain"
            }
        }
    }
}
