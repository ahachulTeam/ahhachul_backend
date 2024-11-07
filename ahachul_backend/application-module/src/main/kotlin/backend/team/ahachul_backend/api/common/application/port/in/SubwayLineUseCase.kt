package backend.team.ahachul_backend.api.common.application.port.`in`

import backend.team.ahachul_backend.api.common.adapter.`in`.dto.SearchSubwayLineDto

interface SubwayLineUseCase {

    fun searchSubwayLines(): SearchSubwayLineDto.Response
}
