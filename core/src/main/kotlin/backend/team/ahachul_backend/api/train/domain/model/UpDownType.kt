package backend.team.ahachul_backend.api.train.domain.model

import backend.team.ahachul_backend.common.exception.DomainException
import backend.team.ahachul_backend.common.response.ResponseCode.INVALID_ENUM

enum class UpDownType {
    UP, DOWN;

    companion object {
        fun from(code: String): UpDownType {
            return when (code) {
                "상행", "내선" -> UP
                "하행", "외선" -> DOWN
                else -> throw DomainException(INVALID_ENUM)
            }
        }
    }
}