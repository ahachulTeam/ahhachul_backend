package backend.team.ahachul_backend.api.train.application.port.`in`.command

import backend.team.ahachul_backend.common.exception.BusinessException
import backend.team.ahachul_backend.common.response.ResponseCode

class GetCongestionCommand(
    val subwayLineId: Long,
    val trainNo: String
) {

    init {
        if (isInValidSubwayLine(subwayLineId)) {
            throw BusinessException(ResponseCode.INVALID_SUBWAY_LINE)
        }
    }

    private fun isInValidSubwayLine(subwayLine: Long): Boolean {
        return !ALLOWED_SUBWAY_LINES.contains(subwayLine)
    }

    companion object {
        val ALLOWED_SUBWAY_LINES = listOf(2L, 3L)
    }
}
