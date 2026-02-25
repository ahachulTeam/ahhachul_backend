package backend.team.ahachul_backend.api.foreigner.application.port.`in`.dto

data class GetForeignerStationSocialOverviewCommand(
    val stationId: Long,
    val subwayLineId: Long?,
    val locale: ForeignerLocale,
    val sameNationalityOnly: Boolean,
    val nationalityCode: String?,
    val limit: Int,
)
