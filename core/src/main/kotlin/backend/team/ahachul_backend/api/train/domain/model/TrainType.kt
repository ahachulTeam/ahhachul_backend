package backend.team.ahachul_backend.api.train.domain.model

import backend.team.ahachul_backend.common.exception.DomainException
import backend.team.ahachul_backend.common.response.ResponseCode.INVALID_ENUM

enum class TrainType {
    EXPRESS, GENERAL;

    companion object {
        fun from(code: String): TrainType {
            return when (code) {
                "G" -> GENERAL
                "D" -> EXPRESS
                else -> throw DomainException(INVALID_ENUM)
            }
        }
    }
}