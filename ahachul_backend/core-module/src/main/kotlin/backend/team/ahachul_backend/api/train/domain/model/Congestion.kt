package backend.team.ahachul_backend.api.train.domain.model

import backend.team.ahachul_backend.common.exception.DomainException
import backend.team.ahachul_backend.common.response.ResponseCode


enum class Congestion(
    private val startPercent: Int,
    private val endPercent: Int
) {

    SMOOTH(0, 35),
    MODERATE(35, 100),
    CONGESTED(100, 200),
    VERY_CONGESTED(200, Int.MAX_VALUE);

    companion object {
        fun from(percent: Int): String {
            return Congestion.values()
                .find { percent >= it.startPercent && percent < it.endPercent }?.name
                ?: throw DomainException(ResponseCode.INVALID_DOMAIN)
        }
    }
}
