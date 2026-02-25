package backend.team.ahachul_backend.api.foreigner.application.port.`in`.dto

data class GetForeignerStationGuideCommand(
    val stationId: Long,
    val subwayLineId: Long,
    val locale: ForeignerLocale,
)
