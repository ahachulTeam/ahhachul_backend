package backend.team.ahachul_backend.api.delayproof.application.service

import backend.team.ahachul_backend.api.community.application.command.out.GetSliceCommunityPostCommand
import backend.team.ahachul_backend.api.community.application.port.out.CommunityPostReader
import backend.team.ahachul_backend.api.community.domain.SearchCommunityPost
import backend.team.ahachul_backend.api.delayproof.adapter.`in`.dto.DelayProofDto
import backend.team.ahachul_backend.api.delayproof.application.port.`in`.DelayProofUseCase
import backend.team.ahachul_backend.api.delayproof.application.port.`in`.command.CreateDelayProofCommand
import backend.team.ahachul_backend.api.delayproof.application.port.`in`.command.GetDelayCenterOverviewCommand
import backend.team.ahachul_backend.api.delayproof.application.port.`in`.command.GetCommunityDelaySignalsCommand
import backend.team.ahachul_backend.api.delayproof.application.port.`in`.command.GetSubwayIncidentsCommand
import backend.team.ahachul_backend.api.train.application.port.`in`.TrainUseCase
import backend.team.ahachul_backend.common.exception.CommonException
import backend.team.ahachul_backend.common.persistence.SubwayLineReader
import backend.team.ahachul_backend.common.properties.DelayProofProperties
import backend.team.ahachul_backend.common.response.ResponseCode
import backend.team.ahachul_backend.common.utils.JwtUtils
import backend.team.ahachul_backend.common.utils.RequestUtils
import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.ExpiredJwtException
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Base64

@Service
@Transactional(readOnly = true)
class DelayProofService(
    private val trainUseCase: TrainUseCase,
    private val communityPostReader: CommunityPostReader,
    private val subwayLineReader: SubwayLineReader,
    private val jwtUtils: JwtUtils,
    private val objectMapper: ObjectMapper,
    private val delayProofProperties: DelayProofProperties,
    private val incidentClient: DelayProofIncidentClient,
) : DelayProofUseCase {

    private val delayKeywords = listOf("지연", "연착", "운행중단", "사고", "고장", "멈춤", "늦")
    private val delayMinuteRegex = Regex("(\\d{1,3})\\s*분")
    private val snippetLimit = 90
    private val reliabilitySlotMinutes = 10

    override fun createDelayProof(command: CreateDelayProofCommand): DelayProofDto.CreateResponse {
        val memberId = RequestUtils.getAttribute(RequestUtils.Attribute.MEMBER_ID)
            ?: throw CommonException(ResponseCode.INVALID_AUTH)
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val nowIso = now.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

        val realtime = trainUseCase.getTrainRealTimesV2(
            stationId = command.stationId,
            subwayLineId = command.subwayLineId,
            upDownType = command.upDownType,
            limit = 2,
        )

        val communitySummary = summarizeCommunitySignals(
            subwayLineId = command.subwayLineId,
            stationId = command.stationId,
            windowMinutes = delayProofProperties.communityWindowMinutes.toInt(),
            limit = delayProofProperties.signalPageSize,
        )
        val (incidentDataSource, officialIncidents) = incidentClient.fetchIncidents(
            subwayLineId = command.subwayLineId,
            stationId = command.stationId,
            limit = 10,
        )

        val grade = DelayProofGradePolicy.resolve(
            hasOfficialIncident = officialIncidents.isNotEmpty(),
            communitySignalCount = communitySummary.signalCount,
            realtimeConfidenceLevel = realtime.confidenceLevel,
            realtimeStale = realtime.isStale,
        )

        val evidenceSummary = DelayProofDto.DelayProofEvidenceSummary(
            official = DelayProofDto.OfficialEvidence(
                matched = officialIncidents.isNotEmpty(),
                eventCount = officialIncidents.size,
                dataSource = incidentDataSource,
                incidents = officialIncidents,
            ),
            community = DelayProofDto.CommunityEvidence(
                signalCount = communitySummary.signalCount,
                distinctAuthors = communitySummary.distinctAuthors,
                medianReportedDelayMin = communitySummary.medianReportedDelayMin,
                confidenceLevel = communitySummary.confidenceLevel,
                signals = communitySummary.signals,
            ),
            realtime = DelayProofDto.RealtimeEvidence(
                isStale = realtime.isStale,
                freshnessSec = realtime.freshnessSec,
                confidenceLevel = realtime.confidenceLevel,
                generatedAt = realtime.generatedAt,
            ),
        )

        val expectedArrivalAt = command.expectedArrivalAt?.takeIf { it.isNotBlank() }
            ?: deriveExpectedArrivalAt(now, realtime)
        val text = buildShareText(
            expectedArrivalAt = expectedArrivalAt,
            officialEventCount = officialIncidents.size,
            communitySignalCount = communitySummary.signalCount,
            generatedAt = nowIso,
            customMessage = command.customMessage,
        )

        val ttlSeconds = delayProofProperties.proofTtlSeconds
        val expiresAt = now.plusSeconds(ttlSeconds).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        val proofPayload = DelayProofPayload(
            memberId = memberId,
            stationId = command.stationId,
            subwayLineId = command.subwayLineId,
            upDownType = command.upDownType?.name,
            issuedAt = nowIso,
            expiresAt = expiresAt,
            grade = grade,
            confidenceLevel = realtime.confidenceLevel,
            evidenceSummary = evidenceSummary,
            text = text,
        )

        val payloadJson = objectMapper.writeValueAsString(proofPayload)
        val signature = Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(payloadJson.toByteArray(StandardCharsets.UTF_8))
        val proofId = jwtUtils.createToken(signature, ttlSeconds)
        val shareUrl = "${delayProofProperties.shareBaseUrl.trimEnd('/')}/proofs/$proofId"

        return DelayProofDto.CreateResponse(
            proofId = proofId,
            issuedAt = nowIso,
            expiresAt = expiresAt,
            grade = grade,
            confidenceLevel = realtime.confidenceLevel,
            evidenceSummary = evidenceSummary,
            text = text,
            shareUrl = shareUrl,
            signature = signature,
        )
    }

    override fun getDelayProof(proofId: String): DelayProofDto.GetResponse {
        try {
            val claims = jwtUtils.verify(proofId)
            val signature = claims.body.subject
            val payloadJson = String(Base64.getUrlDecoder().decode(signature), StandardCharsets.UTF_8)
            val payload = objectMapper.readValue(payloadJson, DelayProofPayload::class.java)
            val shareUrl = "${delayProofProperties.shareBaseUrl.trimEnd('/')}/proofs/$proofId"

            return DelayProofDto.GetResponse(
                proofId = proofId,
                issuedAt = payload.issuedAt,
                expiresAt = payload.expiresAt,
                grade = payload.grade,
                confidenceLevel = payload.confidenceLevel,
                evidenceSummary = payload.evidenceSummary,
                text = payload.text,
                shareUrl = shareUrl,
                signature = signature,
            )
        } catch (_: ExpiredJwtException) {
            throw CommonException(ResponseCode.DELAY_PROOF_EXPIRED)
        } catch (_: Exception) {
            throw CommonException(ResponseCode.DELAY_PROOF_INVALID)
        }
    }

    override fun getSubwayIncidents(command: GetSubwayIncidentsCommand): DelayProofDto.GetSubwayIncidentsResponse {
        val limit = (command.limit ?: 10).coerceIn(1, 100)
        val (dataSource, incidents) = incidentClient.fetchIncidents(
            subwayLineId = command.subwayLineId,
            stationId = command.stationId,
            limit = limit,
        )

        return DelayProofDto.GetSubwayIncidentsResponse(
            generatedAt = OffsetDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
            dataSource = dataSource,
            incidents = incidents,
        )
    }

    override fun getCommunityDelaySignals(command: GetCommunityDelaySignalsCommand): DelayProofDto.GetCommunityDelaySignalsResponse {
        return summarizeCommunitySignals(
            subwayLineId = command.subwayLineId,
            stationId = command.stationId,
            windowMinutes = command.windowMinutes ?: delayProofProperties.communityWindowMinutes.toInt(),
            limit = command.limit ?: delayProofProperties.signalPageSize,
        )
    }

    override fun getDelayCenterOverview(command: GetDelayCenterOverviewCommand): DelayProofDto.GetDelayCenterOverviewResponse {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val generatedAt = now.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

        val realtime = trainUseCase.getTrainRealTimesV2(
            stationId = command.stationId,
            subwayLineId = command.subwayLineId,
            upDownType = command.upDownType,
            limit = 2,
        )

        val communitySummary = summarizeCommunitySignals(
            subwayLineId = command.subwayLineId,
            stationId = command.stationId,
            windowMinutes = command.windowMinutes ?: delayProofProperties.communityWindowMinutes.toInt(),
            limit = command.signalLimit ?: delayProofProperties.signalPageSize,
        )

        val (officialDataSource, incidents) = incidentClient.fetchIncidents(
            subwayLineId = command.subwayLineId,
            stationId = command.stationId,
            limit = command.incidentLimit ?: 10,
        )

        val gradePreview = DelayProofGradePolicy.resolve(
            hasOfficialIncident = incidents.isNotEmpty(),
            communitySignalCount = communitySummary.signalCount,
            realtimeConfidenceLevel = realtime.confidenceLevel,
            realtimeStale = realtime.isStale,
        )

        val realtimeTop = realtime.trainRealTimes.firstOrNull()
        val activeEventCount = incidents.count { it.resolvedAt.isNullOrBlank() }
        val estimatedDelayMin = estimateDelayMinutes(
            realtimeEtaMin = realtimeTop?.etaMinDisplay,
            communityMedianDelayMin = communitySummary.medianReportedDelayMin,
            incidents = incidents,
        )
        val recommendedExpectedArrivalAt = now.plusMinutes(estimatedDelayMin.toLong())
            .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        val recommendedMessage = buildShareText(
            expectedArrivalAt = recommendedExpectedArrivalAt,
            officialEventCount = incidents.size,
            communitySignalCount = communitySummary.signalCount,
            generatedAt = generatedAt,
            customMessage = null,
        )

        return DelayProofDto.GetDelayCenterOverviewResponse(
            generatedAt = generatedAt,
            stationId = command.stationId,
            subwayLineId = command.subwayLineId,
            upDownType = command.upDownType,
            realtime = DelayProofDto.DelayCenterRealtime(
                generatedAt = realtime.generatedAt,
                dataSource = realtime.dataSource,
                isStale = realtime.isStale,
                freshnessSec = realtime.freshnessSec,
                confidenceLevel = realtime.confidenceLevel,
                etaSec = realtimeTop?.etaSec,
                etaMinDisplay = realtimeTop?.etaMinDisplay,
                destinationStationDirection = realtimeTop?.destinationStationDirection,
                nextStationDirection = realtimeTop?.nextStationDirection,
            ),
            official = DelayProofDto.DelayCenterOfficial(
                dataSource = officialDataSource,
                eventCount = incidents.size,
                activeEventCount = activeEventCount,
                incidents = incidents,
            ),
            community = DelayProofDto.CommunityEvidence(
                signalCount = communitySummary.signalCount,
                distinctAuthors = communitySummary.distinctAuthors,
                medianReportedDelayMin = communitySummary.medianReportedDelayMin,
                confidenceLevel = communitySummary.confidenceLevel,
                signals = communitySummary.signals,
            ),
            recommendation = DelayProofDto.DelayCenterRecommendation(
                gradePreview = gradePreview,
                confidenceLevel = realtime.confidenceLevel,
                estimatedDelayMin = estimatedDelayMin,
                recommendedExpectedArrivalAt = recommendedExpectedArrivalAt,
                recommendedMessage = recommendedMessage,
            ),
        )
    }

    private fun summarizeCommunitySignals(
        subwayLineId: Long,
        stationId: Long?,
        windowMinutes: Int,
        limit: Int,
    ): DelayProofDto.GetCommunityDelaySignalsResponse {
        val normalizedWindowMinutes = windowMinutes.coerceIn(5, 120)
        val normalizedLimit = limit.coerceIn(5, 200)

        val posts = communityPostReader.searchCommunityPosts(
            GetSliceCommunityPostCommand(
                categoryType = null,
                subwayLines = listOf(subwayLineReader.getById(subwayLineId)),
                stationId = stationId,
                content = null,
                hashTag = null,
                writer = null,
                sort = Sort.by(Sort.Direction.DESC, "createdAt"),
                date = null,
                communityPostId = null,
                pageSize = normalizedLimit,
            ),
        )

        val cutoff = LocalDateTime.now().minusMinutes(normalizedWindowMinutes.toLong())
        val matchedSignals = posts
            .filter { it.createdAt >= cutoff }
            .mapNotNull { post ->
                val signal = toDelaySignal(post) ?: return@mapNotNull null
                MatchedDelaySignal(
                    createdAt = post.createdAt,
                    writer = post.writer,
                    signal = signal,
                )
            }

        val distinctAuthors = matchedSignals.map { it.writer }.toSet().size
        val delayMinutes = matchedSignals.mapNotNull { it.signal.reportedDelayMin }.sorted()
        val median = delayMinutes.takeIf { it.isNotEmpty() }?.let { values ->
            values[values.size / 2]
        }
        val peakSlotMetric = matchedSignals
            .groupBy { toSlotKey(it.createdAt, reliabilitySlotMinutes) }
            .values
            .map { groupedSignals ->
                SlotMetric(
                    signalCount = groupedSignals.size,
                    distinctAuthors = groupedSignals.map { it.writer }.toSet().size,
                )
            }
            .maxWithOrNull(compareBy<SlotMetric> { it.signalCount }.thenBy { it.distinctAuthors })
            ?: SlotMetric.EMPTY

        val confidence = when {
            matchedSignals.size >= 10 && distinctAuthors >= 5 -> "HIGH"
            matchedSignals.size >= 4 && distinctAuthors >= 2 -> "MEDIUM"
            else -> "LOW"
        }
        val reliabilityBadgeLevel = when {
            peakSlotMetric.signalCount >= 6 && peakSlotMetric.distinctAuthors >= 4 -> "SPIKE"
            peakSlotMetric.signalCount >= 3 && peakSlotMetric.distinctAuthors >= 2 -> "ELEVATED"
            else -> "NONE"
        }

        return DelayProofDto.GetCommunityDelaySignalsResponse(
            generatedAt = OffsetDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
            subwayLineId = subwayLineId,
            stationId = stationId,
            windowMinutes = normalizedWindowMinutes,
            timeSlotMinutes = reliabilitySlotMinutes,
            signalCount = matchedSignals.size,
            distinctAuthors = distinctAuthors,
            medianReportedDelayMin = median,
            confidenceLevel = confidence,
            reliabilityBadgeLevel = reliabilityBadgeLevel,
            sameTimeSlotSignalCount = peakSlotMetric.signalCount,
            sameTimeSlotDistinctAuthors = peakSlotMetric.distinctAuthors,
            signals = matchedSignals.map { it.signal },
        )
    }

    private fun toSlotKey(createdAt: LocalDateTime, slotMinutes: Int): LocalDateTime {
        val flooredMinute = (createdAt.minute / slotMinutes) * slotMinutes
        return createdAt
            .withMinute(flooredMinute)
            .withSecond(0)
            .withNano(0)
    }

    private fun toDelaySignal(post: SearchCommunityPost): DelayProofDto.CommunityDelaySignal? {
        val sourceText = "${post.title} ${post.content}"
        val matchedKeyword = delayKeywords.firstOrNull { sourceText.contains(it) } ?: return null
        val minutes = delayMinuteRegex.find(sourceText)?.groupValues?.get(1)?.toIntOrNull()
        val snippet = sourceText.replace("\n", " ").trim().take(snippetLimit)

        return DelayProofDto.CommunityDelaySignal(
            postId = post.id,
            createdAt = post.createdAt.atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
            writer = post.writer,
            matchedKeyword = matchedKeyword,
            reportedDelayMin = minutes,
            snippet = snippet,
        )
    }

    private data class MatchedDelaySignal(
        val createdAt: LocalDateTime,
        val writer: String,
        val signal: DelayProofDto.CommunityDelaySignal,
    )

    private data class SlotMetric(
        val signalCount: Int,
        val distinctAuthors: Int,
    ) {
        companion object {
            val EMPTY = SlotMetric(signalCount = 0, distinctAuthors = 0)
        }
    }

    private fun deriveExpectedArrivalAt(now: OffsetDateTime, realtime: backend.team.ahachul_backend.api.train.adapter.`in`.dto.GetTrainRealTimesV2Dto.Response): String {
        val etaSec = realtime.trainRealTimes.firstOrNull()?.etaSec?.coerceAtLeast(0) ?: 0
        return now.plusSeconds(etaSec.toLong()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }

    private fun buildShareText(
        expectedArrivalAt: String,
        officialEventCount: Int,
        communitySignalCount: Int,
        generatedAt: String,
        customMessage: String?,
    ): String {
        val base = "지하철 지연으로 ${expectedArrivalAt.take(16).replace('T', ' ')} 도착예정입니다."
        val evidence = "공식공지 ${officialEventCount}건/동일 호선 커뮤니티 ${communitySignalCount}건 확인(${generatedAt.take(16).replace('T', ' ')} 생성)."
        val suffix = customMessage?.trim().takeUnless { it.isNullOrBlank() } ?: "최대한 빨리 가겠습니다."
        return "$base $evidence $suffix"
    }

    private fun estimateDelayMinutes(
        realtimeEtaMin: Int?,
        communityMedianDelayMin: Int?,
        incidents: List<DelayProofDto.OfficialIncident>,
    ): Int {
        val base = maxOf(realtimeEtaMin ?: 0, communityMedianDelayMin ?: 0, 1)
        if (incidents.isEmpty()) {
            return base
        }

        val incidentPenalty = incidents.maxOfOrNull { incident ->
            when (incident.severity.uppercase()) {
                "SUSPENDED", "STOP", "MAJOR" -> 15
                "DELAY", "MINOR" -> 7
                else -> 5
            }
        } ?: 5

        return base + incidentPenalty
    }

    data class DelayProofPayload(
        val memberId: String,
        val stationId: Long,
        val subwayLineId: Long,
        val upDownType: String?,
        val issuedAt: String,
        val expiresAt: String,
        val grade: String,
        val confidenceLevel: String,
        val evidenceSummary: DelayProofDto.DelayProofEvidenceSummary,
        val text: String,
    ) {
        constructor() : this(
            memberId = "",
            stationId = 0L,
            subwayLineId = 0L,
            upDownType = null,
            issuedAt = "",
            expiresAt = "",
            grade = "",
            confidenceLevel = "",
            evidenceSummary = DelayProofDto.DelayProofEvidenceSummary(
                official = DelayProofDto.OfficialEvidence(false, 0, "", emptyList()),
                community = DelayProofDto.CommunityEvidence(0, 0, null, "LOW", emptyList()),
                realtime = DelayProofDto.RealtimeEvidence(false, 0, "LOW", ""),
            ),
            text = "",
        )
    }
}
