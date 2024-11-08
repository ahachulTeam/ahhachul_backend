package backend.team.ahachul_backend.api.report.domain

import backend.team.ahachul_backend.common.exception.DomainException
import backend.team.ahachul_backend.common.response.ResponseCode
import java.util.*

enum class BlockType(
    val blockDays: Int
) {
    POST(3),
    COMMENT(3),
    APP(5);

    companion object {
        fun of(type: String): BlockType {
            return BlockType.values()
                .firstOrNull {  x -> x.name == type.uppercase(Locale.getDefault()) }
                ?: throw DomainException(ResponseCode.INVALID_ENUM)
        }
    }
}