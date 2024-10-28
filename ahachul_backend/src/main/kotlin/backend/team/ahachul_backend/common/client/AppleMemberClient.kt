package backend.team.ahachul_backend.common.client

import backend.team.ahachul_backend.common.dto.AppleUserInfoDto

interface AppleMemberClient {

    fun getIdTokenByCode(code: String): String

    fun getMemberInfoByIdToken(idToken: String): AppleUserInfoDto
}