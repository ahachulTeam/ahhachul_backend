package backend.team.ahachul_backend.common.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class ApplePublicKeyDto(
    val keys: List<Key>
) {

    data class Key(
        @JsonProperty("kty") val kty: String,
        @JsonProperty("kid") val kid: String,
        @JsonProperty("use") val use: String,
        @JsonProperty("alg") val alg: String,
        @JsonProperty("n") val n: String,
        @JsonProperty("e") val e: String
    )

    /**
     * 두개 중 Identity Token header 에 포함된 kid, alg 와 일치하는 key 사용
     */
    fun getMatchedInfo(kid: String, alg: String): Key? {
        return this.keys
            .firstOrNull { key -> key.kid == kid && key.alg == alg }
    }
}