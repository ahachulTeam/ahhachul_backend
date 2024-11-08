package backend.team.ahachul_backend.api.complaint.domain.model

enum class ShortContentType(
    val description: String
) {
    WASTE("오물"),
    VOMIT("토사물"),
    VENTILATION_REQUEST("환기요청"),
    NOISY("시끄러워요"),
    NOT_HEARD("안들려요"),
    TOO_HOT("더워요"),
    TOO_COLD("추워요"),
    MOBILE_VENDOR("이동상인"),
    DRUNK("취객"),
    HOMELESS("노숙"),
    BEGGING("구걸"),
    RELIGIOUS_ACTIVITY("종교행위"),
    SELF("본인"),
    WITNESS("목격자"),
    VICTIM("피해자")
}