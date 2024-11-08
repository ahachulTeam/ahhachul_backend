package backend.team.ahachul_backend.api.member.domain.model

enum class GenderType {
    MALE,
    FEMALE;

    companion object {
        fun of(name: String): GenderType {
            return values().first { genderType -> genderType.name == name.uppercase() }
        }
    }
}