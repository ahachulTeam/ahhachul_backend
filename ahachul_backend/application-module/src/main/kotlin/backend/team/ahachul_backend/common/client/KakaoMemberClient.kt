package backend.team.ahachul_backend.common.client

import backend.team.ahachul_backend.common.dto.KakaoMemberInfoDto

interface KakaoMemberClient {

    fun getAccessTokenByCodeAndOrigin(code: String, origin: String?): String

    fun getMemberInfoByAccessToken(accessToken: String): KakaoMemberInfoDto
}