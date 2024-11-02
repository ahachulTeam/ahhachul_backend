package backend.team.ahachul_backend.api.member.application.service

import backend.team.ahachul_backend.api.member.adapter.web.`in`.dto.GetRedirectUrlDto
import backend.team.ahachul_backend.api.member.adapter.web.`in`.dto.GetTokenDto
import backend.team.ahachul_backend.api.member.adapter.web.`in`.dto.LoginMemberDto
import backend.team.ahachul_backend.api.member.application.port.`in`.AuthUseCase
import backend.team.ahachul_backend.api.member.application.port.`in`.command.GetRedirectUrlCommand
import backend.team.ahachul_backend.api.member.application.port.`in`.command.GetTokenCommand
import backend.team.ahachul_backend.api.member.application.port.`in`.command.LoginMemberCommand
import backend.team.ahachul_backend.api.member.application.port.out.MemberReader
import backend.team.ahachul_backend.api.member.application.port.out.MemberWriter
import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity
import backend.team.ahachul_backend.api.member.domain.model.ProviderType
import backend.team.ahachul_backend.common.client.GoogleMemberClient
import backend.team.ahachul_backend.common.client.KakaoMemberClient
import backend.team.ahachul_backend.common.dto.GoogleUserInfoDto
import backend.team.ahachul_backend.common.dto.KakaoMemberInfoDto
import backend.team.ahachul_backend.common.properties.JwtProperties
import backend.team.ahachul_backend.common.properties.OAuthProperties
import backend.team.ahachul_backend.common.utils.JwtUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.util.UriComponentsBuilder
import java.util.*

@Service
@Transactional(readOnly = true)
class AuthService(
    private val memberWriter: MemberWriter,
    private val memberReader: MemberReader,
    private val kakaoMemberClient: KakaoMemberClient,
    private val googleMemberClient: GoogleMemberClient,
    private val jwtUtils: JwtUtils,
    private val jwtProperties: JwtProperties,
    private val oAuthProperties: OAuthProperties,
) : AuthUseCase {

    companion object {
        const val sevenDaysInMillis = 7 * 24 * 60 * 60 * 1000
    }

    @Transactional
    override fun login(command: LoginMemberCommand): LoginMemberDto.Response {
        var isDuplicatedNickname = false
        val member = when (command.providerType) {
            ProviderType.KAKAO -> {
                val userInfo = getKakaoMemberInfo(
                    command.providerCode,
                    getRedirectUriByOrigin(command.originHost, ProviderType.KAKAO)
                )
                val member = memberReader.findMember(userInfo.id)
                userInfo.kakaoAccount.profile?.let { profile ->
                    isDuplicatedNickname = memberReader.existMember(profile.nickname)
                }
                member ?: memberWriter.save(MemberEntity.ofKakao(command, userInfo))
            }

            ProviderType.GOOGLE -> {
                val userInfo = getGoogleMemberInfo(
                    command.providerCode,
                    getRedirectUriByOrigin(command.originHost, ProviderType.GOOGLE)
                )
                val member = memberReader.findMember(userInfo.id)
                isDuplicatedNickname = memberReader.existMember(userInfo.name)
                member ?: memberWriter.save(MemberEntity.ofGoogle(command, userInfo))
            }
        }
        return makeLoginResponse(member.id.toString(), member.isNeedAdditionalUserInfo() || isDuplicatedNickname)
    }

    private fun getKakaoMemberInfo(provideCode: String, redirectUri: String): KakaoMemberInfoDto {
        val accessToken = kakaoMemberClient.getAccessTokenByCode(provideCode, redirectUri)
        return kakaoMemberClient.getMemberInfoByAccessToken(accessToken)
    }

    private fun getGoogleMemberInfo(provideCode: String, redirectUri: String): GoogleUserInfoDto {
        val accessToken = googleMemberClient.getAccessTokenByCode(provideCode, redirectUri)
        return googleMemberClient.getMemberInfoByAccessToken(accessToken)
    }

    private fun makeLoginResponse(memberId: String, isNeedAdditionalUserInfo: Boolean): LoginMemberDto.Response {
        return LoginMemberDto.Response(
            memberId = memberId,
            isNeedAdditionalUserInfo = isNeedAdditionalUserInfo,
            accessToken = jwtUtils.createToken(memberId, jwtProperties.accessTokenExpireTime),
            accessTokenExpiresIn = jwtProperties.accessTokenExpireTime,
            refreshToken = jwtUtils.createToken(memberId, jwtProperties.refreshTokenExpireTime),
            refreshTokenExpiresIn = jwtProperties.refreshTokenExpireTime
        )
    }

    override fun getToken(command: GetTokenCommand): GetTokenDto.Response {
        val refreshToken = jwtUtils.verify(command.refreshToken)

        if (refreshToken.body.expiration.after(Date(System.currentTimeMillis() - sevenDaysInMillis))) {
            return GetTokenDto.Response(
                accessToken = jwtUtils.createToken(refreshToken.body.subject, jwtProperties.accessTokenExpireTime),
                accessTokenExpiresIn = jwtProperties.accessTokenExpireTime,
                refreshToken = jwtUtils.createToken(refreshToken.body.subject, jwtProperties.refreshTokenExpireTime),
                refreshTokenExpiresIn = jwtProperties.refreshTokenExpireTime
            )
        }
        return GetTokenDto.Response(
            accessToken = jwtUtils.createToken(refreshToken.body.subject, jwtProperties.accessTokenExpireTime),
            accessTokenExpiresIn = jwtProperties.accessTokenExpireTime
        )
    }

    private fun getRedirectUriByOrigin(originHost: String, providerType: ProviderType): String {
        val providerTypeStr = providerType.toString().lowercase()
        val client = oAuthProperties.client[providerTypeStr]!!

        return if (originHost.endsWith("/")) "$originHost${client.redirectUriPath}" else "$originHost/${client.redirectUriPath}"
    }

    override fun getRedirectUrl(command: GetRedirectUrlCommand): GetRedirectUrlDto.Response {
        val providerTypeStr = command.providerType.toString().lowercase()
        val client = oAuthProperties.client[providerTypeStr]!!
        val provider = oAuthProperties.provider[providerTypeStr]!!

        val redirectUri = getRedirectUriByOrigin(command.originHost, command.providerType)

        return GetRedirectUrlDto.Response(
            when (command.providerType) {
                ProviderType.KAKAO -> UriComponentsBuilder.fromUriString(provider.loginUri)
                    .queryParam("client_id", client.clientId)
                    .queryParam("redirect_uri", redirectUri)
                    .queryParam("response_type", client.responseType)
                    .build()
                    .toString()

                ProviderType.GOOGLE -> UriComponentsBuilder.fromUriString(provider.loginUri)
                    .queryParam("client_id", client.clientId)
                    .queryParam("redirect_uri", redirectUri)
                    .queryParam("access_type", client.accessType)
                    .queryParam("response_type", client.responseType)
                    .queryParam("scope", client.scope)
                    .build()
                    .toString()
            }
        )
    }
}

