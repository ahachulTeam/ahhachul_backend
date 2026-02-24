package backend.team.ahachul_backend.api.delayproof.adapter.`in`.dto

import backend.team.ahachul_backend.api.delayproof.application.port.`in`.command.CreateDelayProofCommand
import backend.team.ahachul_backend.api.delayproof.application.port.`in`.command.GetCommunityDelaySignalsCommand
import backend.team.ahachul_backend.api.delayproof.application.port.`in`.command.GetSubwayIncidentsCommand
import backend.team.ahachul_backend.api.train.domain.model.UpDownType

class DelayProofDto {

    data class CreateRequest(
        val stationId: Long,
        val subwayLineId: Long,
        val upDownType: UpDownType?,
        val expectedArrivalAt: String?,
        val customMessage: String?,
    ) {
        fun toCommand(): CreateDelayProofCommand {
            return CreateDelayProofCommand(
                stationId = stationId,
                subwayLineId = subwayLineId,
                upDownType = upDownType,
                expectedArrivalAt = expectedArrivalAt,
                customMessage = customMessage,
            )
        }
    }

    data class GetSubwayIncidentsRequest(
        val subwayLineId: Long,
        val stationId: Long?,
        val limit: Int?,
    ) {
        fun toCommand(): GetSubwayIncidentsCommand {
            return GetSubwayIncidentsCommand(
                subwayLineId = subwayLineId,
                stationId = stationId,
                limit = limit,
            )
        }
    }

    data class GetCommunityDelaySignalsRequest(
        val subwayLineId: Long,
        val windowMinutes: Int?,
        val limit: Int?,
    ) {
        fun toCommand(): GetCommunityDelaySignalsCommand {
            return GetCommunityDelaySignalsCommand(
                subwayLineId = subwayLineId,
                windowMinutes = windowMinutes,
                limit = limit,
            )
        }
    }

    data class GetSubwayIncidentsResponse(
        val generatedAt: String,
        val dataSource: String,
        val incidents: List<OfficialIncident>,
    )

    data class GetCommunityDelaySignalsResponse(
        val generatedAt: String,
        val subwayLineId: Long,
        val windowMinutes: Int,
        val signalCount: Int,
        val distinctAuthors: Int,
        val medianReportedDelayMin: Int?,
        val confidenceLevel: String,
        val signals: List<CommunityDelaySignal>,
    )

    data class CreateResponse(
        val proofId: String,
        val issuedAt: String,
        val expiresAt: String,
        val grade: String,
        val confidenceLevel: String,
        val evidenceSummary: DelayProofEvidenceSummary,
        val text: String,
        val shareUrl: String,
        val signature: String,
    )

    data class GetResponse(
        val proofId: String,
        val issuedAt: String,
        val expiresAt: String,
        val grade: String,
        val confidenceLevel: String,
        val evidenceSummary: DelayProofEvidenceSummary,
        val text: String,
        val shareUrl: String,
        val signature: String,
    )

    data class DelayProofEvidenceSummary(
        val official: OfficialEvidence,
        val community: CommunityEvidence,
        val realtime: RealtimeEvidence,
    )

    data class OfficialEvidence(
        val matched: Boolean,
        val eventCount: Int,
        val dataSource: String,
        val incidents: List<OfficialIncident>,
    )

    data class CommunityEvidence(
        val signalCount: Int,
        val distinctAuthors: Int,
        val medianReportedDelayMin: Int?,
        val confidenceLevel: String,
        val signals: List<CommunityDelaySignal>,
    )

    data class RealtimeEvidence(
        val isStale: Boolean,
        val freshnessSec: Int,
        val confidenceLevel: String,
        val generatedAt: String,
    )

    data class OfficialIncident(
        val eventId: String,
        val occurredAt: String,
        val resolvedAt: String?,
        val severity: String,
        val title: String,
        val description: String,
        val source: String,
        val sourceUrl: String?,
    )

    data class CommunityDelaySignal(
        val postId: Long,
        val createdAt: String,
        val writer: String,
        val matchedKeyword: String,
        val reportedDelayMin: Int?,
        val snippet: String,
    )
}
