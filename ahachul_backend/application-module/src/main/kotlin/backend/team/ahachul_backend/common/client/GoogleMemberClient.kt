package backend.team.ahachul_backend.common.client

import backend.team.ahachul_backend.common.dto.GoogleUserInfoDto

interface GoogleMemberClient {

    fun getAccessTokenByCodeAndOrigin(code: String, origin: String?): String

    fun getMemberInfoByAccessToken(accessToken: String): GoogleUserInfoDto
}