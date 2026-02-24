package backend.team.ahachul_backend.api.delayproof.application.service

import backend.team.ahachul_backend.api.delayproof.adapter.`in`.dto.DelayProofDto
import backend.team.ahachul_backend.common.properties.DelayProofProperties
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.time.Duration
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Optional

@Component
class DelayProofIncidentClient(
    private val properties: DelayProofProperties,
    private val objectMapper: ObjectMapper,
) {

    fun fetchIncidents(
        subwayLineId: Long,
        stationId: Long?,
        limit: Int,
    ): Pair<String, List<DelayProofDto.OfficialIncident>> {
        if (properties.incidentFeedUrl.isBlank()) {
            return Pair("OFFICIAL_FEED_NOT_CONFIGURED", emptyList())
        }

        return runCatching {
            val responseBody = WebClient.builder()
                .baseUrl(properties.incidentFeedUrl.trimEnd('/'))
                .defaultHeader(HttpHeaders.ACCEPT, "application/json")
                .build()
                .get()
                .uri { builder ->
                    builder
                        .queryParam("subwayLineId", subwayLineId)
                        .queryParam("limit", limit)
                        .queryParamIfPresent("stationId", Optional.ofNullable(stationId))
                        .build()
                }
                .headers { headers ->
                    if (properties.incidentApiKey.isNotBlank()) {
                        headers.set("X-API-KEY", properties.incidentApiKey)
                    }
                }
                .retrieve()
                .bodyToMono(String::class.java)
                .block(Duration.ofSeconds(properties.incidentTimeoutSeconds))
                ?: "{}"

            Pair("OFFICIAL_FEED", parseIncidentList(responseBody))
        }.getOrElse {
            Pair("OFFICIAL_FEED_ERROR", emptyList())
        }
    }

    private fun parseIncidentList(rawBody: String): List<DelayProofDto.OfficialIncident> {
        val root = objectMapper.readTree(rawBody)
        val listNode = when {
            root.path("result").path("incidents").isArray -> root.path("result").path("incidents")
            root.path("incidents").isArray -> root.path("incidents")
            root.path("events").isArray -> root.path("events")
            else -> objectMapper.createArrayNode()
        }

        return listNode.mapIndexed { index, node ->
            DelayProofDto.OfficialIncident(
                eventId = node.string("eventId", "official-event-$index"),
                occurredAt = node.string("occurredAt", nowIso()),
                resolvedAt = node.stringOrNull("resolvedAt"),
                severity = node.string("severity", "INFO"),
                title = node.string("title", "운행 안내"),
                description = node.string("description", "외부 긴급 공지 연동 데이터"),
                source = node.string("source", "PUBLIC_FEED"),
                sourceUrl = node.stringOrNull("sourceUrl"),
            )
        }
    }

    private fun JsonNode.string(field: String, fallback: String): String {
        return path(field).takeIf { !it.isMissingNode && !it.isNull }?.asText()?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?: fallback
    }

    private fun JsonNode.stringOrNull(field: String): String? {
        return path(field).takeIf { !it.isMissingNode && !it.isNull }?.asText()?.trim()
            ?.takeIf { it.isNotEmpty() }
    }

    private fun nowIso(): String {
        return OffsetDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }
}
