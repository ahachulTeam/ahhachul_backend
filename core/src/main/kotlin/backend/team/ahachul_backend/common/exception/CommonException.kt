package backend.team.ahachul_backend.common.exception

import backend.team.ahachul_backend.common.response.ResponseCode

class CommonException(
        val code: ResponseCode,
        val e: Throwable? = null
): RuntimeException(code.message, e) {
}