package backend.team.ahachul_backend.common.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class AppleUserInfoDto(
    @JsonProperty("sub") val sub: String,
    @JsonProperty("email") val email: String,
)