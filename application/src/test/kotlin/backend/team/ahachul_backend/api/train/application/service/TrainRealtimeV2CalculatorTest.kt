package backend.team.ahachul_backend.api.train.application.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime

class TrainRealtimeV2CalculatorTest {

    @Test
    fun 수신시각_기반으로_freshness와_confidence를_계산한다() {
        val generatedAt = OffsetDateTime.parse("2026-02-23T23:00:00+09:00")

        val meta = TrainRealtimeV2Calculator.resolveMeta(
            generatedAt = generatedAt,
            recptnAtRawList = listOf("2026-02-23 22:59:15", "2026-02-23 22:58:10"),
        )

        assertThat(meta.freshnessSec).isEqualTo(45)
        assertThat(meta.confidenceLevel).isEqualTo("HIGH")
    }

    @Test
    fun freshness_구간별_confidence_계산을_적용한다() {
        val generatedAt = OffsetDateTime.parse("2026-02-23T23:00:00+09:00")

        val mediumMeta = TrainRealtimeV2Calculator.resolveMeta(
            generatedAt = generatedAt,
            recptnAtRawList = listOf("2026-02-23 22:58:40"),
        )
        val lowMeta = TrainRealtimeV2Calculator.resolveMeta(
            generatedAt = generatedAt,
            recptnAtRawList = listOf("2026-02-23 22:55:00"),
        )

        assertThat(mediumMeta.freshnessSec).isEqualTo(80)
        assertThat(mediumMeta.confidenceLevel).isEqualTo("MEDIUM")
        assertThat(lowMeta.freshnessSec).isEqualTo(300)
        assertThat(lowMeta.confidenceLevel).isEqualTo("LOW")
    }

    @Test
    fun eta_계산시_freshness를_차감하고_0미만은_보정한다() {
        val etaSec = TrainRealtimeV2Calculator.calculateEtaSec(rawEtaSec = 70, freshnessSec = 15)
        val boundedEtaSec = TrainRealtimeV2Calculator.calculateEtaSec(rawEtaSec = 70, freshnessSec = 120)

        assertThat(etaSec).isEqualTo(55)
        assertThat(TrainRealtimeV2Calculator.calculateEtaMinDisplay(etaSec)).isEqualTo(1)

        assertThat(boundedEtaSec).isEqualTo(0)
        assertThat(TrainRealtimeV2Calculator.calculateEtaMinDisplay(boundedEtaSec)).isEqualTo(0)
    }
}
