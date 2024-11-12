package backend.team.ahachul_backend.common.logging

import backend.team.ahachul_backend.common.response.ResponseCode
import mu.KotlinLogging

class Logger(
    private val clazz: Class<*>,
) {

    private val logger = KotlinLogging.logger(clazz.name)

    fun info(message: String) {
        logger.info { message }
    }

    fun debug(message: String) {
        logger.debug { message }
    }

    fun warn(message: String?, code: ResponseCode, ex: Exception) {
        logger.warn(ex) { makeMessage(message, code) }
    }

    fun error(message: String?) {
        logger.error { message }
    }

    fun error(message: String?, code: ResponseCode, ex: Exception) {
        logger.error(ex) { makeMessage(message, code) }
    }

    private fun makeMessage(message: String?, code: ResponseCode): String {
        return "$message - Code: ${code.code}, Message : ${code.message}"
    }
}
