package backend.team.ahachul_backend.api.member.application.port.`in`.command

import backend.team.ahachul_backend.api.member.domain.model.ProviderType

class GetRedirectUrlCommand(
        val originHost: String,
        val providerType: ProviderType
) {
}