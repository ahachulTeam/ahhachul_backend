package backend.team.ahachul_backend.common.exception

import backend.team.ahachul_backend.common.response.ResponseCode

class AdapterException(
        val code: ResponseCode
): RuntimeException(code.message) {
}