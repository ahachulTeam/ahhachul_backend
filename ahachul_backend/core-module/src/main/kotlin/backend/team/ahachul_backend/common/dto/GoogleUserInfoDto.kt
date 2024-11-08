package backend.team.ahachul_backend.common.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class GoogleUserInfoDto(
    @JsonProperty("id") val id: String,
    @JsonProperty("email") val email: String,
    @JsonProperty("verified_email") val verifiedEmail: Boolean,
    @JsonProperty("name") val name: String,
    @JsonProperty("given_name") val givenName: String?,
    @JsonProperty("family_name") val familyName: String?,
    @JsonProperty("picture") val picture: String?,
    @JsonProperty("locale") val locale: String?
)