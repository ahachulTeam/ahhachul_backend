package backend.team.ahachul_backend.common

import backend.team.ahachul_backend.common.exception.*
import backend.team.ahachul_backend.common.logging.Logger
import backend.team.ahachul_backend.common.response.CommonResponse
import backend.team.ahachul_backend.common.response.ResponseCode
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class CommonExceptionHandler {

    val logger = Logger(javaClass)

    @ExceptionHandler(Exception::class)
    fun exception(e: Exception): ResponseEntity<CommonResponse<Unit>> {
        logger.error(
                message = e.message,
                code = ResponseCode.INTERNAL_SERVER_ERROR,
                stackTrace = e.stackTrace.contentToString()
        )
        return ResponseEntity(CommonResponse.fail(), HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(CommonException::class)
    fun commonException(e: CommonException): ResponseEntity<CommonResponse<Unit>> {
        logger.error(
                message = e.message,
                code = e.code,
                stackTrace = e.stackTrace.contentToString()
        )
        return ResponseEntity(CommonResponse.fail(e.code), e.code.httpStatus)
    }

    @ExceptionHandler(AdapterException::class)
    fun adapterException(e: AdapterException): ResponseEntity<CommonResponse<Unit>> {
        logger.error(
                message = e.message,
                code = e.code,
                stackTrace = e.stackTrace.contentToString()
        )
        return ResponseEntity(CommonResponse.fail(e.code), e.code.httpStatus)
    }

    @ExceptionHandler(PortException::class)
    fun portException(e: PortException): ResponseEntity<CommonResponse<Unit>> {
        logger.error(
                message = e.message,
                code = e.code,
                stackTrace = e.stackTrace.contentToString()
        )
        return ResponseEntity(CommonResponse.fail(e.code), e.code.httpStatus)
    }

    @ExceptionHandler(DomainException::class)
    fun domainException(e: DomainException): ResponseEntity<CommonResponse<Unit>> {
        logger.error(
                message = e.message,
                code = e.code,
                stackTrace = e.stackTrace.contentToString()
        )
        return ResponseEntity(CommonResponse.fail(e.code), e.code.httpStatus)
    }

    @ExceptionHandler(BusinessException::class)
    fun domainException(e: BusinessException): ResponseEntity<CommonResponse<Unit>> {
        logger.error(
                message = e.message,
                code = e.code,
                stackTrace = e.stackTrace.contentToString()
        )
        return ResponseEntity(CommonResponse.fail(e.code), e.code.httpStatus)
    }
}
