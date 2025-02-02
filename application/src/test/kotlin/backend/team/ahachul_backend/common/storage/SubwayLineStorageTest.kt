package backend.team.ahachul_backend.common.storage

import backend.team.ahachul_backend.config.controller.CommonServiceTestConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class SubwayLineStorageTest(
    @Autowired private val subwayLineStorage: SubwayLineStorage
): CommonServiceTestConfig() {

    @Test
    @DisplayName("호선 정보만 추출해서 반환한다.")
    fun extractSubwayLine() {
        // given
        val subwayLineName = "숭실대입구역(7호선)"

        // when
        val result = subwayLineStorage.extractSubWayLine(subwayLineName)

        // then
        assertThat(result).isEqualTo("7호선")
    }

    @Test
    @DisplayName("시드 호선과 일치하는 호선 정보가 없다면 null을 반환한다.")
    fun getSubwayLineNull() {
        // given
        val subwayLineName = "인천국제공항(터미널1)"

        // when
        val result = subwayLineStorage.getSubwayLineEntityByName(subwayLineName)

        // then
        assertThat(result).isEqualTo(null)
    }
}
