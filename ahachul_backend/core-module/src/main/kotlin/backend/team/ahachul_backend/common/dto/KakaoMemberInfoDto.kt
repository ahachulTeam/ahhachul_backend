package backend.team.ahachul_backend.common.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class KakaoMemberInfoDto(
        @JsonProperty("id") val id: String,
        @JsonProperty("kakao_account") val kakaoAccount: KakaoAccount
) {

    data class KakaoAccount(
            @JsonProperty("profile") val profile: Profile?,
            @JsonProperty("email") val email: String?,
            @JsonProperty("age_range") val ageRange: String?,
            @JsonProperty("gender") val gender: String?
    )

    data class Profile(
            @JsonProperty("nickname") val nickname: String
    )
}