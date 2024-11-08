package backend.team.ahachul_backend.common.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class KakaoAccessTokenDto (
        @JsonProperty("access_token") val accessToken: String,
        @JsonProperty("token_type") val tokenType: String,
        @JsonProperty("expires_in") val expiresIn: Int,
        @JsonProperty("refresh_token") val refreshToken: String,
        @JsonProperty("refresh_token_expires_in") val refreshTokenExpiresIn: Int
) {

}