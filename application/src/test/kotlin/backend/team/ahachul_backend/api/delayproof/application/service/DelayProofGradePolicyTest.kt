package backend.team.ahachul_backend.api.delayproof.application.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DelayProofGradePolicyTest {

    @Test
    fun 공식공지와_실시간_high가_일치하면_A를_반환한다() {
        val grade = DelayProofGradePolicy.resolve(
            hasOfficialIncident = true,
            communitySignalCount = 0,
            realtimeConfidenceLevel = "HIGH",
            realtimeStale = false,
        )

        assertThat(grade).isEqualTo("A")
    }

    @Test
    fun 공식공지_없이_커뮤니티_신호가_충분하고_실시간이_신선하면_B를_반환한다() {
        val grade = DelayProofGradePolicy.resolve(
            hasOfficialIncident = false,
            communitySignalCount = 6,
            realtimeConfidenceLevel = "MEDIUM",
            realtimeStale = false,
        )

        assertThat(grade).isEqualTo("B")
    }

    @Test
    fun 그외_조건은_C를_반환한다() {
        val grade = DelayProofGradePolicy.resolve(
            hasOfficialIncident = false,
            communitySignalCount = 1,
            realtimeConfidenceLevel = "LOW",
            realtimeStale = true,
        )

        assertThat(grade).isEqualTo("C")
    }
}
