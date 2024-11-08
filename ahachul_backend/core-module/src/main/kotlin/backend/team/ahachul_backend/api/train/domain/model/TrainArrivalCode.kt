package backend.team.ahachul_backend.api.train.domain.model

import backend.team.ahachul_backend.common.exception.DomainException
import backend.team.ahachul_backend.common.response.ResponseCode.INVALID_ENUM

enum class TrainArrivalCode(
    val code: String,
    val priority: Int
) {
    ENTER("0", 2), ARRIVE("1", 1), DEPARTURE("2", 0), BEFORE_STATION_DEPARTURE("3", 3), BEFORE_STATION_ENTER("4", 5), BEFORE_STATION_ARRIVE("5", 4), RUNNING("99", 6);

    companion object {
        fun from(code: String): TrainArrivalCode {
            return TrainArrivalCode.values().find { it.code == code }
                ?: throw DomainException(INVALID_ENUM)
        }
    }
}