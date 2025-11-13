package backend.team.ahachul_backend.common.storage

import backend.team.ahachul_backend.common.domain.entity.SubwayLineEntity
import backend.team.ahachul_backend.common.persistence.SubwayLineReader
import org.springframework.stereotype.Component


@Component
class SubwayLineStorage(
    private var subwayLines: List<SubwayLineEntity>,
    private val subwayLineReader: SubwayLineReader
) {

    init {
        subwayLines = subwayLineReader.getSubwayLines()
    }

    fun extractSubWayLine(place: String): String{
        if (!place.endsWith(')')) return place

        val res = StringBuilder()
        for (i: Int in place.length - 2 downTo 0) {
            if (place[i] == '(') break
            res.append(place[i])
        }

        return res.reverse().toString()
    }

    fun getSubwayLineEntityByName(subwayLineName: String): SubwayLineEntity? {
        return runCatching {
            subwayLines.first { subwayLine -> subwayLine.name == subwayLineName }
        }.getOrNull()
    }
}
