package backend.team.ahachul_backend.common.logging

import backend.team.ahachul_backend.common.response.ResponseCode
import mu.KotlinLogging

class NamedLogger(
        private val name: String
) {

    private val logger = KotlinLogging.logger(name)

    fun info(message: String) {
        logger.info { message }
    }

    fun debug(message: String) {
        logger.debug { message }
    }

    fun warn(message: String?, code: ResponseCode, stackTrace: String) {
        logger.warn { printLog(message, code, stackTrace) }
    }

    fun error(message: String?) {
        logger.error { message }
    }

    fun error(message: String?, code: ResponseCode, stackTrace: String) {
        logger.error { printLog(message, code, stackTrace) }
    }

    private fun printLog(message: String?, code: ResponseCode, stackTrace: String): String {
        return "$message - Code: ${code.code}, Message : ${code.message}, StackTrace : $stackTrace"
    }
}
