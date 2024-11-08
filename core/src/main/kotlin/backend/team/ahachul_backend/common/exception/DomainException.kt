package backend.team.ahachul_backend.common.exception

import backend.team.ahachul_backend.common.response.ResponseCode

class DomainException(
        val code: ResponseCode
): RuntimeException(code.message) {
}