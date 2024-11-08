package backend.team.ahachul_backend.common.response

class CommonResponse<T> private constructor(
        val code: String,
        val message: String,
        val result: T?
) {

    companion object {
        fun success(): CommonResponse<Unit> {
            return CommonResponse(
                    code = ResponseCode.SUCCESS.code,
                    message = ResponseCode.SUCCESS.message,
                    result = null
            )
        }

        fun <T> success(result: T): CommonResponse<T> {
            return CommonResponse(
                    code = ResponseCode.SUCCESS.code,
                    message = ResponseCode.SUCCESS.message,
                    result = result
            )
        }

        fun fail(): CommonResponse<Unit> {
            return CommonResponse(
                    code = ResponseCode.INTERNAL_SERVER_ERROR.code,
                    message = ResponseCode.INTERNAL_SERVER_ERROR.message,
                    result = null
            )
        }

        fun fail(errorCode: ResponseCode): CommonResponse<Unit> {
            return CommonResponse(
                    code = errorCode.code,
                    message = errorCode.message,
                    result = null
            )
        }
    }
}