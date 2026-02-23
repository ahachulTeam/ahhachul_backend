package backend.team.ahachul_backend.api.train.application.service

import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.math.max

object TrainRealtimeV2Calculator {

    data class Meta(
        val lastExternalRecptnAt: OffsetDateTime,
        val freshnessSec: Int,
        val confidenceLevel: String,
    )

    private val localDateTimeFormatters = listOf(
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
        DateTimeFormatter.ofPattern("yyyyMMddHHmmss"),
    )

    fun resolveMeta(generatedAt: OffsetDateTime, recptnAtRawList: List<String?>): Meta {
        val parsedTimes = recptnAtRawList.mapNotNull { parseRecptnAt(it) }
        val lastExternalRecptnAt = parsedTimes.maxOrNull() ?: generatedAt
        val freshnessSec = max((generatedAt.toEpochSecond() - lastExternalRecptnAt.toEpochSecond()).toInt(), 0)

        return Meta(
            lastExternalRecptnAt = lastExternalRecptnAt,
            freshnessSec = freshnessSec,
            confidenceLevel = resolveConfidenceLevel(freshnessSec),
        )
    }

    fun calculateEtaSec(rawEtaSec: Int, freshnessSec: Int): Int {
        return max(rawEtaSec - freshnessSec, 0)
    }

    fun calculateEtaMinDisplay(etaSec: Int): Int {
        if (etaSec <= 0) {
            return 0
        }
        return (etaSec + 59) / 60
    }

    private fun resolveConfidenceLevel(freshnessSec: Int): String {
        return when {
            freshnessSec <= 60 -> "HIGH"
            freshnessSec <= 120 -> "MEDIUM"
            else -> "LOW"
        }
    }

    private fun parseRecptnAt(raw: String?): OffsetDateTime? {
        if (raw.isNullOrBlank()) {
            return null
        }

        val value = raw.trim()

        runCatching {
            OffsetDateTime.parse(value)
        }.getOrNull()?.let { return it }

        localDateTimeFormatters.forEach { formatter ->
            runCatching {
                LocalDateTime.parse(value, formatter)
                    .atOffset(ZoneOffset.ofHours(9))
            }.getOrNull()?.let { return it }
        }

        return null
    }
}
