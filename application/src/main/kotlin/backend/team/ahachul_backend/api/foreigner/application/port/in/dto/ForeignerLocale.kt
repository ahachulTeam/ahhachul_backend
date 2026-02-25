package backend.team.ahachul_backend.api.foreigner.application.port.`in`.dto

enum class ForeignerLocale(val code: String) {
    KO("ko"),
    EN("en"),
    TH("th"),
    CN("cn");

    companion object {
        fun from(raw: String?): ForeignerLocale {
            if (raw.isNullOrBlank()) {
                return EN
            }

            val normalized = raw.trim().lowercase()
            return when (normalized) {
                KO.code, "kr", "ko-kr" -> KO
                EN.code, "en-us", "en-gb" -> EN
                TH.code, "th-th" -> TH
                CN.code, "zh", "zh-cn", "zh-hans" -> CN
                else -> EN
            }
        }
    }
}
