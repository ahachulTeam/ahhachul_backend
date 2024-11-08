package backend.team.ahachul_backend.api.lost.application.service.command.`in`

import backend.team.ahachul_backend.api.lost.domain.model.LostType

class SearchLostPostCommand(
    val lostType: LostType,
    val subwayLineId: Long?,
    val category: String?,
    val keyword: String?,
    val pageToken: String?,
    val pageSize: Int
)
