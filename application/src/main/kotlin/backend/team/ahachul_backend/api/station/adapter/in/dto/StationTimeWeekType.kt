package backend.team.ahachul_backend.api.station.adapter.`in`.dto

enum class StationTimeWeekType(
    val publicCode: Int
) {
    WEEKDAY(1), SATURDAY(2), HOLIDAY(3);

    companion object {
        fun fromPublicCodeOrNull(publicCode: Int?): StationTimeWeekType? {
            if (publicCode == null) {
                return null
            }
            return values().firstOrNull { it.publicCode == publicCode }
        }
    }

}
