package backend.team.ahachul_backend.api.member.application.port.`in`.command

import backend.team.ahachul_backend.api.member.domain.model.GenderType

data class UpdateMemberCommand(
        val nickname: String?,
        val gender: GenderType?,
        val ageRange: String?,
) {
}