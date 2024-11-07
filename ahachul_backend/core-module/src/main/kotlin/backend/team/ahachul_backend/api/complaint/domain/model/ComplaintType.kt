package backend.team.ahachul_backend.api.complaint.domain.model

enum class ComplaintType(
    val description: String
) {
    ENVIRONMENTAL_COMPLAINT("환경민원"),
    TEMPERATURE_CONTROL("온도조절"),
    DISORDER("질서저해"),
    ANNOUNCEMENT("안내방송"),
    EMERGENCY_PATIENT("응급환자"),
    VIOLENCE("폭력"),
    SEXUAL_HARASSMENT("성추행"),
    OTHER_COMPLAINT("기타민원")
}