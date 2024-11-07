package backend.team.ahachul_backend.common.utils

import backend.team.ahachul_backend.common.constant.CommonConstant
import org.apache.commons.codec.binary.Base64
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime

object PageTokenUtils {

    fun <T : Any> encodePageToken(vararg data: T): String {
        val formatted = data.joinToString("|") { it.toString() }
        return Base64.encodeBase64URLSafeString(formatted.toByteArray(StandardCharsets.UTF_8))
    }

    fun decodePageToken(pageToken: String, types: List<Class<*>>): List<Any> {
        val decoded = String(Base64.decodeBase64(pageToken), StandardCharsets.UTF_8)
        val parts = decoded.split("|")
        require(parts.size == types.size) { "invalid pageToken format" }
        return parts.mapIndexed { index, part -> stringToValue(part, types[index]) }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> stringToValue(data: String, clazz: Class<T>): T {
        return when (clazz) {
            String::class.java -> data as T
            Int::class.java -> data.toInt() as T
            Long::class.java -> data.toLong() as T
            Boolean::class.java -> data.toBoolean() as T
            LocalDateTime::class.java -> LocalDateTime.parse(data, CommonConstant.CURSOR_PAGING_DATETIME_FORMATTER) as T
            else -> throw IllegalArgumentException("unsupported type - type: $clazz")
        }
    }
}
