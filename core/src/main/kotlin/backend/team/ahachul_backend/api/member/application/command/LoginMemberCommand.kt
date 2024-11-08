package backend.team.ahachul_backend.api.member.application.port.`in`.command

import backend.team.ahachul_backend.api.member.domain.model.ProviderType

data class LoginMemberCommand(
        val originHost: String?,
        val providerCode: String,
        val providerType: ProviderType
) {
}