package backend.team.ahachul_backend.common.client

import backend.team.ahachul_backend.common.dto.KakaoMemberInfoDto

interface KakaoMemberClient {

    fun getAccessTokenByCode(code: String, redirectUri: String): String

    fun getMemberInfoByAccessToken(accessToken: String): KakaoMemberInfoDto
}