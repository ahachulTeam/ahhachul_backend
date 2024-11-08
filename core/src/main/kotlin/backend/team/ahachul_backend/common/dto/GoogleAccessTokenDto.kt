package backend.team.ahachul_backend.common.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class GoogleAccessTokenDto(
    @JsonProperty("access_token") val accessToken: String,
    @JsonProperty("id_token") val idToken: String,
    @JsonProperty("token_type") val tokenType: String,
    @JsonProperty("expires_in") val expiresIn: Int,
    @JsonProperty("scope") val scope: String,
    @JsonProperty("refresh_token") val refreshToken: String?
)