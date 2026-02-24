package backend.team.ahachul_backend.api.delayproof.application.service

object DelayProofGradePolicy {

    fun resolve(
        hasOfficialIncident: Boolean,
        communitySignalCount: Int,
        realtimeConfidenceLevel: String,
        realtimeStale: Boolean,
    ): String {
        if (hasOfficialIncident && !realtimeStale && realtimeConfidenceLevel == "HIGH") {
            return "A"
        }
        if (!hasOfficialIncident && communitySignalCount >= 4 && !realtimeStale) {
            return "B"
        }
        return "C"
    }
}
