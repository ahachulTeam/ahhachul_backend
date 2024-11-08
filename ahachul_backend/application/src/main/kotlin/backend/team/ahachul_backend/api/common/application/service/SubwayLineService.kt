package backend.team.ahachul_backend.api.common.application.service

import backend.team.ahachul_backend.api.common.adapter.`in`.dto.SearchSubwayLineDto
import backend.team.ahachul_backend.api.common.adapter.`in`.dto.Station
import backend.team.ahachul_backend.api.common.adapter.`in`.dto.SubwayLine
import backend.team.ahachul_backend.api.common.application.port.`in`.SubwayLineUseCase
import backend.team.ahachul_backend.api.common.application.port.out.SubwayLineStationReader
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class SubwayLineService(
    private val lineStationReader: SubwayLineStationReader
): SubwayLineUseCase {

    @Cacheable("subwayLines")
    override fun searchSubwayLines(): SearchSubwayLineDto.Response {
        val subwayLines = lineStationReader.findAll()
                .groupBy { it.subwayLine.id }
                .map {
                    SubwayLine.of(it.value[0].subwayLine, Station.toList(it.value))
                }

        return SearchSubwayLineDto.Response(subwayLines)
    }
}
